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

import base.SpecBase
import controllers.trustee.individual.add.{routes => addRts}
import controllers.trustee.individual.amend.{routes => amendRts}
import controllers.trustee.individual.{routes => rts}
import models.IndividualOrBusiness.Individual
import models._
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.individual._
import pages.trustee.individual.add._
import pages.trustee.individual.amend._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class TrusteeIndividualPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  val country: String = "DE"

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" when {

      "adding" in {

        val provisional = true
        val mode = NormalMode

        val helper = injector.instanceOf[TrusteeIndividualPrintHelper]

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
          .set(PassportDetailsYesNoPage, true).success.value
          .set(PassportDetailsPage, Passport("GB", "1", LocalDate.of(2030, 10, 10))).success.value
          .set(IdCardDetailsYesNoPage, true).success.value
          .set(IdCardDetailsPage, IdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
          .set(MentalCapacityYesNoPage, true).success.value
          .set(WhenAddedPage, LocalDate.of(2020, 1, 1)).success.value

        val result = helper.print(userAnswers, provisional, name.displayName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.DateOfBirthYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.CountryOfNationalityYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationality.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = rts.CountryOfNationalityController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = rts.NationalInsuranceNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.CountryOfResidenceYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = rts.CountryOfResidenceController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.AddressYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = rts.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = rts.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.passportDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = addRts.PassportDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.passportDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = addRts.PassportDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = addRts.IdCardDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.idCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = addRts.IdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.MentalCapacityYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.whenAdded.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = addRts.WhenAddedController.onPageLoad().url)
          )
        )
      }

      "amending" in {

        val provisional = false
        val mode = CheckMode

        val helper = injector.instanceOf[TrusteeIndividualPrintHelper]

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

        val result = helper.print(userAnswers, provisional, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = rts.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.DateOfBirthYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = rts.DateOfBirthController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.CountryOfNationalityYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfNationality.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = rts.CountryOfNationalityController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = rts.NationalInsuranceNumberController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.CountryOfResidenceYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = rts.CountryOfResidenceController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.AddressYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = rts.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = rts.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = amendRts.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = amendRts.PassportOrIdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("trustee.individual.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = rts.MentalCapacityYesNoController.onPageLoad(mode).url)
          )
        )
      }
    }
  }
}
