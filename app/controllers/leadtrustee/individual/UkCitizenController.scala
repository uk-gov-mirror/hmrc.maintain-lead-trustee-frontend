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

package controllers.leadtrustee.individual

import controllers.actions._
import controllers.leadtrustee.actions.NameRequiredAction
import forms.UkCitizenFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.leadtrustee.individual.UkCitizenPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.leadtrustee.individual.UkCitizenView

import scala.concurrent.{ExecutionContext, Future}

class UkCitizenController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     playbackRepository: PlaybackRepository,
                                     navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     nameAction: NameRequiredAction,
                                     formProvider: UkCitizenFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: UkCitizenView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider("leadtrustee.individual")

  def onPageLoad(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(UkCitizenPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.leadTrusteeName))
  }

  def onSubmit(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.leadTrusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UkCitizenPage, value))
            _ <- playbackRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(UkCitizenPage, updatedAnswers))
      )
  }
}
