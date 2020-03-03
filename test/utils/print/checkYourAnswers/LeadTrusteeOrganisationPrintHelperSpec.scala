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
import models.{NonUkAddress, UkAddress}
import pages.leadtrustee.organisation._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeOrganisationPrintHelperSpec extends SpecBase {

  val name: String = "Lead Trustee"
  val ukAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val nonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "LeadTrusteeOrganisationPrintHelper" must {

    "generate lead trustee organisation section for all possible data" in {

      val helper = injector.instanceOf[LeadTrusteeOrganisationPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(RegisteredInUkYesNoPage, true).success.value
        .set(NamePage, name).success.value
        .set(UtrPage, "utr").success.value
        .set(AddressInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value
        .set(EmailAddressYesNoPage, true).success.value
        .set(EmailAddressPage, "email").success.value
        .set(TelephoneNumberPage, "tel").success.value

      val result = helper(userAnswers, name)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("leadtrustee.organisation.registeredInUkYesNo.checkYourAnswersLabel")), answer = Html("Yes"), changeUrl = controllers.leadtrustee.organisation.routes.RegisteredInUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.name.checkYourAnswersLabel")), answer = Html("Lead Trustee"), changeUrl = controllers.leadtrustee.organisation.routes.NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.utr.checkYourAnswersLabel", name)), answer = Html("utr"), changeUrl = controllers.leadtrustee.organisation.routes.UtrController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.addressInTheUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = controllers.leadtrustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.ukAddress.checkYourAnswersLabel", name)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.leadtrustee.organisation.routes.UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.leadtrustee.organisation.routes.NonUkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.emailAddressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = controllers.leadtrustee.organisation.routes.EmailAddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.emailAddress.checkYourAnswersLabel", name)), answer = Html("email"), changeUrl = controllers.leadtrustee.organisation.routes.EmailAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("leadtrustee.organisation.telephoneNumber.checkYourAnswersLabel", name)), answer = Html("tel"), changeUrl = controllers.leadtrustee.organisation.routes.TelephoneNumberController.onPageLoad().url)
        )
      )
    }
  }
}
