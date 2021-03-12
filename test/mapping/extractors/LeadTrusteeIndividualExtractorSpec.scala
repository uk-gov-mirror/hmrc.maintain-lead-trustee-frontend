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
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._

import java.time.LocalDate

class LeadTrusteeIndividualExtractorSpec extends SpecBase {

  private val name: Name = Name("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "country")
  private val phone: String = "tel"
  private val email: String = "email"
  private val nino: String = "nino"

  private val extractor: LeadTrusteeIndividualExtractor = new LeadTrusteeIndividualExtractor()

  "should populate user answers when trustee has a NINO and UK address" in {

    val identification = NationalInsuranceNumber(nino)

    val trustee = LeadTrusteeIndividual(
      name = name,
      dateOfBirth = date,
      phoneNumber = phone,
      email = None,
      identification = identification,
      address = ukAddress
    )

    val result = extractor.extract(emptyUserAnswers, trustee).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthPage).get mustBe date
    result.get(UkCitizenPage).get mustBe true
    result.get(NationalInsuranceNumberPage).get mustBe nino
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
    result.get(LiveInTheUkYesNoPage).get mustBe true
    result.get(UkAddressPage).get mustBe ukAddress
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(EmailAddressYesNoPage).get mustBe false
    result.get(EmailAddressPage) mustNot be(defined)
    result.get(TelephoneNumberPage).get mustBe phone

  }

  "should populate user answers when trustee has a passport/ID card, non-UK address and email" in {

    val combined = CombinedPassportOrIdCard("country", "number", date)

    val trustee = LeadTrusteeIndividual(
      name = name,
      dateOfBirth = date,
      phoneNumber = phone,
      email = Some(email),
      identification = combined,
      address = nonUkAddress
    )

    val result = extractor.extract(emptyUserAnswers, trustee).get

    result.get(IndividualOrBusinessPage).get mustBe Individual
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthPage).get mustBe date
    result.get(UkCitizenPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage).get mustBe combined
    result.get(LiveInTheUkYesNoPage).get mustBe false
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage).get mustBe nonUkAddress
    result.get(EmailAddressYesNoPage).get mustBe true
    result.get(EmailAddressPage).get mustBe email
    result.get(TelephoneNumberPage).get mustBe phone

  }
}
