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

package mapping.extractors

import base.SpecBase
import models.IndividualOrBusiness.Individual
import models._
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.amend.individual._

import java.time.LocalDate

class TrusteeIndividualExtractorSpec extends SpecBase {

  private val index: Int = 0

  private val name: Name = Name("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "country")
  private val nino: String = "nino"

  private val extractor: TrusteeIndividualExtractor = new TrusteeIndividualExtractor()

  "should populate user answers when trustee has a NINO" in {

    val identification = NationalInsuranceNumber(nino)

    val trustee = TrusteeIndividual(
      name = name,
      dateOfBirth = Some(date),
      phoneNumber = None,
      identification = Some(identification),
      address = None,
      entityStart = date,
      provisional = true
    )

    val result = extractor.extract(emptyUserAnswers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe true
    result.get(NationalInsuranceNumberPage).get mustBe nino
    result.get(AddressYesNoPage) mustNot be(defined)
    result.get(LiveInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)

  }

  "should populate user answers when trustee has a UK address and passport/ID card" in {

    val combined = CombinedPassportOrIdCard("country", "number", date)

    val trustee = TrusteeIndividual(
      name = name,
      dateOfBirth = Some(date),
      phoneNumber = None,
      identification = Some(combined),
      address = Some(ukAddress),
      entityStart = date,
      provisional = true
    )

    val result = extractor.extract(emptyUserAnswers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(LiveInTheUkYesNoPage).get mustBe true
    result.get(UkAddressPage).get mustBe ukAddress
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
    result.get(PassportOrIdCardDetailsPage).get mustBe combined

  }

  "should populate user answers when trustee has a non-UK address" in {

    val trustee = TrusteeIndividual(
      name = name,
      dateOfBirth = Some(date),
      phoneNumber = None,
      identification = None,
      address = Some(nonUkAddress),
      entityStart = date,
      provisional = true
    )

    val result = extractor.extract(emptyUserAnswers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(LiveInTheUkYesNoPage).get mustBe false
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage).get mustBe nonUkAddress
    result.get(PassportOrIdCardDetailsYesNoPage) must not be defined
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)

  }

  "should populate user answers when trustee has minimum data" in {

    val trustee = TrusteeIndividual(
      name = name,
      dateOfBirth = None,
      phoneNumber = None,
      identification = None,
      address = None,
      entityStart = date,
      provisional = true
    )

    val result = extractor.extract(emptyUserAnswers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe false
    result.get(DateOfBirthPage) mustNot be(defined)
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe false
    result.get(LiveInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) must not be defined
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)

  }
}
