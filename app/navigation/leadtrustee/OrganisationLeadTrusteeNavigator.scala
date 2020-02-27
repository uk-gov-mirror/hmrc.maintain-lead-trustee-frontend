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

import controllers.leadtrustee.organisation.{routes => rts}
import models.UserAnswers
import pages.leadtrustee.organisation._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

object OrganisationLeadTrusteeNavigator {

  private val simpleNavigations : PartialFunction[Page, Call] = {
    case UtrPage => rts.LiveInTheUkYesNoController.onPageLoad()
    case UkAddressPage => rts.UkAddressController.onPageLoad()
    case NonUkAddressPage => rts.NonUkAddressController.onPageLoad()
  }

  private val yesNoNavigations : PartialFunction[Page, UserAnswers => Call] =
    yesNoNav(RegisteredInUkYesNoPage, rts.NameController.onPageLoad(), rts.NameController.onPageLoad()) orElse
    yesNoNav(LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())

  private val conditionalNavigations : PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => nameNavigation
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigations andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigations orElse
    conditionalNavigations

  private def nameNavigation(userAnswers: UserAnswers): Call = {
    userAnswers.get(RegisteredInUkYesNoPage).map {
      case true => rts.UtrController.onPageLoad()
      case false => rts.LiveInTheUkYesNoController.onPageLoad()
    }.getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call) : PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` =>
      ua => ua.get(fromPage)
              .map(if (_) yesCall else noCall)
              .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
