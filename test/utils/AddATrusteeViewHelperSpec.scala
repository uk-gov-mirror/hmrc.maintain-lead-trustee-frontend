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

import base.SpecBase
import models.{AllTrustees, Name, RemoveTrusteeIndividual, TrustIdentification, TrusteeType}
import org.joda.time.DateTime
import viewmodels.addAnother.AddRow

class AddATrusteeViewHelperSpec extends SpecBase {

  val userAnswersWithTrustees = List(TrusteeType(Some(RemoveTrusteeIndividual(
    lineNo = Some("1"),
    bpMatchStatus = Some("01"),
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = Some(DateTime.parse("1983-9-24")),
    phoneNumber = None,
    identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
    entityStart = DateTime.parse("2019-2-28"))), None))

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
          AddRow("First 0 Last 0", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/0/remove"),
          AddRow("First 1 Last 1", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/1/remove"),
          AddRow("No name added", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/2/remove")
        )
        rows.complete mustBe Nil
      }

      "generate rows complete trustees" in {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, userAnswersWithTrustees)).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/0/remove")
        )
        rows.inProgress mustBe Nil
      }
      
      "generate rows from user answers for complete and in progress trustees" ignore {
        val rows = new AddATrusteeViewHelper(AllTrustees(None, Nil)).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Lead Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/0/remove")
        )
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/0/remove")
        )
      }

    }
  }
}
