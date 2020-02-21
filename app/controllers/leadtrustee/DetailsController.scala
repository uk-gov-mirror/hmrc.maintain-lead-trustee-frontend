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

package controllers.leadtrustee

import com.google.inject.Inject
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import mapping.LeadTrusteesExtractor
import models.{LeadTrusteeIndividual}
import pages.trustee.individual.DateOfBirthPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.leadtrustee.LeadTrusteeDetailsView

import scala.concurrent.{ExecutionContext, Future}

class DetailsController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            standardActionSets: StandardActionSets,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: LeadTrusteeDetailsView,
                                            connector: TrustConnector,
                                            extractor: LeadTrusteesExtractor,
                                            repository: PlaybackRepository
                                          ) (implicit val executionContext: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.IdentifiedUserWithData.async {
    implicit request =>

      connector.getLeadTrustee(request.userAnswers.utr).flatMap {
        case trusteeInd: LeadTrusteeIndividual =>
          val answers = extractor.extractLeadTrusteeIndividual(request.userAnswers, trusteeInd)
          for {
            updatedAnswers <- Future.fromTry(answers)
            _ <- repository.set(updatedAnswers)
          } yield {
            val sections = Seq(AnswerSection(None, Seq()))

            Ok(view(sections))
          }
        case _ => val sections = Seq(AnswerSection(None, Seq()))

          Future.successful(Ok(view(sections)))
      }
  }
}
