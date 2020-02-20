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

package controllers.leadtrustee.individual

import controllers.actions._
import controllers.leadtrustee.individual.actions.NameRequiredAction
import forms.IdentificationDetailOptionsFormProvider
import models.Mode
import navigation.Navigator
import pages.leadtrustee.individual.IdentificationDetailOptionsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.leadtrustee.individual.IdentificationDetailOptionsView
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class IdentificationDetailOptionsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       playbackRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       standardActionSets: StandardActionSets,
                                       nameAction: NameRequiredAction,
                                       formProvider: IdentificationDetailOptionsFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: IdentificationDetailOptionsView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("leadtrustee")

  def onPageLoad(mode: Mode): Action[AnyContent] = (standardActionSets.IdentifiedUserWithData andThen nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(IdentificationDetailOptionsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.leadTrusteeName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (standardActionSets.IdentifiedUserWithData andThen nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.leadTrusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IdentificationDetailOptionsPage, value))
            _              <- playbackRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IdentificationDetailOptionsPage, mode, updatedAnswers))
      )
  }
}
