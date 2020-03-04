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

package controllers.trustee

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.trustee.individual.actions.TrusteeNameRequiredProvider
import javax.inject.Inject
import models.IndividualOrBusiness._
import models.{TrusteeIndividual, UserAnswers}
import navigation.Navigator
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrusteeBuilder
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.checkYourAnswers.TrusteeIndividualPrintHelper
import viewmodels.AnswerSection
import views.html.trustee.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        nameAction: TrusteeNameRequiredProvider,
                                        helper: TrusteeIndividualPrintHelper,
                                        trusteeBuilder: TrusteeBuilder,
                                        trustConnector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction(index)) {
    implicit request =>


      val section: AnswerSection = request.userAnswers.get(IndividualOrBusinessPage(index)) match {
        case Some(Individual) =>
          helper(request.userAnswers, request.trusteeName, index)
        case _ => AnswerSection(None, Seq())
      }
      Ok(view(section, index))
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction(index)).async {
    implicit request =>
      request.userAnswers.get(WhenAddedPage(index)).fold {
        Future.successful(Redirect(routes.WhenAddedController.onPageLoad(index)))
      } {
        date =>
          val trusteeInd: TrusteeIndividual = trusteeBuilder.createTrusteeIndividual(request.userAnswers, date, index)
          trustConnector.addTrusteeIndividual(request.userAnswers.utr, trusteeInd).flatMap { _ =>
            playbackRepository.set(UserAnswers(
              request.userAnswers.internalAuthId,
              request.userAnswers.utr,
              request.userAnswers.whenTrustSetup
            )) map ( _ => Redirect(controllers.routes.AddATrusteeController.onPageLoad()) )
          }
      }
  }
}
