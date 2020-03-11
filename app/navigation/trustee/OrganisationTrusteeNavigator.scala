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

import controllers.trustee.organisation.{routes => rts}
import models.UserAnswers
import pages.trustee.organisation._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object OrganisationTrusteeNavigator {
  private val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.UtrYesNoController.onPageLoad()
    case UtrPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
    case UkAddressPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
    case NonUkAddressPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
  }

  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case UtrYesNoPage => ua =>
      yesNoNav(ua, UtrYesNoPage, rts.UtrController.onPageLoad(), rts.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressInTheUkYesNoController.onPageLoad(), controllers.trustee.routes.WhenAddedController.onPageLoad())
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
