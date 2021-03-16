/*
 * Copyright 2021 HM Revenue & Customs
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

import base.SpecBase
import models.IndividualOrBusiness.Business
import models.{CheckMode, Name, NonUkAddress, NormalMode, UkAddress, UserAnswers}
import pages.trustee.organisation._
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class TrusteeOrganisationPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val country = "DE"
  val trusteeNonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, country)

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(IndividualOrBusinessPage, Business).success.value
    .set(NamePage, name.displayName).success.value
    .set(UtrYesNoPage, true).success.value
    .set(UtrPage, "1234567890").success.value
    .set(CountryOfResidenceYesNoPage, true).success.value
    .set(CountryOfResidenceInTheUkYesNoPage, false).success.value
    .set(CountryOfResidencePage, country).success.value
    .set(AddressYesNoPage, true).success.value
    .set(AddressInTheUkYesNoPage, true).success.value
    .set(UkAddressPage, trusteeUkAddress).success.value
    .set(NonUkAddressPage, trusteeNonUkAddress).success.value
    .set(WhenAddedPage, LocalDate.of(2020, 1, 1)).success.value

  "TrusteeIndividualPrintHelper" must {

    "generate individual trustee section for all possible data" when {

      "adding" in {

        val provisional = true
        val mode = NormalMode

        val helper = injector.instanceOf[TrusteeOrganisationPrintHelper]

        val result = helper.print(userAnswers, provisional, name.displayName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.organisation.name.checkYourAnswersLabel")), answer = Html("First Last"), changeUrl = controllers.trustee.organisation.routes.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.utrYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.utr.checkYourAnswersLabel", name.displayName)), answer = Html("1234567890"), changeUrl = controllers.trustee.organisation.routes.UtrController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.addressInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.whenAdded.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = controllers.trustee.routes.WhenAddedController.onPageLoad().url)
          )
        )
      }

      "amending" in {

        val provisional = false
        val mode = CheckMode

        val helper = injector.instanceOf[TrusteeOrganisationPrintHelper]

        val result = helper.print(userAnswers, provisional, name.displayName)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustee.organisation.name.checkYourAnswersLabel")), answer = Html("First Last"), changeUrl = controllers.trustee.organisation.routes.NameController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.utrYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.utr.checkYourAnswersLabel", name.displayName)), answer = Html("1234567890"), changeUrl = controllers.trustee.organisation.routes.UtrController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
            //AnswerRow(label = Html(messages("trustee.organisation.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.addressInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode).url),
            AnswerRow(label = Html(messages("trustee.organisation.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode).url)
          )
        )
      }
    }
  }
}
