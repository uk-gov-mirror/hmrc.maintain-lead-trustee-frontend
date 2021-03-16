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

import controllers.trustee.individual.add.routes._
import controllers.trustee.individual.amend.routes._
import controllers.trustee.individual.routes._
import models.{Mode, NormalMode, UserAnswers}
import pages.trustee.individual._
import pages.trustee.individual.add._
import pages.trustee.individual.amend._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualTrusteeNavigator {

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      yesNoNavigation(mode) orElse
      addNavigation(mode) orElse
      amendNavigation(mode)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => DateOfBirthYesNoController.onPageLoad(mode)
    case DateOfBirthPage => navigateAwayFromDateOfBirthPages(_, mode)
    case CountryOfNationalityPage => navigateAwayFromCountryOfNationalityPages(_, mode)
    case NationalInsuranceNumberPage => navigateAwayFromNinoPages(_, mode)
    case CountryOfResidencePage => navigateToOrBypassAddressPages(_, mode)
    case UkAddressPage | NonUkAddressPage => _ => navigateToIdQuestions(mode)
    case PassportDetailsPage | IdCardDetailsPage | PassportOrIdCardDetailsPage => navigateToMentalCapacityOrWhenAddedPage(_, mode)
    case MentalCapacityYesNoPage => navigateToWhenAddedOrCheckDetails(_, mode)
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = DateOfBirthYesNoPage,
        yesCall = DateOfBirthController.onPageLoad(mode),
        noCall = navigateAwayFromDateOfBirthPages(ua, mode)
      )
    case CountryOfNationalityYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfNationalityYesNoPage,
        yesCall = CountryOfNationalityInTheUkYesNoController.onPageLoad(mode),
        noCall = navigateAwayFromCountryOfNationalityPages(ua, mode)
      )
    case CountryOfNationalityInTheUkYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfNationalityInTheUkYesNoPage,
        yesCall = navigateAwayFromCountryOfNationalityPages(ua, mode),
        noCall = CountryOfNationalityController.onPageLoad(mode)
      )
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = NationalInsuranceNumberYesNoPage,
        yesCall = NationalInsuranceNumberController.onPageLoad(mode),
        noCall = navigateAwayFromNinoPages(ua, mode)
      )
    case CountryOfResidenceYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceYesNoPage,
        yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(mode),
        noCall = navigateToOrBypassAddressPages(ua, mode)
      )
    case CountryOfResidenceInTheUkYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceInTheUkYesNoPage,
        yesCall = navigateToOrBypassAddressPages(ua, mode),
        noCall = CountryOfResidenceController.onPageLoad(mode)
      )
    case AddressYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressYesNoPage,
        yesCall = LiveInTheUkYesNoController.onPageLoad(mode),
        noCall = navigateToMentalCapacityOrWhenAddedPage(ua, mode)
      )
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = LiveInTheUkYesNoPage,
        yesCall = UkAddressController.onPageLoad(mode),
        noCall = NonUkAddressController.onPageLoad(mode)
      )
  }

  private def addNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case PassportDetailsYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = PassportDetailsYesNoPage,
        yesCall = PassportDetailsController.onPageLoad(),
        noCall = IdCardDetailsYesNoController.onPageLoad()
      )
    case IdCardDetailsYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = IdCardDetailsYesNoPage,
        yesCall = IdCardDetailsController.onPageLoad(),
        noCall = navigateToMentalCapacityOrWhenAddedPage(ua, mode)
      )
  }

  private def amendNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case PassportOrIdCardDetailsYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = PassportOrIdCardDetailsYesNoPage,
        yesCall = PassportOrIdCardDetailsController.onPageLoad(),
        noCall = navigateToMentalCapacityOrWhenAddedPage(ua, mode)
      )
  }

  private def navigateAwayFromNinoPages(ua: UserAnswers, mode: Mode): Call = {
    if (ua.is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      navigateToOrBypassAddressPages(ua, mode)
    }
  }

  private def navigateToOrBypassAddressPages(ua: UserAnswers, mode: Mode): Call = {
    if (ua.get(NationalInsuranceNumberPage).isDefined || !ua.isTaxable) {
      navigateToMentalCapacityOrWhenAddedPage(ua, mode)
    } else {
      AddressYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromDateOfBirthPages(userAnswers: UserAnswers, mode: Mode): Call = {
    if (userAnswers.is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(mode)
    } else {
      NationalInsuranceNumberYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromCountryOfNationalityPages(userAnswers: UserAnswers, mode: Mode): Call = {
    if (userAnswers.isTaxable) {
      NationalInsuranceNumberYesNoController.onPageLoad(mode)
    } else {
      CountryOfResidenceYesNoController.onPageLoad(mode)
    }
  }

  private def navigateToIdQuestions(mode: Mode): Call = {
    if (mode == NormalMode) {
      PassportDetailsYesNoController.onPageLoad()
    } else {
      PassportOrIdCardDetailsYesNoController.onPageLoad()
    }
  }

  private def navigateToMentalCapacityOrWhenAddedPage(userAnswers: UserAnswers, mode: Mode): Call  = {
    if (userAnswers.is5mldEnabled) {
      MentalCapacityYesNoController.onPageLoad(mode)
    } else {
      navigateToWhenAddedOrCheckDetails(userAnswers, mode)
    }
  }

  private def navigateToWhenAddedOrCheckDetails(userAnswers: UserAnswers, mode: Mode): Call = {
    if (mode == NormalMode) {
      controllers.trustee.routes.WhenAddedController.onPageLoad()
    } else {
      checkDetailsNavigation(userAnswers)
    }
  }

  private def checkDetailsNavigation(userAnswers: UserAnswers): Call = {
    userAnswers.get(IndexPage) match {
      case Some(index) => controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
