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

import controllers.leadtrustee.individual.routes._
import controllers.leadtrustee.routes._
import models.UserAnswers
import pages.leadtrustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualLeadTrusteeNavigator {

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation orElse
      yesNoNavigation

  private def simpleNavigation: PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => DateOfBirthController.onPageLoad()
    case DateOfBirthPage => navigateAwayFromDateOfBirthQuestion
    case CountryOfNationalityPage => _ => UkCitizenController.onPageLoad()
    case NationalInsuranceNumberPage | PassportOrIdCardDetailsPage => navigateAwayFromIdentificationQuestions
    case CountryOfResidencePage => _ => NonUkAddressController.onPageLoad()
    case UkAddressPage | NonUkAddressPage => _ => EmailAddressYesNoController.onPageLoad()
    case EmailAddressPage => _ => TelephoneNumberController.onPageLoad()
    case TelephoneNumberPage => _ => CheckDetailsController.onPageLoadIndividualUpdated()
  }

  private def yesNoNavigation: PartialFunction[Page, UserAnswers => Call] =
    yesNoNav(CountryOfNationalityInTheUkYesNoPage, UkCitizenController.onPageLoad(), CountryOfNationalityController.onPageLoad()) orElse
      yesNoNav(UkCitizenPage, NationalInsuranceNumberController.onPageLoad(), PassportOrIdCardController.onPageLoad()) orElse
      yesNoNav(CountryOfResidenceInTheUkYesNoPage, UkAddressController.onPageLoad(), CountryOfResidenceController.onPageLoad()) orElse
      yesNoNav(LiveInTheUkYesNoPage, UkAddressController.onPageLoad(), NonUkAddressController.onPageLoad()) orElse
      yesNoNav(EmailAddressYesNoPage, EmailAddressController.onPageLoad(), TelephoneNumberController.onPageLoad())

  private def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` => ua =>
      ua.get(fromPage)
        .map(if (_) yesCall else noCall)
        .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def navigateAwayFromDateOfBirthQuestion(ua: UserAnswers): Call = {
    if (ua.is5mldEnabled) {
      CountryOfNationalityInTheUkYesNoController.onPageLoad()
    } else {
      UkCitizenController.onPageLoad()
    }
  }

  private def navigateAwayFromIdentificationQuestions(ua: UserAnswers): Call = {
    if (ua.is5mldEnabled) {
      CountryOfResidenceInTheUkYesNoController.onPageLoad()
    } else {
      LiveInTheUkYesNoController.onPageLoad()
    }
  }

}
