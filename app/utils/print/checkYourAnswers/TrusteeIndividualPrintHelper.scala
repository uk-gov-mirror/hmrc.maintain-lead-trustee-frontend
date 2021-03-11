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
import models.{NormalMode, UserAnswers}
import pages.trustee.WhenAddedPage
import pages.trustee.individual._
import play.api.i18n.Messages
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class TrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def print(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName)
    val mode = NormalMode

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "trustee.individual.name", controllers.trustee.individual.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "trustee.individual.dateOfBirthYesNo", controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "trustee.individual.dateOfBirth", controllers.trustee.individual.routes.DateOfBirthController.onPageLoad().url),

        bound.yesNoQuestion(CountryOfNationalityYesNoPage, "trustee.individual.countryOfNationalityYesNo", controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage, "trustee.individual.countryOfNationalityInTheUkYesNo", controllers.trustee.individual.routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, "trustee.individual.countryOfNationality", controllers.trustee.individual.routes.CountryOfNationalityController.onPageLoad(mode).url),

        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "trustee.individual.nationalInsuranceNumberYesNo", controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "trustee.individual.nationalInsuranceNumber", controllers.trustee.individual.routes.NationalInsuranceNumberController.onPageLoad().url),

        bound.yesNoQuestion(CountryOfResidenceYesNoPage, "trustee.individual.countryOfResidenceYesNo", controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage, "trustee.individual.countryOfResidenceInTheUkYesNo", controllers.trustee.individual.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, "trustee.individual.countryOfResidence", controllers.trustee.individual.routes.CountryOfResidenceController.onPageLoad(mode).url),

        bound.yesNoQuestion(AddressYesNoPage, "trustee.individual.addressYesNo", controllers.trustee.individual.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "trustee.individual.liveInTheUkYesNo", controllers.trustee.individual.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustee.individual.ukAddress", controllers.trustee.individual.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "trustee.individual.nonUkAddress", controllers.trustee.individual.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(PassportDetailsYesNoPage, "trustee.individual.passportDetailsYesNo", controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad().url),
        bound.passportDetailsQuestion(PassportDetailsPage, "trustee.individual.passportDetails", controllers.trustee.individual.routes.PassportDetailsController.onPageLoad().url),
        bound.yesNoQuestion(IdCardDetailsYesNoPage, "trustee.individual.idCardDetailsYesNo", controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad().url),
        bound.idCardDetailsQuestion(IdCardDetailsPage, "trustee.individual.idCardDetails", controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad().url),

        bound.yesNoQuestion(MentalCapacityYesNoPage, "trustee.individual.mentalCapacityYesNo", controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode).url),

        bound.dateQuestion(WhenAddedPage, "trustee.whenAdded", controllers.trustee.routes.WhenAddedController.onPageLoad().url)
      ).flatten
    )
  }
}
