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

  final val mode = NormalMode

  val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.DateOfBirthYesNoController.onPageLoad()
    case UkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
  }

  val conditionalNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthPage => ua =>
      navigateAwayFromDateOfBirthPages(ua)
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), navigateAwayFromDateOfBirthPages(ua))
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), navigateAwayFromNinoPages(ua))
    case NationalInsuranceNumberPage => ua =>
      navigateAwayFromNinoPages(ua)
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(), navigateToMentalCapacityOrWhenAddedPage(ua))
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
    case PassportDetailsYesNoPage => ua =>
      yesNoNav(ua, PassportDetailsYesNoPage, rts.PassportDetailsController.onPageLoad(), rts.IdCardDetailsYesNoController.onPageLoad())
    case IdCardDetailsYesNoPage => ua =>
      yesNoNav(ua, IdCardDetailsYesNoPage, rts.IdCardDetailsController.onPageLoad(), navigateToMentalCapacityOrWhenAddedPage(ua))
    case PassportDetailsPage => ua => navigateToMentalCapacityOrWhenAddedPage(ua)
    case IdCardDetailsPage => ua => navigateToMentalCapacityOrWhenAddedPage(ua)
  }

  def navigateAwayFromDateOfBirthPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      rts.CountryOfNationalityYesNoController.onPageLoad(mode)
    } else {
      rts.NationalInsuranceNumberYesNoController.onPageLoad()
    }
  }

  def navigateAwayFromNinoPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      userAnswers.get(NationalInsuranceNumberYesNoPage) match {
        case Some(true) => navigateToMentalCapacityOrWhenAddedPage(userAnswers)
        case _ => rts.AddressYesNoController.onPageLoad()
      }
    }
  }

  def navigateToMentalCapacityOrWhenAddedPage(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      rts.MentalCapacityYesNoController.onPageLoad(mode)
    } else {
      controllers.trustee.routes.WhenAddedController.onPageLoad()
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
