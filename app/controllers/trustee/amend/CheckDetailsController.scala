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

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.trustee.actions.NameRequiredAction
import javax.inject.Inject
import mapping.{TrusteeIndividualExtractor, TrusteeOrganisationExtractor}
import models.IndividualOrBusiness._
import models.{IndividualOrBusiness, Trustee, TrusteeIndividual, TrusteeOrganisation, UserAnswers}
import navigation.Navigator
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

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        navigator: Navigator,
                                        trustService: TrustService,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        indHelper: AmendTrusteeIndividualPrintHelper,
                                        orgHelper: AmendTrusteeOrganisationPrintHelper,
                                        trusteeBuilder: AmendedTrusteeBuilder,
                                        indExtractor: TrusteeIndividualExtractor,
                                        orgExtractor: TrusteeOrganisationExtractor,
                                        trustConnector: TrustConnector,
                                        nameAction: NameRequiredAction,
                                        val appConfig: FrontendAppConfig
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trustService.getTrustee(request.userAnswers.utr, index).flatMap {
        case ind: TrusteeIndividual =>
          val answers = indExtractor(request.userAnswers, ind, index)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val section = indHelper(updatedAnswers, ind.name.displayName)
            Ok(view(section, index))
          }

        case org: TrusteeOrganisation =>
          val answers = orgExtractor(request.userAnswers, org, index)
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

}