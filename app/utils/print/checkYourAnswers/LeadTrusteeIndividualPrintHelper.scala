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
import models.UserAnswers
import pages.leadtrustee.individual._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class LeadTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                 countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "leadtrustee.individual.name", controllers.leadtrustee.individual.routes.NameController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "leadtrustee.individual.dateOfBirth", controllers.leadtrustee.individual.routes.DateOfBirthController.onPageLoad().url),
        bound.yesNoQuestion(UkCitizenPage, "leadtrustee.individual.ukCitizen", controllers.leadtrustee.individual.routes.UkCitizenController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "leadtrustee.individual.nationalInsuranceNumber", controllers.leadtrustee.individual.routes.NationalInsuranceNumberController.onPageLoad().url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "leadtrustee.individual.passportOrIdCardDetails", controllers.leadtrustee.individual.routes.PassportOrIdCardController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "leadtrustee.individual.liveInTheUkYesNo", controllers.leadtrustee.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "leadtrustee.individual.ukAddress", controllers.leadtrustee.individual.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "leadtrustee.individual.nonUkAddress", controllers.leadtrustee.individual.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(EmailAddressYesNoPage, "leadtrustee.individual.emailAddressYesNo", controllers.leadtrustee.individual.routes.EmailAddressYesNoController.onPageLoad().url),
        bound.stringQuestion(EmailAddressPage, "leadtrustee.individual.emailAddress", controllers.leadtrustee.individual.routes.EmailAddressController.onPageLoad().url),
        bound.stringQuestion(TelephoneNumberPage, "leadtrustee.individual.telephoneNumber", controllers.leadtrustee.individual.routes.TelephoneNumberController.onPageLoad().url)
      ).flatten
    )
  }
}
