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

import models.{NormalMode, UserAnswers}
import pages.trustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call
import controllers.trustee.individual.{routes => rts}

object AddIndividualTrusteeNavigator {
  val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.DateOfBirthYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
    case UkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case PassportDetailsPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
    case IdCardDetailsPage => controllers.trustee.routes.WhenAddedController.onPageLoad()
  }

  val conditionalNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthPage => ua =>
      navigateAwayFromDateOfBirthPages(ua)
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), navigateAwayFromDateOfBirthPages(ua))
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), rts.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(), controllers.trustee.routes.WhenAddedController.onPageLoad())
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
    case PassportDetailsYesNoPage => ua =>
      yesNoNav(ua, PassportDetailsYesNoPage, rts.PassportDetailsController.onPageLoad(), rts.IdCardDetailsYesNoController.onPageLoad())
    case IdCardDetailsYesNoPage => ua =>
      yesNoNav(ua, IdCardDetailsYesNoPage, rts.IdCardDetailsController.onPageLoad(), controllers.trustee.routes.WhenAddedController.onPageLoad())
  }

  def navigateAwayFromDateOfBirthPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      rts.CountryOfNationalityYesNoController.onPageLoad(NormalMode)
    } else {
      rts.NationalInsuranceNumberYesNoController.onPageLoad()
    }
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    conditionalNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
