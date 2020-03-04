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
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.TrusteeType
import navigation.Navigator
import pages.trustee.WhenAddedPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.RemoveIndexView

import scala.concurrent.{ExecutionContext, Future}

class RemoveTrusteeController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: PlaybackRepository,
                                         navigator: Navigator,
                                         standardActionSets: StandardActionSets,
                                         trust: TrustService,
                                         nameAction: TrusteeNameRequiredProvider,
                                         formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: RemoveIndexView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def formRoute(index: Int): Call =
    controllers.trustee.routes.RemoveTrusteeController.onSubmit(index)

  private val messagesPrefix: String = "removeATrustee"

  private val form = formProvider.apply(messagesPrefix)

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      for {
        trustee <- trust.getTrustee(request.userAnswers.utr, index)
      } yield {
        val trusteeName = trustee match {
          case TrusteeType(Some(trusteeInd), None) => trusteeInd.name.displayName
          case TrusteeType(None, Some(trusteeOrg)) => trusteeOrg.name
        }
        Ok(view(messagesPrefix, form, index, trusteeName, formRoute(index)))
      }

  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      import scala.concurrent.ExecutionContext.Implicits._

      def redirect(remove: Boolean, index: Int): Call = {
        if (remove) {
          controllers.trustee.routes.WhenRemovedController.onPageLoad(index)
        } else {
          controllers.routes.AddATrusteeController.onPageLoad()
        }
      }

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          for {
            trustee <- trust.getTrustee(request.userAnswers.utr, index)
          } yield {
            val trusteeName = trustee match {
              case TrusteeType(Some(trusteeInd), None) => trusteeInd.name.displayName
              case TrusteeType(None, Some(trusteeOrg)) => trusteeOrg.name
            }
            BadRequest(view(messagesPrefix, formWithErrors, index, trusteeName, formRoute(index)))
          }
        },
        value => {
          if (value) {
            Future.successful(Redirect(redirect(value, index).url))
          } else {
            Future.successful(Redirect(redirect(value, index).url))
          }
        }
      )
  }
}
