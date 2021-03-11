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

import controllers.trustee.amend.individual.{routes => rts}
import controllers.trustee.individual.routes.CountryOfNationalityYesNoController
import models.{CheckMode, UserAnswers}
import pages.trustee.amend.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object AmendIndividualTrusteeNavigator {
  final val mode = CheckMode
  private val simpleNavigation: PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => rts.DateOfBirthYesNoController.onPageLoad()
    case DateOfBirthPage => ua => navigateAwayFromDateOfBirthPages(ua)
    case NationalInsuranceNumberPage => ua => checkDetailsNavigation(ua)
    case UkAddressPage => _ => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => _ => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    case PassportOrIdCardDetailsPage => ua => checkDetailsNavigation(ua)
  }


  private val conditionalNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), navigateAwayFromDateOfBirthPages(ua))
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), rts.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(), checkDetailsNavigation(ua))
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
    case PassportOrIdCardDetailsYesNoPage => ua =>
      yesNoNav(ua, PassportOrIdCardDetailsYesNoPage, rts.PassportOrIdCardDetailsController.onPageLoad(), checkDetailsNavigation(ua))
  }

  def navigateAwayFromDateOfBirthPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(mode)
    } else {
      rts.NationalInsuranceNumberYesNoController.onPageLoad()
    }
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation orElse
      conditionalNavigation

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
