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
import controllers.actions.StandardActionSets
import controllers.leadtrustee.{routes => ltRoutes}
import mapping.{IndividualLeadTrusteeToUserAnswersMapper, LeadTrusteeOrganisationExtractor}
import models.requests.DataRequest
import models.{LeadTrustee, LeadTrusteeIndividual, LeadTrusteeOrganisation, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import utils.print.checkYourAnswers.{LeadTrusteeIndividualPrintHelper, LeadTrusteeOrganisationPrintHelper}
import views.html.leadtrustee.CheckDetailsView
import pages.leadtrustee.{individual => lind, organisation => lorg}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        leadTrusteeIndMapper: IndividualLeadTrusteeToUserAnswersMapper,
                                        leadTrusteeOrgExtractor: LeadTrusteeOrganisationExtractor,
                                        leadTrusteeIndPrintHelper: LeadTrusteeIndividualPrintHelper,
                                        leadTrusteeOrgPrintHelper: LeadTrusteeOrganisationPrintHelper,
                                        repository: PlaybackRepository,
                                        answerRowConverter: AnswerRowConverter,
                                        countryOptions: CountryOptions,
                                        val appConfig: FrontendAppConfig
                                        )(implicit val executionContext: ExecutionContext)

  extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      connector.getLeadTrustee(request.userAnswers.utr).flatMap {
        case trusteeInd: LeadTrusteeIndividual =>
          val answers: Try[UserAnswers] = leadTrusteeIndMapper.populateUserAnswers(request.userAnswers, trusteeInd)
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
      }
  }

  def onPageLoadIndividualUpdated(): Action[AnyContent] = (standardActionSets.verifiedForUtr) {
    implicit request => renderIndividualLeadTrustee(request.userAnswers)
  }

  def onPageLoadOrganisationUpdated(): Action[AnyContent] = (standardActionSets.verifiedForUtr) {
    implicit request => renderOrganisationLeadTrustee(request.userAnswers)
  }

  private def renderIndividualLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]) = {

    val section = leadTrusteeIndPrintHelper(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.individual.NamePage).map(_.displayName).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section, ltRoutes.CheckDetailsController.onSubmitIndividual()))
  }

  private def renderOrganisationLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]) = {

    val section = leadTrusteeOrgPrintHelper(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.organisation.NamePage).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section, ltRoutes.CheckDetailsController.onSubmitOrganisation()))
  }

  def onSubmitIndividual(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
        leadTrusteeIndMapper.getFromUserAnswers(request.userAnswers) match {
          case None => Future.successful(InternalServerError)
          case Some(lt) =>
            request.userAnswers.get(lind.IndexPage) match {
              case None =>
                amendLeadTrustee(request.userAnswers, lt)
              case Some(index) =>
                promoteTrustee(request.userAnswers, lt, index)
            }
        }
  }

  def onSubmitOrganisation(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
        leadTrusteeOrgExtractor.mapLeadTrusteeOrganisation(request.userAnswers) match {
          case None => Future.successful(InternalServerError)
          case Some(lt) =>
            request.userAnswers.get(lorg.IndexPage) match {
              case None =>
                amendLeadTrustee(request.userAnswers, lt)
              case Some(index) =>
                promoteTrustee(request.userAnswers, lt, index)
            }
        }
  }

  private def amendLeadTrustee(userAnswers: UserAnswers, lt: LeadTrustee)(implicit hc: HeaderCarrier) = {
    for {
      _ <- connector.amendLeadTrustee(userAnswers.utr, lt)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.leadtrustee.basePath))
      _ <- repository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }

  private def promoteTrustee(userAnswers: UserAnswers, lt: LeadTrustee, index: Int)(implicit hc: HeaderCarrier) = {
    for {
      _ <- connector.promoteTrustee(userAnswers.utr, index, lt)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.leadtrustee.basePath))
      _ <- repository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }
}
