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

package controllers.trustee.individual

import controllers.actions._
import controllers.trustee.individual.actions.TrusteeNameRequiredProvider
import forms.{IdCardDetailsFormProvider, PassportDetailsFormProvider}
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.trustee.individual.PassportDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.trustee.individual.PassportDetailsView

import scala.concurrent.{ExecutionContext, Future}

class PassportDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           sessionRepository: PlaybackRepository,
                                           navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           nameAction: TrusteeNameRequiredProvider,
                                           formProvider: PassportDetailsFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: PassportDetailsView,
                                           val countryOptions: CountryOptions
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("trustee")

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction(index)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PassportDetailsPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, index, request.trusteeName))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction(index)).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, index, request.trusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PassportDetailsPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PassportDetailsPage(index), mode, updatedAnswers))
      )
  }
}
