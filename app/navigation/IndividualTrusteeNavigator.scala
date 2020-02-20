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

import controllers.trustee.individual.{routes => rts}
import models.UserAnswers
import pages.trustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualTrusteeNavigator {
  private val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage(index) => rts.DateOfBirthYesNoController.onPageLoad(index)
    case DateOfBirthPage(index) => rts.NationalInsuranceNumberYesNoController.onPageLoad(index)
    case NationalInsuranceNumberPage(index) => rts.NationalInsuranceNumberController.onPageLoad(index)
    case NonUkAddressPage(index) => rts.PassportDetailsYesNoController.onPageLoad(index)
    case PassportDetailsPage(index) => rts.PassportDetailsController.onPageLoad(index)
    case IdCardDetailsPage(index) => rts.IdCardDetailsController.onPageLoad(index)
  }

  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage(index), rts.DateOfBirthController.onPageLoad(index), rts.NationalInsuranceNumberYesNoController.onPageLoad(index))
    case NationalInsuranceNumberYesNoPage(index) => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage(index), rts.NationalInsuranceNumberController.onPageLoad(index), rts.AddressYesNoController.onPageLoad(index))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(ua, AddressYesNoPage(index), rts.LiveInTheUkYesNoController.onPageLoad(index), rts.AddressYesNoController.onPageLoad(index))
    case LiveInTheUkYesNoPage(index) => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage(index), rts.LiveInTheUkYesNoController.onPageLoad(index), rts.NonUkAddressController.onPageLoad(index))
    case PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(ua, PassportDetailsYesNoPage(index), rts.PassportDetailsController.onPageLoad(index), rts.IdCardDetailsYesNoController.onPageLoad(index))
    case IdCardDetailsYesNoPage(index) => ua =>
      yesNoNav(ua, IdCardDetailsYesNoPage(index), rts.IdCardDetailsController.onPageLoad(index), rts.IdCardDetailsYesNoController.onPageLoad(index))
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
