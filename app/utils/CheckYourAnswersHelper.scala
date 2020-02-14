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

import java.time.format.DateTimeFormatter

import controllers.leadtrustee.individual.{routes => individualLeadtrusteeRoutes}
import models.UserAnswers
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.AnswerRow
import CheckYourAnswersHelper._
import pages.leadtrustee.individual._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def telephoneNumber: Option[AnswerRow] = userAnswers.get(TelephoneNumberPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("telephoneNumber.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        individualLeadtrusteeRoutes.TelephoneNumberController.onPageLoad().url
      )
  }

  def identificationDetailOptions: Option[AnswerRow] = userAnswers.get(IdentificationDetailOptionsPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("identificationDetailOptions.checkYourAnswersLabel")),
        HtmlFormat.escape(messages(s"identificationDetailOptions.$x")),
        individualLeadtrusteeRoutes.IdentificationDetailOptionsController.onPageLoad().url
      )
  }

  def emailAddressYesNo: Option[AnswerRow] = userAnswers.get(EmailAddressYesNoPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("emailAddressYesNo.checkYourAnswersLabel")),
        yesOrNo(x),
        individualLeadtrusteeRoutes.EmailAddressYesNoController.onPageLoad().url
      )
  }

  def emailAddress: Option[AnswerRow] = userAnswers.get(EmailAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("emailAddress.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        individualLeadtrusteeRoutes.EmailAddressController.onPageLoad().url
      )
  }

  def ukCitizen: Option[AnswerRow] = userAnswers.get(UkCitizenPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("ukCitizen.checkYourAnswersLabel")),
        yesOrNo(x),
        individualLeadtrusteeRoutes.UkCitizenController.onPageLoad().url
      )
  }

  def ukAddress: Option[AnswerRow] = userAnswers.get(UkAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("ukAddress.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.line1} ${x.line2}"),
        individualLeadtrusteeRoutes.UkAddressController.onPageLoad().url
      )
  }

  def passportDetails: Option[AnswerRow] = userAnswers.get(PassportDetailsPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("passportDetails.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        individualLeadtrusteeRoutes.PassportDetailsController.onPageLoad().url
      )
  }

  def nonUkAddress: Option[AnswerRow] = userAnswers.get(NonUkAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("nonUkAddress.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.line1} ${x.line2}"),
        individualLeadtrusteeRoutes.NonUkAddressController.onPageLoad().url
      )
  }

  def nationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("nationalInsuranceNumber.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        individualLeadtrusteeRoutes.NationalInsuranceNumberController.onPageLoad().url
      )
  }

  def name: Option[AnswerRow] = userAnswers.get(NamePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("name.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
        individualLeadtrusteeRoutes.NameController.onPageLoad().url
      )
  }

  def liveInTheUkYesNoPage: Option[AnswerRow] = userAnswers.get(LiveInTheUkYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("liveInTheUkYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        individualLeadtrusteeRoutes.LiveInTheUkYesNoPageController.onPageLoad().url
      )
  }

  def idCardDetails: Option[AnswerRow] = userAnswers.get(IdCardDetailsPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("idCardDetails.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        individualLeadtrusteeRoutes.IdCardDetailsController.onPageLoad().url
      )
  }

  def dateOfBirth: Option[AnswerRow] = userAnswers.get(DateOfBirthPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("dateOfBirth.checkYourAnswersLabel")),
        HtmlFormat.escape(x.format(dateFormatter)),
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

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
