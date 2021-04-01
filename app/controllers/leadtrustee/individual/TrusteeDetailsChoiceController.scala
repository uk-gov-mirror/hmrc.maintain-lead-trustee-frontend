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

import config.FrontendAppConfig
import controllers.actions._
import controllers.leadtrustee.actions.NameRequiredAction
import forms.DetailsChoiceFormProvider
import models.DetailsChoice
import navigation.Navigator
import pages.leadtrustee.individual.TrusteeDetailsChoicePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.leadtrustee.individual.TrusteeDetailsChoiceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrusteeDetailsChoiceController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                implicit val frontendAppConfig: FrontendAppConfig,
                                                registrationsRepository: PlaybackRepository,
                                                navigator: Navigator,
                                                standardActionSets: StandardActionSets,
                                                nameAction: NameRequiredAction,
                                                formProvider: DetailsChoiceFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: TrusteeDetailsChoiceView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[DetailsChoice] = formProvider.withPrefix("leadTrustee.individual.trusteeDetailsChoice")

  private def actions() =
    standardActionSets.identifiedUserWithData andThen nameAction

  def onPageLoad(): Action[AnyContent] = actions() {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrusteeDetailsChoicePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.leadTrusteeName, request.userAnswers.is5mldEnabled))
  }


  def onSubmit(): Action[AnyContent] = actions().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, request.leadTrusteeName, request.userAnswers.is5mldEnabled))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteeDetailsChoicePage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteeDetailsChoicePage, updatedAnswers))
        }
      )
  }
}
