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

import controllers.trustee.organisation.{routes => rts}
import models.{Mode, NormalMode, UserAnswers}
import pages.trustee.organisation._
import pages.trustee.organisation.amend.IndexPage
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object OrganisationTrusteeNavigator {

  private def conditionalNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => ua => navigateAwayFromNamePage(mode, ua)
    case UtrYesNoPage => ua =>
      yesNoNav(ua, UtrYesNoPage, rts.UtrController.onPageLoad(mode), navigateAwayFromUtrPages(mode, ua))
    case UtrPage => ua =>
      navigateAwayFromUtrPages(mode, ua)
    case CountryOfResidenceYesNoPage => ua =>
      yesNoNav(ua, CountryOfResidenceYesNoPage, rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode), navigateAwayFromResidencePages(mode, ua))
    case CountryOfResidenceInTheUkYesNoPage => ua =>
      yesNoNav(ua, CountryOfResidenceInTheUkYesNoPage, navigateAwayFromResidencePages(mode, ua), rts.CountryOfResidenceController.onPageLoad(mode))
    case CountryOfResidencePage => ua =>
      navigateAwayFromResidencePages(mode, ua)
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressInTheUkYesNoController.onPageLoad(mode), navigateToStartDateOrCheckDetails(mode, ua))
    case AddressInTheUkYesNoPage => ua =>
      yesNoNav(ua, AddressInTheUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
    case UkAddressPage | NonUkAddressPage => ua =>
      navigateToStartDateOrCheckDetails(mode, ua)
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
      conditionalNavigation(mode)

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def navigateAwayFromNamePage(mode: Mode, answers: UserAnswers): Call = {
    if (answers.is5mldEnabled && !answers.isTaxable) {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      rts.UtrYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromResidencePages(mode: Mode, answers: UserAnswers): Call = {
    val isNonTaxable5mld = answers.is5mldEnabled && !answers.isTaxable

    if (isNonTaxable5mld || isUtrDefined(answers)){
      navigateToStartDateOrCheckDetails(mode, answers)
    } else {
      rts.AddressYesNoController.onPageLoad(mode)
    }
  }

  private def navigateToStartDateOrCheckDetails(mode: Mode, answers: UserAnswers): Call = {
    if (mode == NormalMode) {
      controllers.trustee.routes.WhenAddedController.onPageLoad()
    } else {
      checkDetailsRoute(answers)
    }
  }

  private def checkDetailsRoute(answers: UserAnswers): Call = {
    answers.get(IndexPage) match {
      case Some(index) => controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def navigateAwayFromUtrPages(mode: Mode, answers: UserAnswers): Call = {
    (answers.is5mldEnabled, isUtrDefined(answers)) match {
      case (true, _) => rts.CountryOfResidenceYesNoController.onPageLoad(mode)
      case (false, true) => navigateToStartDateOrCheckDetails(mode, answers)
      case (false, _) => rts.AddressYesNoController.onPageLoad(mode)
    }
  }

  private def isUtrDefined(answers: UserAnswers): Boolean = answers.get(UtrYesNoPage).getOrElse(false)

}
