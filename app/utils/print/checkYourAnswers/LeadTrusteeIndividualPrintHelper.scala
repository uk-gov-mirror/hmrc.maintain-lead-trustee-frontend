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

import com.google.inject.Inject
import controllers.leadtrustee.individual.routes._
import models.UserAnswers
import pages.leadtrustee.individual._
import play.api.i18n.Messages
import utils.print.AnswerRowConverter
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def print(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName)

    val prefix: String = "leadtrustee.individual"

    def answerRows: Seq[AnswerRow] = Seq(
      bound.nameQuestion(NamePage, s"$prefix.name", NameController.onPageLoad().url),
      bound.dateQuestion(DateOfBirthPage, s"$prefix.dateOfBirth", DateOfBirthController.onPageLoad().url),
      bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage, s"$prefix.countryOfNationalityInTheUkYesNo", CountryOfNationalityInTheUkYesNoController.onPageLoad().url),
      bound.countryQuestion(CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, s"$prefix.countryOfNationality", CountryOfNationalityController.onPageLoad().url),
      bound.yesNoQuestion(UkCitizenPage, s"$prefix.ukCitizen", UkCitizenController.onPageLoad().url),
      bound.ninoQuestion(NationalInsuranceNumberPage, s"$prefix.nationalInsuranceNumber", NationalInsuranceNumberController.onPageLoad().url),
      bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, s"$prefix.passportOrIdCardDetails", PassportOrIdCardController.onPageLoad().url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage, s"$prefix.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad().url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, s"$prefix.countryOfResidence", CountryOfResidenceController.onPageLoad().url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, s"$prefix.liveInTheUkYesNo", LiveInTheUkYesNoController.onPageLoad().url),
      bound.addressQuestion(UkAddressPage, s"$prefix.ukAddress", UkAddressController.onPageLoad().url),
      bound.addressQuestion(NonUkAddressPage, s"$prefix.nonUkAddress", NonUkAddressController.onPageLoad().url),
      bound.yesNoQuestion(EmailAddressYesNoPage, s"$prefix.emailAddressYesNo", EmailAddressYesNoController.onPageLoad().url),
      bound.stringQuestion(EmailAddressPage, s"$prefix.emailAddress", EmailAddressController.onPageLoad().url),
      bound.stringQuestion(TelephoneNumberPage, s"$prefix.telephoneNumber", TelephoneNumberController.onPageLoad().url)
    ).flatten

    AnswerSection(headingKey = None, rows = answerRows)
  }
}
