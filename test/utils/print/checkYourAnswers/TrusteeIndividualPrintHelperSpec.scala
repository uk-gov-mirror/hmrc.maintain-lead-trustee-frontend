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

package utils.print.checkYourAnswers

import java.time.LocalDate

import base.SpecBase
import models.IndividualOrBusiness.Individual
import models.{IdCard, Name, NonUkAddress, Passport, UkAddress}
import pages.trustee.individual._
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.i18n.Messages
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeIndividualPrintHelperSpec extends SpecBase {

  val index = 0
  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" in {

      val helper = injector.instanceOf[TrusteeIndividualPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage(index), Individual).success.value
        .set(NamePage(index), name).success.value
        .set(DateOfBirthYesNoPage(index), true).success.value
        .set(DateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value
        .set(NationalInsuranceNumberYesNoPage(index), true).success.value
        .set(NationalInsuranceNumberPage(index), "AA000000A").success.value
        .set(AddressYesNoPage(index), true).success.value
        .set(LiveInTheUkYesNoPage(index), true).success.value
        .set(UkAddressPage(index), trusteeUkAddress).success.value
        .set(NonUkAddressPage(index), trusteeNonUkAddress).success.value
        .set(PassportDetailsYesNoPage(index), true).success.value
        .set(PassportDetailsPage(index), Passport("1", LocalDate.of(2030, 10, 10), "GB")).success.value
        .set(IdCardDetailsYesNoPage(index), true).success.value
        .set(IdCardDetailsPage(index), IdCard("1", LocalDate.of(2030, 10, 10), "GB")).success.value
        .set(WhenAddedPage(index), LocalDate.of(2020, 1, 1)).success.value

      val result = helper(userAnswers, name.displayName, index)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("trustee.individual.name.checkYourAnswersLabel")), answer = Html("First Last"), changeUrl = controllers.trustee.individual.routes.NameController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.trustee.individual.routes.DateOfBirthController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.AddressYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.LiveInTheUkYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.individual.routes.UkAddressController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.individual.routes.NonUkAddressController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.passportDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.passportDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.trustee.individual.routes.PassportDetailsController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.individual.idCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad(index).url),
          AnswerRow(label = Html(messages("trustee.whenAdded.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = controllers.trustee.routes.WhenAddedController.onPageLoad(index).url)
        )
      )
    }
  }
}
