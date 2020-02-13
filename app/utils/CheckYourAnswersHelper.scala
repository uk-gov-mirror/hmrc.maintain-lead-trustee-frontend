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

import controllers.leadtrustee.individual.routes
import models.{NormalMode, UserAnswers}
import pages.leadtrustee.individual._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.AnswerRow
import CheckYourAnswersHelper._
import pages.leadtrustee.individual.{AddressYesNoPagePage, DateOfBirthPage, DateOfBirthYesNoPagePage, IdCardDetailsPage, IdCardYesNoPagePage, LiveInTheUkYesNoPagePage, NamePage, NationalInsuranceNumberPage, NationalInsuranceNumberyesNoPagePage, NonUkAddressPage, PassportDetailsPage, PassportYesNoPagePage, UkAddressPage}

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def ukAddress: Option[AnswerRow] = userAnswers.get(UkAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("ukAddress.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.line1} ${x.line2}"),
        routes.UkAddressController.onPageLoad().url
      )
  }

  def passportYesNoPage: Option[AnswerRow] = userAnswers.get(PassportYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("passportYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.PassportYesNoPageController.onPageLoad().url
      )
  }

  def passportDetails: Option[AnswerRow] = userAnswers.get(PassportDetailsPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("passportDetails.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.PassportDetailsController.onPageLoad().url
      )
  }

  def nonUkAddress: Option[AnswerRow] = userAnswers.get(NonUkAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("nonUkAddress.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.line1} ${x.line2}"),
        routes.NonUkAddressController.onPageLoad().url
      )
  }

  def nationalInsuranceNumberyesNoPage: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberyesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("nationalInsuranceNumberyesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.NationalInsuranceNumberyesNoPageController.onPageLoad().url
      )
  }

  def nationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("nationalInsuranceNumber.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.NationalInsuranceNumberController.onPageLoad().url
      )
  }

  def name: Option[AnswerRow] = userAnswers.get(NamePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("name.checkYourAnswersLabel")),
        HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
        routes.NameController.onPageLoad().url
      )
  }

  def liveInTheUkYesNoPage: Option[AnswerRow] = userAnswers.get(LiveInTheUkYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("liveInTheUkYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.LiveInTheUkYesNoPageController.onPageLoad().url
      )
  }

  def idCardYesNoPage: Option[AnswerRow] = userAnswers.get(IdCardYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("idCardYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.IdCardYesNoPageController.onPageLoad().url
      )
  }

  def idCardDetails: Option[AnswerRow] = userAnswers.get(IdCardDetailsPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("idCardDetails.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.IdCardDetailsController.onPageLoad().url
      )
  }

  def dateOfBirthYesNoPage: Option[AnswerRow] = userAnswers.get(DateOfBirthYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("dateOfBirthYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.DateOfBirthYesNoPageController.onPageLoad().url
      )
  }

  def dateOfBirth: Option[AnswerRow] = userAnswers.get(DateOfBirthPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("dateOfBirth.checkYourAnswersLabel")),
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.DateOfBirthController.onPageLoad().url
      )
  }

  def addressYesNoPage: Option[AnswerRow] = userAnswers.get(AddressYesNoPagePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("addressYesNoPage.checkYourAnswersLabel")),
        yesOrNo(x),
        routes.AddressYesNoPageController.onPageLoad().url
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
