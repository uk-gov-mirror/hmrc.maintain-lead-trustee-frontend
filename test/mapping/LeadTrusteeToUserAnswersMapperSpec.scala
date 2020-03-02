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

package mapping

import java.time.LocalDate

import generators.ModelGenerators
import models.{IdCard, Passport, UserAnswers}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

import scala.util.{Failure, Success}

class LeadTrusteeToUserAnswersMapperSpec extends FreeSpec with ScalaCheckPropertyChecks with ModelGenerators with MustMatchers {
  "should round trip through user answers  except Passports & Id cards will be unified into CombinedPassportOrId" in {
    forAll(arbitraryLeadTrusteeIndividual.arbitrary) { lt =>
      val mapper = new LeadTrusteeToUserAnswersMapper()
      val userAnswers = new UserAnswers("Id", "UTRUTRUTR", LocalDate.of(1987, 12, 31), Json.obj())
      val expectedId = lt.identification match {
        case p:Passport => p.asCombined
        case id:IdCard => id.asCombined
        case x => x
      }

      mapper.populateUserAnswers(userAnswers, lt) match {
        case Failure(_) => fail("Setting user answers failed")
        case Success(ua) => mapper.getFromUserAnswers(ua) mustBe Some(lt.copy(identification = expectedId))
      }
    }
  }
}
