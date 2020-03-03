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
import controllers.actions.StandardActionSets
import controllers.actions.{LeadTrusteeNameRequest, StandardActionSets}
import controllers.leadtrustee.actions.NameRequiredAction
import mapping.{LeadTrusteeIndividualExtractor, LeadTrusteeOrganisationExtractor}
import models.IndividualOrBusiness._
import models.requests.DataRequest
import models.{LeadTrusteeIndividual, LeadTrusteeOrganisation, UserAnswers}
import pages.leadtrustee.IndividualOrBusinessPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import utils.print.checkYourAnswers.{LeadTrusteeIndividualPrintHelper, LeadTrusteeOrganisationPrintHelper}
import viewmodels.AnswerSection
import views.html.leadtrustee.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        leadTrusteeIndExtractor: LeadTrusteeIndividualExtractor,
                                        leadTrusteeOrgExtractor: LeadTrusteeOrganisationExtractor,
                                        leadTrusteeIndPrintHelper: LeadTrusteeIndividualPrintHelper,
                                        leadTrusteeOrgPrintHelper: LeadTrusteeOrganisationPrintHelper,
                                        repository: PlaybackRepository,
                                        answerRowConverter: AnswerRowConverter,
                                        countryOptions: CountryOptions,
                                        val appConfig: FrontendAppConfig
                                          )(implicit val executionContext: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (standardActionSets.verifiedForUtr).async {
    implicit request =>

      connector.getLeadTrustee(request.userAnswers.utr).flatMap {
        case trusteeInd: LeadTrusteeIndividual =>
          val answers: Try[UserAnswers] = leadTrusteeIndExtractor.extractLeadTrusteeIndividual(request.userAnswers, trusteeInd)
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

  def onPageLoadUpdated(): Action[AnyContent] = (standardActionSets.verifiedForUtr) {
    implicit request =>
      request.userAnswers.get(IndividualOrBusinessPage) match {
        case Some(Individual) => renderIndividualLeadTrustee(request.userAnswers)
        case Some(Business) => renderOrganisationLeadTrustee(request.userAnswers)
        case None => Ok(view(AnswerSection(None, Seq())))
      }
  }

  private def renderIndividualLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]) = {

    val section = leadTrusteeIndPrintHelper(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.individual.NamePage).map(_.displayName).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section))
  }

  private def renderOrganisationLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]) = {

    val section = leadTrusteeOrgPrintHelper(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.organisation.NamePage).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
      request.userAnswers.get(IndividualOrBusinessPage) match {
        case Some(_) =>
          leadTrusteeIndExtractor.mapLeadTrusteeIndividual(request.userAnswers) match {
            case None => Future.successful(InternalServerError)
            case Some(lt) => connector.amendLeadTrustee(request.userAnswers.utr, lt).map(_ =>
              Redirect(controllers.routes.AddATrusteeController.onPageLoad())
            )
          }
        case None =>
          Future.successful(InternalServerError)
      }

  }
}
