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

import base.SpecBase
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.trustee.AddATrusteeYesNoPage

trait AddATrusteeRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def addATrusteeRoutes()(implicit navigator: Navigator) = {

    "there are no trustees" must {

      "go to the next trustee from AddATrusteeYesNoPage when selecting yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, true).success.value

            navigator.nextPage(AddATrusteeYesNoPage, answers)
              .mustBe(controllers.trustee.individual.routes.NameController.onPageLoad(0))
        }
      }

      "go to the registration progress page from AddATrusteeYesNoPage when selecting no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, false).success.value

            navigator.nextPage(AddATrusteeYesNoPage, answers)
              .mustBe(controllers.routes.IndexController.onPageLoad(answers.utr))
        }
      }

    }

  }
}
