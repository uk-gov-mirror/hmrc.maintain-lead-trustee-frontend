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

import java.time.LocalDate

import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json, __}

class TrusteeSpec extends FreeSpec with MustMatchers {
  "Can read individual with no id from json" in {
    val jsonStr =
      """{
        |  "trusteeInd":{
        |    "name":{
        |      "firstName":"First",
        |      "lastName":"Last"
        |    },
        |    "entityStart":"2018-02-01",
        |    "provisional":true
        |    }
        |}""".stripMargin

    val json = Json.parse(jsonStr)

    val result = json.validate[Trustee]
    result mustBe JsSuccess(TrusteeIndividual(Name("First", None, "Last"), None, None, None, None, None, LocalDate.of(2018, 2, 1), true), __)

  }
}
