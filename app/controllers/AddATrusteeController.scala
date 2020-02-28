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

import connectors.TrustConnector
import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import forms.trustee.AddATrusteeFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode, RemoveTrusteeIndividual, TrusteeType, Trustees}
import navigation.Navigator
import pages.trustee.{AddATrusteePage, AddATrusteeYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import sections.LeadTrusteeIndividual
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
                                       yesNoView: AddATrusteeYesNoView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm = addAnotherFormProvider()
  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addATrusteeYesNo")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      trust.getTrustees(request.userAnswers.utr) map {
          case Trustees(Nil) => Ok()
          case Trustees(data) =>  {

              val trustees = new AddATrusteeViewHelper(data).rows

              val isLeadTrusteeDefined = request.userAnswers.get(LeadTrusteeIndividual).exists(trustee => trustee.isLead)  // TODO Check for Lead Trustee Company

              trustees.count match {
                case 0 =>
                  Ok(yesNoView(yesNoForm, mode))
                case count =>
                  Ok(addAnotherView(addAnotherForm, mode, trustees.inProgress, trustees.complete, isLeadTrusteeDefined, heading(count)))
              }

          }
      }

  }

  def submitOne(mode: Mode): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, mode)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteeYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteeYesNoPage, updatedAnswers))
        }
      )
  }

  def submitAnother(mode: Mode): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          trust.getTrustees(request.userAnswers.utr).map {
            case Trustees(Nil) => Ok()
            case Trustees(data) =>  {

              val trustees = new AddATrusteeViewHelper(data).rows

              val isLeadTrusteeDefined = request.userAnswers.get(LeadTrusteeIndividual).exists(trustee => trustee.isLead)  // TODO Check for Lead Trustee Company

              BadRequest(
                addAnotherView(
                  formWithErrors,
                  mode,
                  trustees.inProgress,
                  trustees.complete,
                  isLeadTrusteeDefined,
                  heading(trustees.count)
                )
              )

            }
          }
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteePage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteePage, updatedAnswers))
        }
      )
  }

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addATrustee.heading")
      case 1 => Messages("addATrustee.singular.heading")
      case size => Messages("addATrustee.count.heading", size)
    }
  }
}
