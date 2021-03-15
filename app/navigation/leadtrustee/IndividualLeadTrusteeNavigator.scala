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

package navigation.leadtrustee

import controllers.leadtrustee.individual.{routes => rts}
import controllers.leadtrustee.{routes => leadTrusteeRoutes}
import models.UserAnswers
import pages.leadtrustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualLeadTrusteeNavigator {

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation orElse
      yesNoNavigation

  private def simpleNavigation: PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => rts.DateOfBirthController.onPageLoad()
    case DateOfBirthPage => ua => navigateAwayFromDateOfBirthQuestion(ua)
    case CountryOfNationalityPage => _ => rts.UkCitizenController.onPageLoad()
    case NationalInsuranceNumberPage | PassportOrIdCardDetailsPage => ua => navigateAwayFromIdentificationQuestions(ua)
    case CountryOfResidencePage => _ => rts.NonUkAddressController.onPageLoad()
    case UkAddressPage | NonUkAddressPage => _ => rts.EmailAddressYesNoController.onPageLoad()
    case EmailAddressPage => _ => rts.TelephoneNumberController.onPageLoad()
    case TelephoneNumberPage => _ => leadTrusteeRoutes.CheckDetailsController.onPageLoadIndividualUpdated()
  }

  private def yesNoNavigation: PartialFunction[Page, UserAnswers => Call] = {
    yesNoNav(CountryOfNationalityInTheUkYesNoPage, rts.UkCitizenController.onPageLoad(), rts.CountryOfNationalityController.onPageLoad()) orElse
      yesNoNav(UkCitizenPage, rts.NationalInsuranceNumberController.onPageLoad(), rts.PassportOrIdCardController.onPageLoad()) orElse
      yesNoNav(CountryOfResidenceInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.CountryOfResidenceController.onPageLoad()) orElse
      yesNoNav(LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad()) orElse
      yesNoNav(EmailAddressYesNoPage, rts.EmailAddressController.onPageLoad(), rts.TelephoneNumberController.onPageLoad())
  }

  private def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` => ua =>
      ua.get(fromPage)
        .map(if (_) yesCall else noCall)
        .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def navigateAwayFromDateOfBirthQuestion(ua: UserAnswers): Call = {
    if (ua.is5mldEnabled) {
      rts.CountryOfNationalityInTheUkYesNoController.onPageLoad()
    } else {
      rts.UkCitizenController.onPageLoad()
    }
  }

  private def navigateAwayFromIdentificationQuestions(ua: UserAnswers): Call = {
    if (ua.is5mldEnabled) {
      rts.CountryOfResidenceInTheUkYesNoController.onPageLoad()
    } else {
      rts.LiveInTheUkYesNoController.onPageLoad()
    }
  }

}
