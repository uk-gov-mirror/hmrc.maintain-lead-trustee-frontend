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

package utils.print.checkYourAnswers

import java.time.LocalDate

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{Name, NonUkAddress, NormalMode, UkAddress}
import pages.trustee.organisation._
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeOrganisationPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  val mode = NormalMode

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" in {

      val helper = injector.instanceOf[TrusteeOrganisationPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Business).success.value
        .set(NamePage, name.displayName).success.value
        .set(UtrYesNoPage, true).success.value
        .set(UtrPage, "1234567890").success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, trusteeUkAddress).success.value
        .set(NonUkAddressPage, trusteeNonUkAddress).success.value
        .set(WhenAddedPage, LocalDate.of(2020, 1, 1)).success.value

      val result = helper.print(userAnswers, true, name.displayName, mode)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("trustee.organisation.name.checkYourAnswersLabel")), answer = Html("First Last"), changeUrl = controllers.trustee.organisation.routes.NameController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.utrYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.utr.checkYourAnswersLabel", name.displayName)), answer = Html("1234567890"), changeUrl = controllers.trustee.organisation.routes.UtrController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.addressInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.organisation.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode).url),
          AnswerRow(label = Html(messages("trustee.whenAdded.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = controllers.trustee.routes.WhenAddedController.onPageLoad().url)
        )
      )
    }
  }
}
