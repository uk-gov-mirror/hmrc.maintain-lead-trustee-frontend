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
import forms.IndividualOrBusinessFormProvider
import javax.inject.Inject
import models.{IndividualOrBusiness, Mode}
import navigation.Navigator
import pages.trustee.IndividualOrBusinessPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trustee.IndividualOrBusinessView

import scala.concurrent.{ExecutionContext, Future}

class IndividualOrBusinessController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                sessionRepository: PlaybackRepository,
                                                navigator: Navigator,
                                                standardActionSets: StandardActionSets,
                                                formProvider: IndividualOrBusinessFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: IndividualOrBusinessView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider("trustee.individualOrBusiness")

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualOrBusinessPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, index))
  }



  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, index))),

        (value: IndividualOrBusiness) =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualOrBusinessPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualOrBusinessPage(index), mode, updatedAnswers))
      )
  }
}
