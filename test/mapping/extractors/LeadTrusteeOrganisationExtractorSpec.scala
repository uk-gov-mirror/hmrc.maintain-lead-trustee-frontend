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
import models.{LeadTrusteeOrganisation, NonUkAddress, UkAddress}
import pages.leadtrustee.organisation._

class LeadTrusteeOrganisationExtractorSpec extends SpecBase {

  private val name: String = "Name"
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "country")
  private val phone: String = "tel"
  private val utr: String = "utr"
  private val email: String = "email"

  private val extractor = new LeadTrusteeOrganisationExtractor()

  "Lead trustee organisation extractor" must {

    "populate user answers" when {

      "trustee has a UTR, a UK address and no email" in {

        val trustee = LeadTrusteeOrganisation(
          name = name,
          phoneNumber = phone,
          email = None,
          utr = Some(utr),
          address = ukAddress
        )

        val result = extractor.extract(emptyUserAnswers, trustee).get

        result.get(RegisteredInUkYesNoPage).get mustBe true
        result.get(NamePage).get mustBe name
        result.get(UtrPage).get mustBe utr
        result.get(AddressInTheUkYesNoPage).get mustBe true
        result.get(UkAddressPage).get mustBe ukAddress
        result.get(NonUkAddressPage) mustNot be(defined)
        result.get(EmailAddressYesNoPage).get mustBe false
        result.get(EmailAddressPage) mustNot be(defined)
        result.get(TelephoneNumberPage).get mustBe phone
      }

      "trustee has no UTR, a non-UK address and an email" in {

        val trustee = LeadTrusteeOrganisation(
          name = name,
          phoneNumber = phone,
          email = Some(email),
          utr = None,
          address = nonUkAddress
        )

        val result = extractor.extract(emptyUserAnswers, trustee).get

        result.get(RegisteredInUkYesNoPage).get mustBe false
        result.get(NamePage).get mustBe name
        result.get(UtrPage) mustNot be(defined)
        result.get(AddressInTheUkYesNoPage).get mustBe false
        result.get(UkAddressPage) mustNot be(defined)
        result.get(NonUkAddressPage).get mustBe nonUkAddress
        result.get(EmailAddressYesNoPage).get mustBe true
        result.get(EmailAddressPage).get mustBe email
        result.get(TelephoneNumberPage).get mustBe phone
      }
    }
  }
}
