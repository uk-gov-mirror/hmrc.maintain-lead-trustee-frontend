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

package utils

import java.time.LocalDate

import base.SpecBase
import models.{AllTrustees, Name, NationalInsuranceNumber, TrusteeIndividual}
import viewmodels.addAnother.AddRow

class AddATrusteeViewHelperSpec extends SpecBase {

  val trustees = List(
    TrusteeIndividual(
      name = Name(firstName = "First", middleName = None, lastName = "Last"),
      dateOfBirth = Some(LocalDate.parse("1983-09-24")),
      phoneNumber = None,
      identification = Some(NationalInsuranceNumber("JS123456A")),
      address = None,
      entityStart = LocalDate.parse("2019-02-28"),
      provisional = true
    )
  )

  "AddATrusteeViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, Nil)).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for trustees" ignore {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, Nil)).rows
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "Change details", "#", "Remove", "/maintain-a-trust/trustees/trustee/0/remove"),
          AddRow("First 1 Last 1", typeLabel = "Trustee", "Change details" ,"#",  "Remove", "/maintain-a-trust/trustees/trustee/1/remove"),
          AddRow("No name added", typeLabel = "Trustee", "Change details", "#", "Remove","/maintain-a-trust/trustees/trustee/2/remove")
        )
        rows.complete mustBe Nil
      }

      "generate rows complete trustees" in {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, trustees)).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Trustee Individual", "Change details", "/maintain-a-trust/trustees/trustee/0/check-details", "Remove", "/maintain-a-trust/trustees/trustee/0/remove")
        )
        rows.inProgress mustBe Nil
      }
      
      "generate rows from user answers for complete and in progress trustees" ignore {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, Nil)).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Lead Trustee Individual", "Change details", "#", "Remove","/maintain-a-trust/trustees/trustee/0/remove")
        )
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "Change details", "#", "Remove","/maintain-a-trust/trustees/trustee/0/remove")
        )
      }

    }
  }
}
