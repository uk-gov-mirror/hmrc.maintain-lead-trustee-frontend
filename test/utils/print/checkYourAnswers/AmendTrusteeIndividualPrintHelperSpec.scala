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
import models.{CombinedPassportOrIdCard, Name, NonUkAddress, UkAddress}
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.amend.individual._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AmendTrusteeIndividualPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" in {

      val helper = injector.instanceOf[AmendTrusteeIndividualPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Individual).success.value
        .set(NamePage, name).success.value
        .set(DateOfBirthYesNoPage, true).success.value
        .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(NationalInsuranceNumberYesNoPage, true).success.value
        .set(NationalInsuranceNumberPage, "AA000000A").success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, trusteeUkAddress).success.value
        .set(NonUkAddressPage, trusteeNonUkAddress).success.value
        .set(PassportOrIdCardDetailsYesNoPage, true).success.value
        .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value

      val result = helper(userAnswers, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = controllers.trustee.amend.individual.routes.NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.trustee.amend.individual.routes.NationalInsuranceNumberController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.amend.individual.routes.UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.amend.individual.routes.NonUkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsController.onPageLoad().url)
        )
      )
    }
  }
}
