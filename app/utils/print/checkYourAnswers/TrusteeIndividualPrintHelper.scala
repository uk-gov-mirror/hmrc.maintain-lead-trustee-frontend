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
import pages.trustee.WhenAddedPage
import pages.trustee.individual._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class TrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                             countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, trusteeName: String, index: Int)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage(index), "trustee.individual.name", controllers.trustee.individual.routes.NameController.onPageLoad(index).url),
        bound.yesNoQuestion(DateOfBirthYesNoPage(index), "trustee.individual.dateOfBirthYesNo", controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad(index).url),
        bound.dateQuestion(DateOfBirthPage(index), "trustee.individual.dateOfBirth", controllers.trustee.individual.routes.DateOfBirthController.onPageLoad(index).url),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage(index), "trustee.individual.nationalInsuranceNumberYesNo", controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index).url),
        bound.ninoQuestion(NationalInsuranceNumberPage(index), "trustee.individual.nationalInsuranceNumber", controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index).url),
        bound.yesNoQuestion(AddressYesNoPage(index), "trustee.individual.addressYesNo", controllers.trustee.individual.routes.AddressYesNoController.onPageLoad(index).url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage(index), "trustee.individual.liveInTheUkYesNo", controllers.trustee.individual.routes.LiveInTheUkYesNoController.onPageLoad(index).url),
        bound.addressQuestion(UkAddressPage(index), "trustee.individual.ukAddress", controllers.trustee.individual.routes.UkAddressController.onPageLoad(index).url),
        bound.addressQuestion(NonUkAddressPage(index), "trustee.individual.nonUkAddress", controllers.trustee.individual.routes.NonUkAddressController.onPageLoad(index).url),
        bound.yesNoQuestion(PassportDetailsYesNoPage(index), "trustee.individual.passportDetailsYesNo", controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad(index).url),
        bound.passportDetailsQuestion(PassportDetailsPage(index), "trustee.individual.passportDetails", controllers.trustee.individual.routes.PassportDetailsController.onPageLoad(index).url),
        bound.yesNoQuestion(IdCardDetailsYesNoPage(index), "trustee.individual.idCardDetailsYesNo", controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad(index).url),
        bound.idCardDetailsQuestion(IdCardDetailsPage(index), "trustee.individual.idCardDetails", controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad(index).url),
        bound.dateQuestion(WhenAddedPage(index), "trustee.whenAdded", controllers.trustee.routes.WhenAddedController.onPageLoad(index).url)
      ).flatten
    )
  }
}
