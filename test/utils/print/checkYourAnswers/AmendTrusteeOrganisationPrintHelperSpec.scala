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

package utils.print.checkYourAnswers

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{Name, NonUkAddress, UkAddress}
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.amend.organisation._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AmendTrusteeOrganisationPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" in {

      val helper = injector.instanceOf[AmendTrusteeOrganisationPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Business).success.value
        .set(NamePage, name.displayName).success.value
        .set(UtrYesNoPage, true).success.value
        .set(UtrPage, "1234567890").success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, trusteeUkAddress).success.value
        .set(NonUkAddressPage, trusteeNonUkAddress).success.value

      val result = helper(userAnswers, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("trustee.organisation.name.checkYourAnswersLabel")), answer = Html("First Last"), changeUrl = controllers.trustee.amend.organisation.routes.NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.utrYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.organisation.routes.UtrYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.utr.checkYourAnswersLabel", name.displayName)), answer = Html("1234567890"), changeUrl = controllers.trustee.amend.organisation.routes.UtrController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.organisation.routes.AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.addressInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.organisation.routes.AddressInTheUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.amend.organisation.routes.UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.organisation.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.amend.organisation.routes.NonUkAddressController.onPageLoad().url)
        )
      )
    }
  }
}
