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

package viewmodels.leadtrustee.individual

import java.time.format.DateTimeFormatter

import controllers.leadtrustee.individual.{routes => individualLeadtrusteeRoutes}
import javax.inject.Inject
import models.UserAnswers
import pages.leadtrustee.individual._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import utils.CheckAnswersFormatters
import viewmodels.AnswerRow

class CheckYourAnswersHelper @Inject()(formatter: CheckAnswersFormatters) {
  def bind(userAnswers: UserAnswers, leadTrusteeName: String)(implicit messages: Messages): Bound = new Bound(formatter, userAnswers, leadTrusteeName)

  class Bound(formatter: CheckAnswersFormatters, userAnswers: UserAnswers, leadTrusteeName: String)(implicit messages: Messages) {

    def telephoneNumber: Option[AnswerRow] = userAnswers.get(TelephoneNumberPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.telephoneNumber.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(x),
          individualLeadtrusteeRoutes.TelephoneNumberController.onPageLoad().url
        )
    }

    def identificationDetailOptions: Option[AnswerRow] = userAnswers.get(IdentificationDetailOptionsPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.identificationDetailOptions.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(messages(s"identificationDetailOptions.$x")),
          individualLeadtrusteeRoutes.IdentificationDetailOptionsController.onPageLoad().url
        )
    }

    def emailAddressYesNo: Option[AnswerRow] = userAnswers.get(EmailAddressYesNoPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.emailAddressYesNo.checkYourAnswersLabel", leadTrusteeName)),
          yesOrNo(x),
          individualLeadtrusteeRoutes.EmailAddressYesNoController.onPageLoad().url
        )
    }

    def emailAddress: Option[AnswerRow] = userAnswers.get(EmailAddressPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.emailAddress.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(x),
          individualLeadtrusteeRoutes.EmailAddressController.onPageLoad().url
        )
    }

    def ukCitizen: Option[AnswerRow] = userAnswers.get(UkCitizenPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.ukCitizen.checkYourAnswersLabel", leadTrusteeName)),
          yesOrNo(x),
          individualLeadtrusteeRoutes.UkCitizenController.onPageLoad().url
        )
    }

    def ukAddress: Option[AnswerRow] = userAnswers.get(UkAddressPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.ukAddress.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(s"${x.line1} ${x.line2}"),
          individualLeadtrusteeRoutes.UkAddressController.onPageLoad().url
        )
    }

    def passportDetails: Option[AnswerRow] = userAnswers.get(PassportDetailsPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.passportDetails.checkYourAnswersLabel", leadTrusteeName)),
          formatter.passport(x),
          individualLeadtrusteeRoutes.PassportDetailsController.onPageLoad().url
        )
    }

    def nonUkAddress: Option[AnswerRow] = userAnswers.get(NonUkAddressPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.nonUkAddress.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(s"${x.line1} ${x.line2}"),
          individualLeadtrusteeRoutes.NonUkAddressController.onPageLoad().url
        )
    }

    def nationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.nationalInsuranceNumber.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(x),
          individualLeadtrusteeRoutes.NationalInsuranceNumberController.onPageLoad().url
        )
    }

    def name: Option[AnswerRow] = userAnswers.get(NamePage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.name.checkYourAnswersLabel")),
          HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
          individualLeadtrusteeRoutes.NameController.onPageLoad().url
        )
    }

    def liveInTheUkYesNoPage: Option[AnswerRow] = userAnswers.get(LiveInTheUkYesNoPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.liveInTheUkYesNoPage.checkYourAnswersLabel", leadTrusteeName)),
          yesOrNo(x),
          individualLeadtrusteeRoutes.LiveInTheUkYesNoPageController.onPageLoad().url
        )
    }

    def idCardDetails: Option[AnswerRow] = userAnswers.get(IdCardDetailsPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.idCardDetails.checkYourAnswersLabel", leadTrusteeName)),
          formatter.idCard(x),
          individualLeadtrusteeRoutes.IdCardDetailsController.onPageLoad().url
        )
    }

    def dateOfBirth: Option[AnswerRow] = userAnswers.get(DateOfBirthPage) map {
      x =>
        AnswerRow(
          HtmlFormat.escape(messages("leadtrustee.individual.dateOfBirth.checkYourAnswersLabel", leadTrusteeName)),
          HtmlFormat.escape(x.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))),
          individualLeadtrusteeRoutes.DateOfBirthController.onPageLoad().url
        )
    }

    private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
      if (answer) {
        HtmlFormat.escape(messages("site.yes"))
      } else {
        HtmlFormat.escape(messages("site.no"))
      }
  }

}
