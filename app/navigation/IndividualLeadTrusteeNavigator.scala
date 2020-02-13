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

package navigation

import models.UserAnswers
import pages.{Page, QuestionPage}
import play.api.mvc.Call
import controllers.leadtrustee.individual.routes._
import pages.leadtrustee.individual._

object IndividualLeadTrusteeNavigator {
  private val simpleNavigations : PartialFunction[Page, Call] = {
    case NamePage =>  DateOfBirthYesNoPageController.onPageLoad()
    case DateOfBirthPage => NationalInsuranceNumberyesNoPageController.onPageLoad()
    case NationalInsuranceNumberPage => controllers.routes.CheckYourAnswersController.onPageLoad()
    case UkAddressPage => PassportYesNoPageController.onPageLoad()
    case NonUkAddressPage => PassportYesNoPageController.onPageLoad()
    case PassportDetailsPage => controllers.routes.CheckYourAnswersController.onPageLoad()
    case IdCardDetailsPage => controllers.routes.CheckYourAnswersController.onPageLoad()
  }

  private val yesNoNavigations : PartialFunction[Page, UserAnswers => Call] =
    yesNoNav(DateOfBirthYesNoPagePage, DateOfBirthController.onPageLoad(), NationalInsuranceNumberyesNoPageController.onPageLoad()) orElse
    yesNoNav(NationalInsuranceNumberyesNoPagePage, NationalInsuranceNumberController.onPageLoad(), AddressYesNoPageController.onPageLoad()) orElse
    yesNoNav(AddressYesNoPagePage, LiveInTheUkYesNoPageController.onPageLoad(), controllers.routes.CheckYourAnswersController.onPageLoad()) orElse
    yesNoNav(LiveInTheUkYesNoPagePage, UkAddressController.onPageLoad(), NonUkAddressController.onPageLoad()) orElse
    yesNoNav(PassportYesNoPagePage, PassportDetailsController.onPageLoad(), IdCardYesNoPageController.onPageLoad()) orElse
    yesNoNav(IdCardYesNoPagePage, IdCardDetailsController.onPageLoad(), controllers.routes.CheckYourAnswersController.onPageLoad())



  val routes: PartialFunction[Page, UserAnswers => Call] = simpleNavigations andThen (c => (_:UserAnswers) => c) orElse yesNoNavigations

  def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call) : PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` =>
      ua => ua.get(fromPage)
              .map(if (_) yesCall else noCall)
              .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
