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
import controllers.trustee.individual.actions.TrusteeNameRequiredProvider
import forms.DateAddedToTrustFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.trustee.WhenAddedPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trustee.WhenAddedView

import scala.concurrent.{ExecutionContext, Future}

class WhenAddedController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     sessionRepository: PlaybackRepository,
                                     navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     nameAction: TrusteeNameRequiredProvider,
                                     formProvider: DateAddedToTrustFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: WhenAddedView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction(index)) {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("trustee.whenAdded", request.userAnswers.whenTrustSetup)

      val preparedForm = request.userAnswers.get(WhenAddedPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, request.trusteeName))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction(index)).async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("trustee.whenAdded", request.userAnswers.whenTrustSetup)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, index, request.trusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhenAddedPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhenAddedPage(index), mode, updatedAnswers))
      )
  }
}
