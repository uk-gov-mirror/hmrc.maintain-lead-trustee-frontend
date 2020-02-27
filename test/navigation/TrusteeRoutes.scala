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

import generators.Generators
import models.{AddATrustee, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import sections.Trustees
import base.SpecBase
import pages.trustee.{AddATrusteePage, AddATrusteeYesNoPage, IsThisLeadTrusteePage}

trait AddATrusteeRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  val index = 0

  def addATrusteeRoutes()(implicit navigator: Navigator) = {

    "there are no trustees" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(AddATrusteePage, AddATrustee.YesNow).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteePage, answers)
              .mustBe(controllers.trustee.individual.routes.NameController.onPageLoad(index))
        }
      }

      "go to the next trustee from AddATrusteeYesNoPage when selecting yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, true).success.value
                .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, answers)
              .mustBe(controllers.trustee.individual.routes.NameController.onPageLoad(index))
        }
      }

      "go to the registration progress page from AddATrusteeYesNoPage when selecting no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, false).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, answers)
              .mustBe(controllers.routes.IndexController.onPageLoad(answers.utr))
        }
      }

    }

    "there is at least one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

            val answers = emptyUserAnswers
              .set(IsThisLeadTrusteePage(index), true).success.value
              .set(AddATrusteePage, AddATrustee.YesNow).success.value

            navigator.nextPage(AddATrusteePage, answers)
              .mustBe(controllers.trustee.individual.routes.NameController.onPageLoad(1))
      }

    }

    "go to Index page from AddATrusteePage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          navigator.nextPage(AddATrusteePage, answers)
            .mustBe(controllers.routes.IndexController.onPageLoad(answers.utr))
      }
    }

    "go to Index page from AddATrusteePage when selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          navigator.nextPage(AddATrusteePage, answers)
            .mustBe(controllers.routes.IndexController.onPageLoad(answers.utr))
      }
    }

  }
}
