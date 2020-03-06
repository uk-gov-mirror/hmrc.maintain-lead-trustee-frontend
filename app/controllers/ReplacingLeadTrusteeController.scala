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
import models.{Address, AllTrustees, LeadTrustee, LeadTrusteeIndividual, LeadTrusteeOrganisation, NonUkAddress, Passport, TrustIdentification, TrusteeIndividual, TrusteeOrganisation, UkAddress, UserAnswers}
import navigation.Navigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.RadioOption
import views.html.ReplacingLeadTrusteeView
import mapping.PlaybackImplicits._
import pages.leadtrustee.individual._

import scala.concurrent.ExecutionContext
import scala.util.{Success, Try}

class ReplacingLeadTrusteeController @Inject()(
                                                override val messagesApi: MessagesApi,
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

      trust.getAllTrustees(request.userAnswers.utr) map {
        case AllTrustees(leadTrustee, trustees) =>
          val trusteeNames = trustees.map {
            case ind: TrusteeIndividual => ind.name.displayName
            case org: TrusteeOrganisation => org.name
          }.zipWithIndex.map(
            x => RadioOption(s"replacingLeadTrustee.${x._2}", s"${x._2}", x._1)
          ) :+ RadioOption("replacingLeadTrustee.-1", "-1", "replacingLeadTrustee.add-new")

          form.bindFromRequest().fold(
            formWithErrors =>
              BadRequest(view(formWithErrors, getLeadTrusteeName(leadTrustee), trusteeNames)),
            value => {
              value.toInt match {
                case -1 =>
                  Redirect(controllers.leadtrustee.routes.IndividualOrBusinessController.onPageLoad())
                case index =>
                  trustees(index) match {
                    case ind: TrusteeIndividual =>
                      request.userAnswers.set(IndexPage, index)
                        .flatMap(_.set(NamePage, ind.name))
                        .flatMap(answers => extractDateOfBirth(ind.dateOfBirth, answers))
                        // TODO: uk citizen, nino, passport/id card, lives in uk, address
                        .flatMap(_.set(EmailAddressYesNoPage, false))
                        .flatMap(answers => extractTelephoneNumber(ind.phoneNumber, answers))

                      // TODO: redirect to lead trustee ind name page
                    case org: TrusteeOrganisation => ???
                      // TODO: set user answers and redirect to is UK registered business?
                  }
                  Redirect(controllers.routes.ReplacingLeadTrusteeController.onPageLoad())
              }
            }
          )
      }
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(DateOfBirthPage, dob)
      case _ =>
        Success(answers)
    }
  }

  private def extractIdentification(trustee: TrusteeIndividual, answers: UserAnswers) = {
    trustee.identification map {

      case TrustIdentification(_, Some(nino), None, None) =>
        answers.set(UkCitizenPage, true)
          .flatMap(_.set(NationalInsuranceNumberPage, nino))

      case TrustIdentification(_, None, Some(passport), Some(address)) =>
        answers.set(UkCitizenPage, false)
        // TODO: set address and passport. Passport and ID card will need shared view

    } getOrElse {
      ???
    }
  }

  private def extractPassportOrIdCard(passport: Passport, answers: UserAnswers) = {
    ???
  }

  private def extractAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(UkAddressPage, uk)
          .flatMap(_.set(LiveInTheUkYesNoPage, true))
      case nonUk: NonUkAddress =>
        answers.set(NonUkAddressPage, nonUk)
          .flatMap(_.set(LiveInTheUkYesNoPage, false))
    }
  }

  private def extractTelephoneNumber(phoneNumber: Option[String], answers: UserAnswers) = {
    phoneNumber match {
      case Some(tel) =>
        answers.set(TelephoneNumberPage, tel)
      case _ =>
        Success(answers)
    }
  }

  private def getLeadTrusteeName(leadTrustee: Option[LeadTrustee]): String = {
    leadTrustee match {
      case Some(ltInd: LeadTrusteeIndividual) => ltInd.name.displayName
      case Some(ltOrg: LeadTrusteeOrganisation) => ltOrg.name
      case None => ???
    }
  }
}
