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
import models.{CheckMode, UserAnswers}
import pages.trustee.amend.individual._
import pages.trustee.individual.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage, CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage, MentalCapacityYesNoPage}
import play.api.i18n.Messages
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class AmendTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def print(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages): AnswerSection = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, trusteeName)
    val mode = CheckMode

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "trustee.individual.name", controllers.trustee.amend.individual.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "trustee.individual.dateOfBirthYesNo", controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "trustee.individual.dateOfBirth", controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad().url),

        bound.yesNoQuestion(CountryOfNationalityYesNoPage, "trustee.individual.countryOfNationalityYesNo", controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage, "trustee.individual.countryOfNationalityInTheUkYesNo", controllers.trustee.individual.routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, "trustee.individual.countryOfNationality", controllers.trustee.individual.routes.CountryOfNationalityController.onPageLoad(mode).url),

        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "trustee.individual.nationalInsuranceNumberYesNo", controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "trustee.individual.nationalInsuranceNumber", controllers.trustee.amend.individual.routes.NationalInsuranceNumberController.onPageLoad().url),

        bound.yesNoQuestion(CountryOfResidenceYesNoPage, "trustee.individual.countryOfResidenceYesNo", controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage, "trustee.individual.countryOfResidenceInTheUkYesNo", controllers.trustee.individual.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, "trustee.individual.countryOfResidence", controllers.trustee.individual.routes.CountryOfResidenceController.onPageLoad(mode).url),

        bound.yesNoQuestion(AddressYesNoPage, "trustee.individual.addressYesNo", controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "trustee.individual.liveInTheUkYesNo", controllers.trustee.amend.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustee.individual.ukAddress", controllers.trustee.amend.individual.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "trustee.individual.nonUkAddress", controllers.trustee.amend.individual.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "trustee.individual.passportOrIdCardDetailsYesNo", controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "trustee.individual.passportOrIdCardDetails", controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsController.onPageLoad().url),

        bound.yesNoQuestion(MentalCapacityYesNoPage, "trustee.individual.mentalCapacityYesNo", controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode).url)
      ).flatten
    )
  }
}
