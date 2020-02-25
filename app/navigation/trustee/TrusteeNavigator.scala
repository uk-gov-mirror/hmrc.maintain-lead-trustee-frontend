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

package navigation.trustee

import models.IndividualOrBusiness.{Business, Individual}
import models.UserAnswers
import pages.Page
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.mvc.Call

object TrusteeNavigator {

  private val simpleNavigation: PartialFunction[Page, Call] = {
    case WhenAddedPage(index) => controllers.trustee.routes.CheckDetailsController.onPageLoad(index)
  }

  private val parameterisedNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case IndividualOrBusinessPage(index) => individualOrBusinessNavigation(index)
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    parameterisedNavigation orElse
    IndividualTrusteeNavigator.routes

  private def individualOrBusinessNavigation(index: Int)(userAnswers: UserAnswers): Call = {
    userAnswers.get(IndividualOrBusinessPage(index)).map {
      case Individual => controllers.trustee.individual.routes.NameController.onPageLoad(index)
      case Business => controllers.trustee.routes.IndividualOrBusinessController.onPageLoad(index)
    }.getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
