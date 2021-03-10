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

import controllers.trustee.individual.{routes => rts}
import controllers.trustee.amend.individual.{routes => amendRts}
import models.{Mode, NormalMode, UserAnswers}
import pages.trustee.amend.individual.IndexPage
import pages.trustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualTrusteeNavigator {

  def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case CountryOfNationalityPage => ua => navigateAwayFromCountryOfNationalityPages(mode, ua)
    case CountryOfResidencePage => ua => navigateAwayFromCountryOfResidencePages(mode, ua)
  }

  def conditionalNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    case CountryOfNationalityYesNoPage => ua =>
      yesNoNav(ua,
        CountryOfNationalityYesNoPage,
        rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode),
        navigateAwayFromCountryOfNationalityPages(mode, ua)
      )
    case CountryOfNationalityInTheUkYesNoPage => ua =>
      yesNoNav(ua,
        CountryOfNationalityInTheUkYesNoPage,
        navigateAwayFromCountryOfNationalityPages(mode, ua),
        rts.CountryOfNationalityController.onPageLoad(mode)
      )
    case CountryOfResidenceYesNoPage => ua =>
      yesNoNav(ua,
        CountryOfResidenceYesNoPage,
        rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode),
        navigateAwayFromCountryOfResidencePages(mode, ua)
      )
    case CountryOfResidenceInTheUkYesNoPage => ua =>
      yesNoNav(ua,
        CountryOfResidenceInTheUkYesNoPage,
        navigateAwayFromCountryOfResidencePages(mode, ua),
        rts.CountryOfResidenceController.onPageLoad(mode)
      )
    case MentalCapacityYesNoPage => ua =>
      yesNoNav(ua,
        MentalCapacityYesNoPage,
        navigateAwayFromMentalCapacityPage(mode, ua),
        navigateAwayFromMentalCapacityPage(mode, ua)
      )
  }

  def navigateAwayFromCountryOfNationalityPages(mode: Mode, userAnswers: UserAnswers)  = {
    (userAnswers.isTaxable, mode) match {
      case (false, _) =>
        rts.CountryOfResidenceYesNoController.onPageLoad(mode)
      case (true, NormalMode) =>
        rts.NationalInsuranceNumberYesNoController.onPageLoad()
      case (true, _) =>
        amendRts.NationalInsuranceNumberYesNoController.onPageLoad()
    }
  }

  def navigateAwayFromCountryOfResidencePages(mode: Mode, userAnswers: UserAnswers)  = {
    (userAnswers.isTaxable, mode) match {
      case (false, _) =>
        rts.MentalCapacityYesNoController.onPageLoad(mode)
      case (true, NormalMode) =>
        rts.PassportDetailsYesNoController.onPageLoad()
      case (true, _) =>
        amendRts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    }
  }

  def navigateAwayFromMentalCapacityPage(mode: Mode, userAnswers: UserAnswers)  = {
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

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      conditionalNavigation(mode)

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
