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
import models.TrusteeStatus.Completed
import models.{IndividualOrBusiness, Name}
import pages.leadtrustee.individual.{NamePage => leadTrusteeNamePage}
import pages.leadtrustee.individual.{LeadTrusteeStatusPage => leadTrusteeStatusPage}
import pages.trustee.individual.NamePage
import pages.trustee.{IndividualOrBusinessPage, IsThisLeadTrusteePage, TrusteeStatusPage}
import viewmodels.addAnother.AddRow

class AddATrusteeViewHelperSpec extends SpecBase {

  val userAnswersWithTrusteesComplete = emptyUserAnswers
    .set(leadTrusteeNamePage, Name("First", None, "Last")).success.value
    .set(leadTrusteeStatusPage, Completed).success.value
    .set(IsThisLeadTrusteePage(0), false).success.value
    .set(IndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(NamePage(0), Name("First 0", None, "Last 0")).success.value
    .set(TrusteeStatusPage(0), Completed).success.value

  val userAnswersWithTrusteesInProgress = emptyUserAnswers
    .set(IsThisLeadTrusteePage(0), false).success.value
    .set(NamePage(0), Name("First 0", Some("Middle"), "Last 0")).success.value
    .set(IsThisLeadTrusteePage(1), false).success.value
    .set(NamePage(1), Name("First 1", Some("Middle"), "Last 1")).success.value
    .set(IsThisLeadTrusteePage(2),false).success.value

  val userAnswersWithCompleteAndInProgress = emptyUserAnswers
    .set(NamePage(0), Name("First 0", Some("Middle"), "Last 0")).success.value
    .set(leadTrusteeNamePage, Name("First", Some("Middle"), "Last")).success.value
    .set(leadTrusteeStatusPage, Completed).success.value

  val userAnswersWithNoTrustees = emptyUserAnswers

  "AddATrusteeViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithNoTrustees).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for trustees in progress" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesInProgress).rows
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/0/remove"),
          AddRow("First 1 Last 1", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/1/remove"),
          AddRow("No name added", typeLabel = "Trustee", "#", "/maintain-a-trust/trustees/trustee/2/remove")
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete trustees (Lead Trustee Individual)" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesComplete).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Lead Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/0/remove"),
          AddRow("First 0 Last 0", typeLabel = "Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/0/remove")
        )
        rows.inProgress mustBe Nil
      }
      
      "generate rows from user answers for complete and in progress trustees" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithCompleteAndInProgress).rows
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
