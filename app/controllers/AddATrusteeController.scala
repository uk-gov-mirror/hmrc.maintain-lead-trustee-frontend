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

package controllers

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import forms.trustee.AddATrusteeFormProvider
import javax.inject.Inject
import models.{AddATrustee, AllTrustees, Enumerable}
import navigation.Navigator
import pages.trustee.AddATrusteeYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddATrusteeViewHelper
import views.html.trustee.{AddATrusteeView, AddATrusteeYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddATrusteeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       trust: TrustService,
                                       standardActionSets: StandardActionSets,
                                       addAnotherFormProvider: AddATrusteeFormProvider,
                                       yesNoFormProvider: YesNoFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddATrusteeView,
                                       yesNoView: AddATrusteeYesNoView,
                                       val appConfig: FrontendAppConfig
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController
  with I18nSupport
  with Enumerable.Implicits
  with ReturnToStart {

  val addAnotherForm = addAnotherFormProvider()
  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addATrusteeYesNo")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getAllTrustees(request.userAnswers.utr) map {
        case AllTrustees(None, Nil) => Ok(yesNoView(yesNoForm))
        case all: AllTrustees => {

          val trustees = new AddATrusteeViewHelper(all).rows

          Ok(addAnotherView(addAnotherForm,
            trustees.inProgress,
            trustees.complete,
            isLeadTrusteeDefined = false,
            heading(all.size)
          ))
        }

      }
  }

  def submitOne(): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteeYesNoPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteeYesNoPage, updatedAnswers))
        }
      )
  }

  def submitAnother(): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      trust.getAllTrustees(request.userAnswers.utr).map { trustees =>
        addAnotherForm.bindFromRequest().fold(
          (formWithErrors: Form[_]) => {

            val rows = new AddATrusteeViewHelper(trustees).rows

            BadRequest(
              addAnotherView(
                formWithErrors,
                rows.inProgress,
                rows.complete,
                isLeadTrusteeDefined = false,
                heading(trustees.size)
              )
            )
          },
          {
            case AddATrustee.YesNow => Redirect(controllers.trustee.routes.IndividualOrBusinessController.onPageLoad(trustees.trustees.size))
            case AddATrustee.YesLater => returnToStart(request.user.affinityGroup)
            case AddATrustee.NoComplete => returnToStart(request.user.affinityGroup)
          }
        )
      }
  }

  private def heading(count: Int)(implicit mp: MessagesProvider) = {
    count match {
      case 0 => Messages("addATrustee.heading")
      case 1 => Messages("addATrustee.singular.heading")
      case size => Messages("addATrustee.count.heading", size)
    }
  }
}
