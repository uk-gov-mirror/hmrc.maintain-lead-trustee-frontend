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
import pages.trustee.organisation._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class TrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                               countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(NamePage, "trustee.organisation.name", controllers.trustee.organisation.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(UtrYesNoPage, "trustee.organisation.utrYesNo", controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad().url),
        bound.stringQuestion(UtrPage, "trustee.organisation.utr", controllers.trustee.organisation.routes.UtrController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "trustee.organisation.addressYesNo", controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressInTheUkYesNoPage, "trustee.organisation.addressInTheUkYesNo", controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustee.organisation.ukAddress", controllers.trustee.organisation.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "trustee.organisation.nonUkAddress", controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad().url),
        bound.dateQuestion(WhenAddedPage, "trustee.whenAdded", controllers.trustee.routes.WhenAddedController.onPageLoad().url)
      ).flatten
    )
  }
}
