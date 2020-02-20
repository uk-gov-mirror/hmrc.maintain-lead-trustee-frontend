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
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.individual.{DateOfBirthYesNoPage, NamePage}

class IndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator
  val index = 0

  "Individual trustee navigator" when {

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(NamePage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad(index))
    }

    "Do you know date of birth page -> Yes -> Date of birth page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage(index), true).success.value

      navigator.nextPage(DateOfBirthYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthController.onPageLoad(index))
    }
  }
}
