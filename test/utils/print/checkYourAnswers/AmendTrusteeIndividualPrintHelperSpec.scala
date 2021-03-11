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
import models.IndividualOrBusiness.Individual
import models.{CheckMode, CombinedPassportOrIdCard, Name, NonUkAddress, UkAddress}
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.amend.individual._
import pages.trustee.individual.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage, CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage, MentalCapacityYesNoPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AmendTrusteeIndividualPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  val country: String = "DE"

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section" when {
      "all possible data" in {

        val helper = injector.instanceOf[AmendTrusteeIndividualPrintHelper]
        val mode = CheckMode

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Individual).success.value
          .set(NamePage, name).success.value
          .set(DateOfBirthYesNoPage, true).success.value
          .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
          .set(CountryOfNationalityYesNoPage, true).success.value
          .set(CountryOfNationalityInTheUkYesNoPage, false).success.value
          .set(CountryOfNationalityPage, country).success.value
          .set(NationalInsuranceNumberYesNoPage, true).success.value
          .set(NationalInsuranceNumberPage, "AA000000A").success.value
          .set(CountryOfResidenceYesNoPage, true).success.value
          .set(CountryOfResidenceInTheUkYesNoPage, false).success.value
          .set(CountryOfResidencePage, country).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, trusteeUkAddress).success.value
          .set(NonUkAddressPage, trusteeNonUkAddress).success.value
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value
          .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
          .set(MentalCapacityYesNoPage, true).success.value
        val result = helper.print(userAnswers, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = controllers.trustee.amend.individual.routes.NameController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad().url),

            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.individual.routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationality.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = controllers.trustee.individual.routes.CountryOfNationalityController.onPageLoad(mode).url),

            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.trustee.amend.individual.routes.NationalInsuranceNumberController.onPageLoad().url),

            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.individual.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = controllers.trustee.individual.routes.CountryOfResidenceController.onPageLoad(mode).url),

            AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.amend.individual.routes.UkAddressController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.amend.individual.routes.NonUkAddressController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsController.onPageLoad().url),

            AnswerRow(label = Html(messages("trustee.individual.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode).url)
          )
        )
      }

      "partial data" in {

        val helper = injector.instanceOf[AmendTrusteeIndividualPrintHelper]

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Individual).success.value
          .set(NamePage, name).success.value
          .set(DateOfBirthYesNoPage, true).success.value
          .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(AddressYesNoPage, false).success.value

        val result = helper.print(userAnswers, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = controllers.trustee.amend.individual.routes.NameController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad().url)
          )
        )
      }

    }
  }
}
