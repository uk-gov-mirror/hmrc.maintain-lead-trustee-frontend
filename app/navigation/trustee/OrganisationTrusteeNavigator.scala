/*
 * Copyright 2021 HM Revenue & Customs
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
import models.{Mode, NormalMode, UserAnswers}
import pages.trustee.organisation._
import pages.trustee.organisation.amend.IndexPage
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object OrganisationTrusteeNavigator {

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      yesNoNavigation(mode)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => rts.UtrYesNoController.onPageLoad(mode)
    case UtrPage | UkAddressPage | NonUkAddressPage => navigateToStartDateOrCheckDetails(_, mode)
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case UtrYesNoPage => ua =>
      yesNoNav(ua, UtrYesNoPage, rts.UtrController.onPageLoad(mode), rts.AddressYesNoController.onPageLoad(mode))
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressInTheUkYesNoController.onPageLoad(mode), controllers.trustee.routes.WhenAddedController.onPageLoad())
    case AddressInTheUkYesNoPage => ua =>
      yesNoNav(ua, AddressInTheUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
  }

  private def navigateToStartDateOrCheckDetails(userAnswers: UserAnswers, mode: Mode): Call = {
    if (mode == NormalMode) {
      controllers.trustee.routes.WhenAddedController.onPageLoad()
    } else {
      userAnswers.get(IndexPage) match {
        case Some(index) => controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index)
        case _ => controllers.routes.SessionExpiredController.onPageLoad()
      }
    }
  }

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

}
