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

package mapping

import java.time.LocalDate

import generators.ModelGenerators
import models.IndividualOrBusiness.Business
import models.{TrustIdentificationOrgType, TrusteeOrganisation, UkAddress, UserAnswers}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.amend.organisation._
import play.api.libs.json.Json

class TrusteeOrganisationExtractorSpec extends FreeSpec with ScalaCheckPropertyChecks with ModelGenerators with MustMatchers {

  val answers = UserAnswers("Id", "UTRUTRUTR", LocalDate.of(1987, 12, 31), Json.obj())
  val index = 0

  val name = "First Last"
  val date = LocalDate.parse("1996-02-03")
  val address = UkAddress("Line 1", "Line 2", None, None, "postcode")

  val extractor = new TrusteeOrganisationExtractor()

  "should populate user answers when trustee has a UTR" in {

    val trustee = TrusteeOrganisation(
      name = name,
      phoneNumber = None,
      email = None,
      identification = Some(TrustIdentificationOrgType(None, Some("utr"), None)),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Business
    result.get(NamePage).get mustBe name
    result.get(UtrYesNoPage).get mustBe true
    result.get(UtrPage).get mustBe "utr"
    result.get(AddressYesNoPage) mustNot be(defined)
    result.get(AddressInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)

  }

  "should populate user answers when trustee has an address" in {

    val trustee = TrusteeOrganisation(
      name = name,
      phoneNumber = None,
      email = None,
      identification = Some(TrustIdentificationOrgType(None, None, Some(address))),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Business
    result.get(NamePage).get mustBe name
    result.get(UtrYesNoPage).get mustBe false
    result.get(UtrPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(AddressInTheUkYesNoPage).get mustBe true
    result.get(UkAddressPage).get mustBe address
    result.get(NonUkAddressPage) mustNot be(defined)

  }

  "should populate user answers when trustee has no UTR or address" in {

    val trustee = TrusteeOrganisation(
      name = name,
      phoneNumber = None,
      email = None,
      identification = Some(TrustIdentificationOrgType(None, None, None)),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, trustee, index).get

    result.get(IndividualOrBusinessPage).get mustBe Business
    result.get(NamePage).get mustBe name
    result.get(UtrYesNoPage).get mustBe false
    result.get(UtrPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe false
    result.get(AddressInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)

  }

}
