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

package models

import generators.ModelGenerators
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NameSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with ModelGenerators {
  "displayName properties" in {
    forAll(arbitraryName.arbitrary) { (name:Name) =>
      name.displayName must startWith(name.firstName + " ")
      name.displayName must endWith(" " + name.lastName)

      if (name.middleName.isDefined) {
        name.middleName.foreach(mn => {
          name.displayName must include(mn)
          name.displayName.size mustBe name.firstName.size + mn.size + name.lastName.size + 2
        })
      } else {
        name.displayName.size mustBe name.firstName.size + name.lastName.size + 1
      }
    }
  }
}
