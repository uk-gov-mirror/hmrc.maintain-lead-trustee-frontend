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

import com.google.inject.Inject
import models.UserAnswers
import pages.trustee.amend.individual._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class AmendTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                  countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, trusteeName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "trustee.individual.name", controllers.trustee.amend.individual.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "trustee.individual.dateOfBirthYesNo", controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "trustee.individual.dateOfBirth", controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad().url),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "trustee.individual.nationalInsuranceNumberYesNo", controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "trustee.individual.nationalInsuranceNumber", controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "trustee.individual.addressYesNo", controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "trustee.individual.liveInTheUkYesNo", controllers.trustee.amend.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustee.individual.ukAddress", controllers.trustee.amend.individual.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "trustee.individual.nonUkAddress", controllers.trustee.amend.individual.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "trustee.individual.passportOrIdCardDetailsYesNo", controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "trustee.individual.passportOrIdCardDetails", controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsController.onPageLoad().url)
      ).flatten
    )
  }
}
