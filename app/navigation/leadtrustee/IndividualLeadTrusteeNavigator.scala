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

package navigation.leadtrustee

import controllers.leadtrustee.individual.{routes => rts}
import controllers.leadtrustee.{routes => leadTrusteeRoutes}
import models.UserAnswers
import pages.leadtrustee.individual._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object IndividualLeadTrusteeNavigator {
  private val simpleNavigations : PartialFunction[Page, Call] = {
    case NamePage => rts.DateOfBirthController.onPageLoad()
    case DateOfBirthPage => rts.UkCitizenController.onPageLoad()
    case PassportOrIdCardDetailsPage => rts.LiveInTheUkYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => rts.LiveInTheUkYesNoController.onPageLoad()
    case UkAddressPage => rts.EmailAddressYesNoController.onPageLoad()
    case NonUkAddressPage => rts.EmailAddressYesNoController.onPageLoad()
    case EmailAddressPage => rts.TelephoneNumberController.onPageLoad()
    case TelephoneNumberPage => leadTrusteeRoutes.CheckDetailsController.onPageLoadIndividualUpdated()
  }

  private val yesNoNavigations : PartialFunction[Page, UserAnswers => Call] =
    yesNoNav(UkCitizenPage, rts.NationalInsuranceNumberController.onPageLoad(), rts.PassportOrIdCardController.onPageLoad()) orElse
    yesNoNav(LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad()) orElse
    yesNoNav(EmailAddressYesNoPage, rts.EmailAddressController.onPageLoad(), rts.TelephoneNumberController.onPageLoad())


  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigations andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigations

  def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call) : PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` =>
      ua => ua.get(fromPage)
              .map(if (_) yesCall else noCall)
              .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

}
