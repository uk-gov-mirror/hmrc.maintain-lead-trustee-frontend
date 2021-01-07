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

package models

import generators.ModelGenerators
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class LeadTrusteeSpec extends FreeSpec with ModelGenerators with MustMatchers with ScalaCheckPropertyChecks{
  "JSON roundtrips with individual except Passports & Id cards will be unified into CombinedPassportOrId" in {
    forAll(arbitraryLeadTrusteeIndividual.arbitrary) { lti =>
      val expectedId = lti.identification match {
        case p:Passport => p.asCombined
        case id:IdCard => id.asCombined
        case x => x
      }

      Json.toJson(lti.asInstanceOf[LeadTrustee])(LeadTrustee.writes).as[LeadTrustee](LeadTrustee.reads) mustBe lti.copy(identification = expectedId)
    }
  }

  "JSON roundtrips with organisation" in {
    forAll(arbitraryLeadTrusteeOrganisation.arbitrary) { lto =>

      Json.toJson(lto.asInstanceOf[LeadTrustee])(LeadTrustee.writes).as[LeadTrustee](LeadTrustee.reads) mustBe lto
    }
  }
}
