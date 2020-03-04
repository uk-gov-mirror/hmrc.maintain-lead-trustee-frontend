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

import controllers.RemoveIndexController
import controllers.actions.StandardActionSets
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.Name
import models.requests.DataRequest
import pages.QuestionPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import repositories.PlaybackRepository
import views.html.RemoveIndexView
import pages.trustee.individual.NamePage

import scala.concurrent.ExecutionContext

class RemoveIndividualTrusteeController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val repository: PlaybackRepository,
                                         standardActionSets: StandardActionSets,
                                         val formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         val removeView: RemoveIndexView
                                 )(implicit ec: ExecutionContext) extends RemoveIndexController with I18nSupport {

  override val messagesPrefix : String = "removeATrustee"

  override def page(index: Int) : QuestionPage[Name] = NamePage(index)

  override def actions(index: Int) =
    standardActionSets.identifiedUserWithData

  override def redirect(remove: Boolean, index : Int) : Call = {
    if (remove) {
      controllers.trustee.routes.WhenRemovedController.onPageLoad(index)
    } else {
      controllers.routes.AddATrusteeController.onPageLoad()
    }
  }

  override def formRoute(index: Int): Call =
    controllers.trustee.routes.RemoveIndividualTrusteeController.onSubmit(index)

  override def content(index: Int)(implicit request: DataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).map(_.displayName).getOrElse(Messages(s"$messagesPrefix.default"))

}
