/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import java.time.LocalDate

import controllers.actions.StandardActionSets
import forms.ReplaceLeadTrusteeFormProvider
import javax.inject.Inject
import mapping.PlaybackImplicits._
import models.IndividualOrBusiness._
import models.requests.DataRequest
import models.{Address, AllTrustees, LeadTrustee, LeadTrusteeIndividual, LeadTrusteeOrganisation, NonUkAddress, TrustIdentification, TrustIdentificationOrgType, TrusteeIndividual, TrusteeOrganisation, UkAddress, UserAnswers}
import navigation.Navigator
import pages.leadtrustee.organisation.UtrPage
import pages.leadtrustee.{IndividualOrBusinessPage, individual => ltind, organisation => ltorg}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.RadioOption
import views.html.ReplacingLeadTrusteeView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class ReplacingLeadTrusteeController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                playbackRepository: PlaybackRepository,
                                                sessionRepository: PlaybackRepository,
                                                navigator: Navigator,
                                                trust: TrustService,
                                                standardActionSets: StandardActionSets,
                                                formProvider: ReplaceLeadTrusteeFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ReplacingLeadTrusteeView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("replacingLeadTrustee")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getAllTrustees(request.userAnswers.utr) map {
        case AllTrustees(leadTrustee, trustees) =>
          val trusteeNames = trustees.map {
            case ind: TrusteeIndividual => ind.name.displayName
            case org: TrusteeOrganisation => org.name
          }.zipWithIndex.map(
            x => RadioOption(s"replacingLeadTrustee.${x._2}", s"${x._2}", x._1)
          ) :+ RadioOption("replacingLeadTrustee.-1", "-1", "replacingLeadTrustee.add-new")

          Ok(view(form, getLeadTrusteeName(leadTrustee), trusteeNames))
      }
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getAllTrustees(request.userAnswers.utr) flatMap {
        case AllTrustees(leadTrustee, trustees) =>
          val trusteeNames = trustees.map {
            case ind: TrusteeIndividual => ind.name.displayName
            case org: TrusteeOrganisation => org.name
          }.zipWithIndex.map(
            x => RadioOption(s"replacingLeadTrustee.${x._2}", s"${x._2}", x._1)
          ) :+ RadioOption("replacingLeadTrustee.-1", "-1", "replacingLeadTrustee.add-new")

          form.bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, getLeadTrusteeName(leadTrustee), trusteeNames))),
            value => {
              value.toInt match {
                case -1 =>
                  Future.successful(Redirect(controllers.leadtrustee.routes.IndividualOrBusinessController.onPageLoad()))
                case index =>
                  trustees(index) match {
                    case ind: TrusteeIndividual =>
                      for {
                        updatedAnswers <- Future.fromTry(
                          request.userAnswers.set(IndividualOrBusinessPage, Individual)
                            .flatMap(_.set(ltind.IndexPage, index))
                            .flatMap(_.set(ltind.NamePage, ind.name))
                            .flatMap(answers => extractDateOfBirth(ind.dateOfBirth, answers))
                            .flatMap(answers => extractIndIdentification(ind.identification, answers))
                            .flatMap(_.set(ltind.EmailAddressYesNoPage, false))
                            .flatMap(answers => extractIndTelephoneNumber(ind.phoneNumber, answers))
                        )
                        _ <- playbackRepository.set(updatedAnswers)
                      } yield Redirect(controllers.leadtrustee.individual.routes.NeedToAnswerQuestionsController.onPageLoad())

                    case org: TrusteeOrganisation =>
                      for {
                        updatedAnswers <- Future.fromTry(
                          request.userAnswers.set(IndividualOrBusinessPage, Business)
                            .flatMap(_.set(ltorg.IndexPage, index))
                            .flatMap(answers => extractOrgIdentification(org.identification, answers))
                            .flatMap(_.set(ltorg.NamePage, org.name))
                            .flatMap(answers => extractOrgEmail(org.email, answers))
                            .flatMap(answers => extractOrgTelephoneNumber(org.phoneNumber, answers))
                        )
                        _ <- playbackRepository.set(updatedAnswers)
                      } yield Redirect(controllers.leadtrustee.organisation.routes.NeedToAnswerQuestionsController.onPageLoad())
                  }
              }
            }
          )
      }
  }

  private def getLeadTrusteeName(leadTrustee: Option[LeadTrustee])(implicit request: DataRequest[AnyContent]): String = {
    leadTrustee match {
      case Some(ltInd: LeadTrusteeIndividual) => ltInd.name.displayName
      case Some(ltOrg: LeadTrusteeOrganisation) => ltOrg.name
      case None => request.messages(messagesApi)("leadTrusteeName.defaultText")
    }
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(ltind.DateOfBirthPage, dob)
      case _ =>
        Success(answers)
    }
  }

  private def extractIndIdentification(identification: Option[TrustIdentification], answers: UserAnswers) = {
    identification map {

      case TrustIdentification(_, Some(nino), None, None) =>
        answers.set(ltind.UkCitizenPage, true)
          .flatMap(_.set(ltind.NationalInsuranceNumberPage, nino))

      case TrustIdentification(_, None, Some(passport), Some(address)) =>
        answers.set(ltind.UkCitizenPage, false)
          .flatMap(answers => extractIndAddress(address.convert, answers))
          .flatMap(_.set(ltind.PassportOrIdCardDetailsPage, passport.asCombined))

      case TrustIdentification(_, None, None, Some(address)) =>
        extractIndAddress(address.convert, answers)

      case _ => Success(answers)

    } getOrElse {
      Success(answers)
    }
  }

  private def extractIndAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(ltind.LiveInTheUkYesNoPage, true)
          .flatMap(_.set(ltind.UkAddressPage, uk))
      case nonUk: NonUkAddress =>
        answers.set(ltind.LiveInTheUkYesNoPage, false)
          .flatMap(_.set(ltind.NonUkAddressPage, nonUk))
    }
  }

  private def extractIndTelephoneNumber(phoneNumber: Option[String], answers: UserAnswers) = {
    phoneNumber match {
      case Some(tel) =>
        answers.set(ltind.TelephoneNumberPage, tel)
      case _ =>
        Success(answers)
    }
  }

  private def extractOrgIdentification(identification: Option[TrustIdentificationOrgType], answers: UserAnswers) = {
    identification map {
      case TrustIdentificationOrgType(_, Some(utr), None) =>
        answers.set(ltorg.RegisteredInUkYesNoPage, true)
          .flatMap(_.set(UtrPage, utr))
      case TrustIdentificationOrgType(_, None, Some(address)) =>
        answers.set(ltorg.RegisteredInUkYesNoPage, false)
          .flatMap(answers => extractOrgAddress(address.convert, answers))
      case _ => Success(answers)
    } getOrElse {
      Success(answers)
    }
  }

  private def extractOrgAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(ltorg.AddressInTheUkYesNoPage, true)
          .flatMap(_.set(ltorg.UkAddressPage, uk))
      case nonUk: NonUkAddress =>
        answers.set(ltorg.AddressInTheUkYesNoPage, false)
          .flatMap(_.set(ltorg.NonUkAddressPage, nonUk))

    }
  }

  private def extractOrgEmail(emailAddress: Option[String], answers: UserAnswers) = {
    emailAddress match {
      case Some(email) =>
        answers.set(ltorg.EmailAddressYesNoPage, true)
          .flatMap(_.set(ltorg.EmailAddressPage, email))
      case _ =>
        answers.set(ltorg.EmailAddressYesNoPage, false)
    }
  }

  private def extractOrgTelephoneNumber(phoneNumber: Option[String], answers: UserAnswers) = {
    phoneNumber match {
      case Some(tel) =>
        answers.set(ltorg.TelephoneNumberPage, tel)
      case _ =>
        Success(answers)
    }
  }

}