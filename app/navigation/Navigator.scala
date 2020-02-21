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

package navigation

import javax.inject.{Inject, Singleton}
import models.{Mode, NormalMode, UserAnswers}
import navigation.leadtrustee.IndividualLeadTrusteeNavigator
import navigation.trustee.TrusteeNavigator
import pages.Page
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call =
    IndividualLeadTrusteeNavigator.routes orElse
    TrusteeNavigator.routes orElse {
    case _ => ua => controllers.routes.IndexController.onPageLoad(ua.utr)
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case _ =>
      controllers.routes.CheckYourAnswersController.onPageLoad()
  }
}


