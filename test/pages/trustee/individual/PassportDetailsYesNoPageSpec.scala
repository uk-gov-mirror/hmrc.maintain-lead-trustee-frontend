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

package pages.trustee.individual

import java.time.LocalDate

import models.PassportOrIdCardDetails
import pages.behaviours.PageBehaviours

class PassportDetailsYesNoPageSpec extends PageBehaviours {

  val index = 0

  val data: PassportOrIdCardDetails = PassportOrIdCardDetails("country", "number", LocalDate.of(2020, 1, 1))

  "PassportDetailsYesNo page" must {

    beRetrievable[Boolean](PassportDetailsYesNoPage(0))

    beSettable[Boolean](PassportDetailsYesNoPage(0))

    beRemovable[Boolean](PassportDetailsYesNoPage(0))

    "implement cleanup logic when YES selected" in {
      val userAnswers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage(index), true)
        .flatMap(_.set(IdCardDetailsPage(index), data))
        .flatMap(_.set(PassportDetailsYesNoPage(index), true))

      userAnswers.get.get(IdCardDetailsYesNoPage(index)) mustNot be(defined)
      userAnswers.get.get(IdCardDetailsPage(index)) mustNot be(defined)
    }

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(PassportDetailsPage(index), data)
        .flatMap(_.set(PassportDetailsYesNoPage(index), false))

      userAnswers.get.get(PassportDetailsPage(index)) mustNot be(defined)
    }
  }
}
