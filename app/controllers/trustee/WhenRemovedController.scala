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

import controllers.actions.StandardActionSets
import forms.DateRemovedFromTrustFormProvider
import javax.inject.Inject
import models.{RemoveTrustee, TrusteeIndividual, TrusteeOrganisation}
import navigation.Navigator
import pages.trustee.WhenRemovedPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trustee.WhenRemovedView

import scala.concurrent.ExecutionContext

class WhenRemovedController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     sessionRepository: PlaybackRepository,
                                     navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     formProvider: DateRemovedFromTrustFormProvider,
                                     trust: TrustService,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: WhenRemovedView,
                                     trustService: TrustService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("trustee.whenRemoved", request.userAnswers.whenTrustSetup)

      val preparedForm = request.userAnswers.get(WhenRemovedPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      trust.getTrustee(request.userAnswers.utr, index).map {
        trustee =>
          val trusteeName = trustee match {
            case lti:TrusteeIndividual => lti.name.displayName
            case lto:TrusteeOrganisation => lto.name
          }
        Ok(view(preparedForm, index, trusteeName))
      }


  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("trustee.whenRemoved", request.userAnswers.whenTrustSetup)

      form.bindFromRequest().fold(
        formWithErrors =>{
          trust.getTrustee(request.userAnswers.utr, index).map {
            trustee =>
              val trusteeName = trustee match {
                case lti:TrusteeIndividual => lti.name.displayName
                case lto:TrusteeOrganisation => lto.name
              }
            BadRequest(view(formWithErrors, index, trusteeName))
          }
        },
        value =>
          for {
            _ <- trustService.removeTrustee(request.userAnswers.utr, RemoveTrustee(index, value))
          } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
      )
  }
}