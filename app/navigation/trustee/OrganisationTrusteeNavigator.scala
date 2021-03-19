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

import controllers.trustee.organisation.add.{routes => addRts}
import controllers.trustee.organisation.amend.{routes => amendRts}
import controllers.trustee.organisation.{routes => rts}
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.trustee.organisation._
import pages.trustee.organisation.add.WhenAddedPage
import pages.trustee.organisation.amend.IndexPage
import play.api.mvc.Call

object OrganisationTrusteeNavigator extends TrusteeNavigator {

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    linearNavigation(mode) orElse
      yesNoNavigation(mode)
  }

  private def linearNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => ua => navigateAwayFromNamePage(mode, ua)
    case UtrPage => ua =>navigateAwayFromUtrPages(mode, ua)
    case CountryOfResidencePage => ua => navigateAwayFromResidencePages(mode, ua)
    case UkAddressPage | NonUkAddressPage => ua => navigateToStartDateOrCheckDetails(mode, ua)
    case WhenAddedPage => _ => addRts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {

    case UtrYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = UtrYesNoPage,
      yesCall = rts.UtrController.onPageLoad(mode),
      noCall = navigateAwayFromUtrPages(mode, ua)
    )
    case CountryOfResidenceYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfResidenceYesNoPage,
      yesCall = rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode),
      noCall = navigateAwayFromResidencePages(mode, ua)
    )
    case CountryOfResidenceInTheUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfResidenceInTheUkYesNoPage,
      yesCall = navigateAwayFromResidencePages(mode, ua),
      noCall = rts.CountryOfResidenceController.onPageLoad(mode)
    )
    case AddressYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = AddressYesNoPage,
      yesCall = rts.AddressInTheUkYesNoController.onPageLoad(mode),
      noCall = navigateToStartDateOrCheckDetails(mode, ua)
    )
    case AddressInTheUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = AddressInTheUkYesNoPage,
      yesCall = rts.UkAddressController.onPageLoad(mode),
      noCall = rts.NonUkAddressController.onPageLoad(mode)
    )
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
      addRts.WhenAddedController.onPageLoad()
    } else {
      checkDetailsRoute(answers)
    }
  }

  private def checkDetailsRoute(answers: UserAnswers): Call = {
    answers.get(IndexPage) match {
      case Some(index) => amendRts.CheckDetailsController.onPageLoadUpdated(index)
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
