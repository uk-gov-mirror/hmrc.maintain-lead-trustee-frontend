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

import controllers.actions.StandardActionSets
import forms.{IndividualOrBusinessFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.{IndividualOrBusiness, Mode}
import navigation.Navigator
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.individual.DateOfBirthYesNoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trustee.IndividualOrBusinessView
import views.html.trustee.individual.DateOfBirthYesNoView

import scala.concurrent.{ExecutionContext, Future}

class DateOfBirthYesNoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            sessionRepository: PlaybackRepository,
                                            navigator: Navigator,
                                            standardActionSets: StandardActionSets,
                                            nameAction: actions.NameRequiredAction,
                                            formProvider: YesNoFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: DateOfBirthYesNoView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("trustee.individual.dateOfBirthYesNo")

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DateOfBirthYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, index, request.trusteeName))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, index, request.trusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DateOfBirthYesNoPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DateOfBirthYesNoPage(index), mode, updatedAnswers))
      )
  }
}