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

package controllers.leadtrustee

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.ReturnToStart
import controllers.actions.{LeadTrusteeNameRequest, StandardActionSets}
import controllers.leadtrustee.actions.NameRequiredAction
import mapping.{LeadTrusteeOrganisationExtractor, LeadTrusteesExtractor}
import models.IndividualOrBusiness._
import models.{LeadTrusteeIndividual, LeadTrusteeOrganisation, UserAnswers}
import pages.leadtrustee.IndividualOrBusinessPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection
import views.html.leadtrustee.LeadTrusteeDetailsView
import pages.leadtrustee.{individual => ltind}
import pages.leadtrustee.{organisation => ltorg}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class DetailsController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   standardActionSets: StandardActionSets,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: LeadTrusteeDetailsView,
                                   connector: TrustConnector,
                                   extractor: LeadTrusteesExtractor,
                                   leadTrusteeOrgExtractor: LeadTrusteeOrganisationExtractor,
                                   repository: PlaybackRepository,
                                   answerRowConverter: AnswerRowConverter,
                                   nameRequiredAction: NameRequiredAction,
                                   countryOptions: CountryOptions,
                                   val appConfig: FrontendAppConfig
                                          ) (implicit val executionContext: ExecutionContext)
  extends FrontendBaseController with I18nSupport with ReturnToStart {

  def onPageLoad(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameRequiredAction).async {
    implicit request =>

      connector.getLeadTrustee(request.userAnswers.utr).flatMap {
        case trusteeInd: LeadTrusteeIndividual =>
          val answers: Try[UserAnswers] = extractor.extractLeadTrusteeIndividual(request.userAnswers, trusteeInd)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- repository.set(updatedAnswers)
          } yield {
            renderIndividualLeadTrustee(updatedAnswers)
          }
        case trusteeOrg: LeadTrusteeOrganisation =>
          val answers: Try[UserAnswers] = leadTrusteeOrgExtractor.extractLeadTrusteeOrganisation(request.userAnswers, trusteeOrg)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- repository.set(updatedAnswers)
          } yield {
            renderOrganisationLeadTrustee(updatedAnswers)
          }
        case _ => val section = AnswerSection(None, Seq())

          Future.successful(Ok(view(section)))
      }
  }

  def onPageLoadUpdated(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameRequiredAction) {
    implicit request =>
      request.userAnswers.get(IndividualOrBusinessPage) match {
        case Some(Individual) => renderIndividualLeadTrustee(request.userAnswers)
        case Some(Business) => renderOrganisationLeadTrustee(request.userAnswers)
        case None => Ok(view(AnswerSection(None, Seq())))
      }
  }

  private def renderIndividualLeadTrustee(updatedAnswers: UserAnswers)(implicit request: LeadTrusteeNameRequest[AnyContent]) = {
    val bound = answerRowConverter.bind(updatedAnswers, request.leadTrusteeName, countryOptions)

    val section = AnswerSection(
      None,
      Seq(
        bound.nameQuestion(ltind.NamePage, "leadtrustee.individual.name", controllers.leadtrustee.individual.routes.NameController.onPageLoad().url),
        bound.dateQuestion(ltind.DateOfBirthPage, "leadtrustee.individual.dateOfBirth", controllers.leadtrustee.individual.routes.DateOfBirthController.onPageLoad().url),
        bound.yesNoQuestion(ltind.UkCitizenPage, "leadtrustee.individual.ukCitizen", controllers.leadtrustee.individual.routes.UkCitizenController.onPageLoad().url),
        bound.ninoQuestion(ltind.NationalInsuranceNumberPage, "leadtrustee.individual.nationalInsuranceNumber", controllers.leadtrustee.individual.routes.NationalInsuranceNumberController.onPageLoad().url),
        bound.identificationOptionsQuestion(ltind.IdentificationDetailOptionsPage, "leadtrustee.individual.identificationDetailOptions", controllers.leadtrustee.individual.routes.IdentificationDetailOptionsController.onPageLoad().url),
        bound.idCardDetailsQuestion(ltind.IdCardDetailsPage, "leadtrustee.individual.idCardDetails", controllers.leadtrustee.individual.routes.IdCardDetailsController.onPageLoad().url),
        bound.passportDetailsQuestion(ltind.PassportDetailsPage, "leadtrustee.individual.passportDetails", controllers.leadtrustee.individual.routes.PassportDetailsController.onPageLoad().url),
        bound.yesNoQuestion(ltind.LiveInTheUkYesNoPage, "leadtrustee.individual.liveInTheUkYesNo", controllers.leadtrustee.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(ltind.UkAddressPage, "leadtrustee.individual.ukAddress", controllers.leadtrustee.individual.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(ltind.NonUkAddressPage, "leadtrustee.individual.nonUkAddress", controllers.leadtrustee.individual.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(ltind.EmailAddressYesNoPage, "leadtrustee.individual.emailAddressYesNo", controllers.leadtrustee.individual.routes.EmailAddressYesNoController.onPageLoad().url),
        bound.stringQuestion(ltind.EmailAddressPage, "leadtrustee.individual.emailAddress", controllers.leadtrustee.individual.routes.EmailAddressController.onPageLoad().url),
        bound.stringQuestion(ltind.TelephoneNumberPage, "leadtrustee.individual.telephoneNumber", controllers.leadtrustee.individual.routes.TelephoneNumberController.onPageLoad().url)
      ).flatten
    )

    Ok(view(section))
  }

  private def renderOrganisationLeadTrustee(updatedAnswers: UserAnswers)(implicit request: LeadTrusteeNameRequest[AnyContent]) = {
    val bound = answerRowConverter.bind(updatedAnswers, request.leadTrusteeName, countryOptions)

    val section = AnswerSection(
      None,
      Seq(
        bound.yesNoQuestion(ltorg.RegisteredInUkYesNoPage, "leadtrustee.organisation.registeredInUkYesNo", controllers.leadtrustee.organisation.routes.RegisteredInUkYesNoController.onPageLoad().url),
        bound.stringQuestion(ltorg.NamePage, "leadtrustee.organisation.name", controllers.leadtrustee.organisation.routes.NameController.onPageLoad().url),
        bound.stringQuestion(ltorg.UtrPage, "leadtrustee.organisation.utr", controllers.leadtrustee.organisation.routes.UtrController.onPageLoad().url),
        bound.yesNoQuestion(ltorg.LiveInTheUkYesNoPage, "leadtrustee.organisation.liveInTheUkYesNo", controllers.leadtrustee.organisation.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(ltorg.UkAddressPage, "leadtrustee.organisation.ukAddress", controllers.leadtrustee.organisation.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(ltorg.NonUkAddressPage, "leadtrustee.organisation.nonUkAddress", controllers.leadtrustee.organisation.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(ltorg.EmailAddressYesNoPage, "leadtrustee.organisation.emailAddressYesNo", controllers.leadtrustee.organisation.routes.EmailAddressYesNoController.onPageLoad().url),
        bound.stringQuestion(ltorg.EmailAddressPage, "leadtrustee.organisation.emailAddress", controllers.leadtrustee.organisation.routes.EmailAddressController.onPageLoad().url),
        bound.stringQuestion(ltorg.TelephoneNumberPage, "leadtrustee.organisation.telephoneNumber", controllers.leadtrustee.organisation.routes.TelephoneNumberController.onPageLoad().url)
      ).flatten
    )

    Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
        extractor.mapLeadTrusteeIndividual(request.userAnswers) match {
          case None => Future.successful(InternalServerError)
          case Some(lt) => connector.amendLeadTrustee(request.userAnswers.utr, lt).map(_ => returnToStart(request.user.affinityGroup))
        }
  }
}
