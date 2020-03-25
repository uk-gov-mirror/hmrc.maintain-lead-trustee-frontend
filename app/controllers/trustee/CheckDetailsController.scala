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
import controllers.trustee.actions.NameRequiredAction
import javax.inject.Inject
import models.IndividualOrBusiness._
import models.{Trustee, TrusteeIndividual, TrusteeOrganisation, UserAnswers}
import navigation.Navigator
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrusteeBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.checkYourAnswers.{TrusteeIndividualPrintHelper, TrusteeOrganisationPrintHelper}
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
                                        nameAction: NameRequiredAction,
                                        indHelper: TrusteeIndividualPrintHelper,
                                        orgHelper: TrusteeOrganisationPrintHelper,
                                        trusteeBuilder: TrusteeBuilder,
                                        trustConnector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>


      val section: AnswerSection = request.userAnswers.get(IndividualOrBusinessPage) match {
        case Some(Individual) =>
          indHelper(request.userAnswers, request.trusteeName)
        case Some(Business) =>
          orgHelper(request.userAnswers, request.trusteeName)
        case _ => AnswerSection(None, Seq())
      }
      Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>
      request.userAnswers.get(WhenAddedPage).fold {
        Future.successful(Redirect(routes.WhenAddedController.onPageLoad()))
      } {
        date =>
          request.userAnswers.get(IndividualOrBusinessPage) match {
              
            case Some(Individual) =>
              val trusteeInd: TrusteeIndividual = trusteeBuilder.createTrusteeIndividual(request.userAnswers, date)
              addTrustee(request.userAnswers, trusteeInd)

            case Some(Business) =>
              val trusteeOrg: TrusteeOrganisation = trusteeBuilder.createTrusteeOrganisation(request.userAnswers, date)
              addTrustee(request.userAnswers, trusteeOrg)

            case None => Future.successful(InternalServerError)
          }
      }
  }

  private def addTrustee(userAnswers: UserAnswers, t: Trustee)(implicit hc: HeaderCarrier) = {
    trustConnector.addTrustee(userAnswers.utr, t).map(_ =>
      Redirect(controllers.routes.AddATrusteeController.onPageLoad())
    )
  }
}
