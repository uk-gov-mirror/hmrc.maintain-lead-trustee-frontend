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
import models.{CheckMode, UserAnswers}
import pages.trustee.amend.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object AmendIndividualTrusteeNavigator {

  final val mode = CheckMode

  private val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.DateOfBirthYesNoController.onPageLoad()
    case UkAddressPage => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
  }

  private val conditionalNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), navigateAwayFromDateOfBirthPages(ua))
    case DateOfBirthPage => ua =>
      navigateAwayFromDateOfBirthPages(ua)
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), navigateAwayFromNinoPages(ua))
    case NationalInsuranceNumberPage => ua =>
      navigateAwayFromNinoPages(ua)
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(), navigateToMentalCapacityOrCheckDetailsPage(ua))
    case LiveInTheUkYesNoPage => ua =>
      yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
    case PassportOrIdCardDetailsYesNoPage => ua =>
      yesNoNav(ua, PassportOrIdCardDetailsYesNoPage, rts.PassportOrIdCardDetailsController.onPageLoad(), navigateToMentalCapacityOrCheckDetailsPage(ua))
    case PassportOrIdCardDetailsPage => ua =>
      navigateToMentalCapacityOrCheckDetailsPage(ua)
  }

  private def navigateAwayFromDateOfBirthPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(mode)
    } else {
      rts.NationalInsuranceNumberYesNoController.onPageLoad()
    }
  }

  private def navigateAwayFromNinoPages(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      userAnswers.get(NationalInsuranceNumberYesNoPage) match {
        case Some(true) => navigateToMentalCapacityOrCheckDetailsPage(userAnswers)
        case _ => rts.AddressYesNoController.onPageLoad()
      }
    }
  }

  private def navigateToMentalCapacityOrCheckDetailsPage(userAnswers: UserAnswers)  = {
    if (userAnswers.is5mldEnabled) {
      controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode)
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

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
