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

import java.time.LocalDate

import connectors.TrustConnector
import controllers.actions.IdentifierAction
import javax.inject.Inject
import models.{UserAnswers, UtrSession}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.{ActiveSessionRepository, PlaybackRepository}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 identifierAction: IdentifierAction,
                                 activeSessionRepository: ActiveSessionRepository,
                                 cacheRepository : PlaybackRepository,
                                 connector: TrustConnector)
                               (implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(utr: String): Action[AnyContent] = identifierAction.async {
      implicit request =>

        val session = UtrSession(request.user.internalId, utr)

        for {
          date <- connector.getTrustStartDate(utr)
          _ <- activeSessionRepository.set(session)
          _ <- cacheRepository.set(UserAnswers.newSession(request.user.internalId, utr, date.startDate))
        } yield Redirect(controllers.routes.AddATrusteeController.onPageLoad())
    }
}
