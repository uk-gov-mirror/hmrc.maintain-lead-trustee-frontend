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

import controllers.trustee.amend.organisation.{routes => rts}
import models.UserAnswers
import pages.trustee.amend.organisation._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object AmendOrganisationTrusteeNavigator {
  private val simpleNavigation: PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => rts.UtrYesNoController.onPageLoad()
    case UtrPage => ua => checkDetailsNavigation(ua)
    case UkAddressPage => ua => checkDetailsNavigation(ua)
    case NonUkAddressPage => ua => checkDetailsNavigation(ua)
  }

  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case UtrYesNoPage => ua =>
      yesNoNav(ua, UtrYesNoPage, rts.UtrController.onPageLoad(), rts.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressInTheUkYesNoController.onPageLoad(), checkDetailsNavigation(ua))
    case AddressInTheUkYesNoPage => ua =>
      yesNoNav(ua, AddressInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation orElse
    yesNoNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def checkDetailsNavigation(userAnswers: UserAnswers): Call = {
    userAnswers.get(IndexPage) match {
      case Some(index) => controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
