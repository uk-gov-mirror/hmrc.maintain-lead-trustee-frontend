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

package controllers.trustee.amend

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.trustee.actions.NameRequiredAction
import handlers.ErrorHandler
import mapping.extractors.TrusteeExtractor
import mapping.mappers.TrusteeMappers
import models.IndividualOrBusiness._
import models.{Trustee, TrusteeIndividual, TrusteeOrganisation, UserAnswers}
import pages.trustee.IndividualOrBusinessPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.checkYourAnswers.TrusteePrintHelper
import views.html.trustee.amend.CheckDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        trustService: TrustService,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        printHelper: TrusteePrintHelper,
                                        mapper: TrusteeMappers,
                                        extractor: TrusteeExtractor,
                                        trustConnector: TrustConnector,
                                        nameAction: NameRequiredAction,
                                        val appConfig: FrontendAppConfig,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trustService.getTrustee(request.userAnswers.identifier, index).flatMap {
        case ind: TrusteeIndividual =>
          val answers = extractor.extractTrusteeIndividual(request.userAnswers, ind, index)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val section = printHelper.printAmendedIndividualTrustee(updatedAnswers, ind.name.displayName)
            Ok(view(section, index))
          }

        case org: TrusteeOrganisation =>
          val answers = extractor.extractTrusteeOrganisation(request.userAnswers, org, index)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val section = printHelper.printAmendedOrganisationTrustee(updatedAnswers, org.name)
            Ok(view(section, index))
          }
        case _ =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}] Unable to retrieve trustee from trusts")
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def onPageLoadUpdated(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      request.userAnswers.get(IndividualOrBusinessPage) map {
        case Individual =>
          val section = printHelper.printAmendedIndividualTrustee(request.userAnswers, request.trusteeName)
          Ok(view(section, index))
        case Business =>
          val section = printHelper.printAmendedOrganisationTrustee(request.userAnswers, request.trusteeName)
          Ok(view(section, index))
      } getOrElse {
        logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}] " +
          s"unable to display updated trustee on check your answers due to not having user answer for individual or business")
        InternalServerError(errorHandler.internalServerErrorTemplate)
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      val trustee: Option[Trustee] = request.userAnswers.get(IndividualOrBusinessPage) match {
        case Some(Individual) =>
          logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}] amending trustee individual")
          mapper.mapToTrusteeIndividual(request.userAnswers)
        case Some(Business) =>
          logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}] amending trustee organisation")
          mapper.mapToTrusteeOrganisation(request.userAnswers)
        case _ =>
          None
      }

      trustee match {
        case Some(t) =>
          amendTrustee(request.userAnswers, t, index)
        case _ =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}] " +
            s"unable to amend trustee due to no user answer for individual or business")
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  private def amendTrustee(userAnswers: UserAnswers, t: Trustee, index: Int)(implicit hc: HeaderCarrier): Future[Result] = {
    for {
      _ <- trustConnector.amendTrustee(userAnswers.identifier, index, t)
      updatedUserAnswers <- Future.fromTry(userAnswers.deleteAtPath(pages.trustee.basePath))
      _ <- sessionRepository.set(updatedUserAnswers)
    } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
  }

}
