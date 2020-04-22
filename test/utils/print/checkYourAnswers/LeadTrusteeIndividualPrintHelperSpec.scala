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

import java.time.LocalDate

import base.SpecBase
import models.IndividualOrBusiness.Individual
import models.{CombinedPassportOrIdCard, IdCard, IdentificationDetailOptions, Name, NonUkAddress, Passport, UkAddress}
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeIndividualPrintHelperSpec extends SpecBase {

  val name: Name = Name("Lead", None, "Trustee")
  val ukAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val nonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "LeadTrusteeIndividualPrintHelper" must {

    "generate lead trustee organisation section" when {
      "for all possible data" in {

        val helper = injector.instanceOf[LeadTrusteeIndividualPrintHelper]

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, LocalDate.of(1996, 2, 3)).success.value
          .set(UkCitizenPage, true).success.value
          .set(NationalInsuranceNumberPage, "AA000000A").success.value
          .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("DE", "number", LocalDate.of(1996, 2, 3))).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value
          .set(EmailAddressYesNoPage, true).success.value
          .set(EmailAddressPage, "email").success.value
          .set(TelephoneNumberPage, "tel").success.value

        val result = helper(userAnswers, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("leadtrustee.individual.name.checkYourAnswersLabel")), answer = Html("Lead Trustee"), changeUrl = controllers.leadtrustee.individual.routes.NameController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("3 February 1996"), changeUrl = controllers.leadtrustee.individual.routes.DateOfBirthController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.ukCitizen.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.leadtrustee.individual.routes.UkCitizenController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.leadtrustee.individual.routes.NationalInsuranceNumberController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("Germany<br />number<br />3 February 1996"), changeUrl = controllers.leadtrustee.individual.routes.PassportOrIdCardController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.leadtrustee.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.leadtrustee.individual.routes.UkAddressController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.leadtrustee.individual.routes.NonUkAddressController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.emailAddressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.leadtrustee.individual.routes.EmailAddressYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.emailAddress.checkYourAnswersLabel", name.displayName)), answer = Html("email"), changeUrl = controllers.leadtrustee.individual.routes.EmailAddressController.onPageLoad().url),
            AnswerRow(label = Html(messages("leadtrustee.individual.telephoneNumber.checkYourAnswersLabel", name.displayName)), answer = Html("tel"), changeUrl = controllers.leadtrustee.individual.routes.TelephoneNumberController.onPageLoad().url)
          )
        )
      }
    }
  }
}
