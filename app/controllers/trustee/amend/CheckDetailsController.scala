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

package controllers.trustee.amend

import java.time.LocalDate

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.trustee.actions.NameRequiredAction
import javax.inject.Inject
import models.IndividualOrBusiness._
import models.{Address, CombinedPassportOrIdCard, IdCard, IndividualIdentification, IndividualOrBusiness, NationalInsuranceNumber, NonUkAddress, Passport, TrustIdentificationOrgType, Trustee, TrusteeIndividual, TrusteeOrganisation, UkAddress, UserAnswers}
import navigation.Navigator
import pages.trustee.amend.{individual => ind, organisation => org}
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.{AmendedTrusteeBuilder, TrustService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.checkYourAnswers.{AmendTrusteeIndividualPrintHelper, AmendTrusteeOrganisationPrintHelper}
import views.html.trustee.amend.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        navigator: Navigator,
                                        trust: TrustService,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        indHelper: AmendTrusteeIndividualPrintHelper,
                                        orgHelper: AmendTrusteeOrganisationPrintHelper,
                                        trusteeBuilder: AmendedTrusteeBuilder,
                                        trustConnector: TrustConnector,
                                        nameAction: NameRequiredAction,
                                        val appConfig: FrontendAppConfig
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getTrustee(request.userAnswers.utr, index).flatMap {
        case ind: TrusteeIndividual =>
          val answers = populateUserAnswers(request.userAnswers, ind, index)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val section = indHelper(updatedAnswers, ind.name.displayName)
            Ok(view(section, index))
          }

        case org: TrusteeOrganisation =>
          val answers = populateUserAnswers(request.userAnswers, org, index)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val section = orgHelper(updatedAnswers, org.name)
            Ok(view(section, index))
          }
      }
  }

  def onPageLoadUpdated(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      request.userAnswers.get(IndividualOrBusinessPage) map {
        case Individual =>
          val section = indHelper(request.userAnswers, request.trusteeName)
          Ok(view(section, index))
        case Business =>
          val section = orgHelper(request.userAnswers, request.trusteeName)
          Ok(view(section, index))
      } getOrElse Redirect(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
      request.userAnswers.get(WhenAddedPage).fold {
        Future.successful(Redirect(controllers.trustee.routes.WhenAddedController.onPageLoad()))
      } {
        date =>
          (request.userAnswers.get(IndividualOrBusinessPage) map {
            case IndividualOrBusiness.Individual =>
              val trusteeInd: TrusteeIndividual = trusteeBuilder.createTrusteeIndividual(request.userAnswers, date)
              amendTrustee(request.userAnswers, trusteeInd, index)
            case IndividualOrBusiness.Business =>
              val trusteeOrg: TrusteeOrganisation = trusteeBuilder.createTrusteeOrganisation(request.userAnswers, date)
              amendTrustee(request.userAnswers, trusteeOrg, index)
          }).getOrElse(Future.successful(InternalServerError))
      }
  }

  private def amendTrustee(userAnswers: UserAnswers, t: Trustee, index: Int)(implicit hc: HeaderCarrier) = {
    for {
      _ <- trustConnector.amendTrustee(userAnswers.utr, index, t)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.trustee.basePath))
      _ <- sessionRepository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }

  private def populateUserAnswers(userAnswers: UserAnswers, trustee: TrusteeIndividual, index: Int) = {
    userAnswers.deleteAtPath(pages.trustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Individual))
      .flatMap(_.set(ind.IndexPage, index))
      .flatMap(_.set(ind.NamePage, trustee.name))
      .flatMap(answers => extractDateOfBirth(trustee.dateOfBirth, answers))
      .flatMap(answers => extractIndAddress(trustee.address, answers))
      .flatMap(answers => extractIndIdentification(trustee.identification, answers))
      .flatMap(_.set(WhenAddedPage, trustee.entityStart))
  }

  private def populateUserAnswers(userAnswers: UserAnswers, trustee: TrusteeOrganisation, index: Int) = {
    userAnswers.deleteAtPath(pages.trustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Business))
      .flatMap(_.set(org.IndexPage, index))
      .flatMap(_.set(org.NamePage, trustee.name))
      .flatMap(answers => extractOrgIdentification(trustee.identification, answers))
      .flatMap(_.set(WhenAddedPage, trustee.entityStart))
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(ind.DateOfBirthYesNoPage, true)
          .flatMap(_.set(ind.DateOfBirthPage, dob))
      case _ =>
        answers.set(ind.DateOfBirthYesNoPage, false)
    }
  }

  private def extractIndIdentification(identification: Option[IndividualIdentification], answers: UserAnswers) = {
    identification match {

      case Some(NationalInsuranceNumber(nino)) =>
        answers.set(ind.NationalInsuranceNumberYesNoPage, true)
          .flatMap(_.set(ind.NationalInsuranceNumberPage, nino))

      case Some(p:Passport) =>
        answers.set(ind.NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
          .flatMap(_.set(ind.PassportOrIdCardDetailsPage, p.asCombined))

      case Some(id:IdCard) =>
        answers.set(ind.NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
          .flatMap(_.set(ind.PassportOrIdCardDetailsPage, id.asCombined))

      case Some(c:CombinedPassportOrIdCard) =>
        answers.set(ind.NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
          .flatMap(_.set(ind.PassportOrIdCardDetailsPage, c))

      case _ =>
        answers.set(ind.NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, false))

    }
  }

  private def extractOrgIdentification(identification: Option[TrustIdentificationOrgType], answers: UserAnswers) = {
    identification match {

      case Some(TrustIdentificationOrgType(_, Some(utr), None)) =>
        answers.set(org.UtrYesNoPage, true)
          .flatMap(_.set(org.UtrPage, utr))

      case Some(TrustIdentificationOrgType(_, None, Some(address))) =>
        answers.set(org.UtrYesNoPage, false)
          .flatMap(answers => extractOrgAddress(address, answers))

      case _ =>
        answers.set(org.UtrYesNoPage, false)
          .flatMap(_.set(ind.AddressYesNoPage, false))

    }
  }

  private def extractIndAddress(address: Option[Address], answers: UserAnswers) = {
    address match {
      case Some(uk: UkAddress) =>
        answers.set(ind.AddressYesNoPage, true)
          .flatMap(_.set(ind.LiveInTheUkYesNoPage, true))
          .flatMap(_.set(ind.UkAddressPage, uk))
      case Some(nonUk: NonUkAddress) =>
        answers.set(ind.AddressYesNoPage, true)
          .flatMap(_.set(ind.LiveInTheUkYesNoPage, false))
          .flatMap(_.set(ind.NonUkAddressPage, nonUk))
      case _ =>
        answers.set(ind.AddressYesNoPage, false)
    }
  }

  private def extractOrgAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(org.AddressYesNoPage, true)
          .flatMap(_.set(org.AddressInTheUkYesNoPage, true))
          .flatMap(_.set(org.UkAddressPage, uk))
      case nonUk: NonUkAddress =>
        answers.set(org.AddressYesNoPage, true)
          .flatMap(_.set(org.AddressInTheUkYesNoPage, false))
          .flatMap(_.set(org.NonUkAddressPage, nonUk))
    }
  }
}
