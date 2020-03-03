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

package navigation

import java.time.LocalDate

import base.SpecBase
import generators.Generators
import pages._
import models._
import controllers.routes.IndexController
import org.scalatest.prop.PropertyChecks

class NavigatorSpec extends SpecBase with PropertyChecks with Generators {

  implicit val navigator = new Navigator

  "Navigator" when {

      "navigating individual lead trustee change journey" when {
        import pages.leadtrustee.individual._
        import controllers.leadtrustee.individual.routes._

        "navigating away from the trustee name question should go to the Do you know Date of birth question" in {
          val value1 = DateOfBirthController.onPageLoad()
          navigator.nextPage(NamePage, UserAnswers("id", "UTRUTRUTR", LocalDate.now())) mustBe value1
        }
      }

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, UserAnswers("id", "UTRUTRUTR", LocalDate.now())) mustBe IndexController.onPageLoad("UTRUTRUTR")
      }
    
    }
}
