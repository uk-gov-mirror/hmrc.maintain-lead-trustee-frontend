/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import controllers.leadtrustee.{routes => ltRoutes}
import mapping.extractors.TrusteeExtractor
import mapping.mappers.TrusteeMapper
import models.requests.DataRequest
import models.{LeadTrustee, LeadTrusteeIndividual, LeadTrusteeOrganisation, UserAnswers}
import pages.leadtrustee.{individual => lind, organisation => lorg}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.PlaybackRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.checkYourAnswers.TrusteePrintHelper
import views.html.leadtrustee.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        extractor: TrusteeExtractor,
                                        printHelper: TrusteePrintHelper,
                                        mapper: TrusteeMapper,
                                        repository: PlaybackRepository
                                      )(implicit val executionContext: ExecutionContext)

  extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
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
          val answers: Try[UserAnswers] = extractor.extractLeadTrusteeOrganisation(request.userAnswers, trusteeOrg)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- repository.set(updatedAnswers)
          } yield {
            renderOrganisationLeadTrustee(updatedAnswers)
          }
        case _ =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}] Unable to retrieve Lead Trustee from trusts")
          throw new RuntimeException("unable to retrieve lead trustee from trusts service")
      }
  }

  def onPageLoadIndividualUpdated(): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request => renderIndividualLeadTrustee(request.userAnswers)
  }

  def onPageLoadOrganisationUpdated(): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request => renderOrganisationLeadTrustee(request.userAnswers)
  }

  private def renderIndividualLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]): Result = {

    val section = printHelper.printLeadIndividualTrustee(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.individual.NamePage).map(_.displayName).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section, ltRoutes.CheckDetailsController.onSubmitIndividual()))
  }

  private def renderOrganisationLeadTrustee(updatedAnswers: UserAnswers)(implicit request: DataRequest[AnyContent]): Result = {

    val section = printHelper.printLeadOrganisationTrustee(
      updatedAnswers,
      updatedAnswers.get(pages.leadtrustee.organisation.NamePage).getOrElse(request.messages(messagesApi)("leadTrusteeName.defaultText"))
    )

    Ok(view(section, ltRoutes.CheckDetailsController.onSubmitOrganisation()))
  }

  def onSubmitIndividual(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
      mapper.mapToLeadTrusteeIndividual(request.userAnswers) match {
        case None =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}]" +
            s"[UTR: ${request.userAnswers.utr}] unable to build lead trustee individual from user answers," +
            s" cannot continue with submitting transform")
          Future.successful(InternalServerError)
        case Some(lt) =>
          request.userAnswers.get(lind.IndexPage) match {
            case None =>
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}] amending lead trustee")
              amendLeadTrustee(request.userAnswers, lt)
            case Some(index) =>
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}] promoting lead trustee")
              promoteTrustee(request.userAnswers, lt, index)
          }
      }
  }

  def onSubmitOrganisation(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>
      mapper.mapToLeadTrusteeOrganisation(request.userAnswers) match {
        case None =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}]" +
            s"[UTR: ${request.userAnswers.utr}] unable to build lead trustee organisation from user answers," +
            s" cannot continue with submitting transform")
          Future.successful(InternalServerError)
        case Some(lt) =>
          request.userAnswers.get(lorg.IndexPage) match {
            case None =>
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}] amending lead trustee")
              amendLeadTrustee(request.userAnswers, lt)
            case Some(index) =>
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}] promoting lead trustee")
              promoteTrustee(request.userAnswers, lt, index)
          }
      }
  }

  private def amendLeadTrustee(userAnswers: UserAnswers, lt: LeadTrustee)(implicit hc: HeaderCarrier): Future[Result] = {
    for {
      _ <- connector.amendLeadTrustee(userAnswers.utr, lt)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.leadtrustee.basePath))
      _ <- repository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }

  private def promoteTrustee(userAnswers: UserAnswers, lt: LeadTrustee, index: Int)(implicit hc: HeaderCarrier): Future[Result] = {
    for {
      _ <- connector.promoteTrustee(userAnswers.utr, index, lt)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.leadtrustee.basePath))
      _ <- repository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }
}
