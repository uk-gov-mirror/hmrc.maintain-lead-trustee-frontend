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
import pages.leadtrustee.organisation._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection

class LeadTrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                   countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, trusteeName: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.yesNoQuestion(RegisteredInUkYesNoPage, "leadtrustee.organisation.registeredInUkYesNo", controllers.leadtrustee.organisation.routes.RegisteredInUkYesNoController.onPageLoad().url),
        bound.stringQuestion(NamePage, "leadtrustee.organisation.name", controllers.leadtrustee.organisation.routes.NameController.onPageLoad().url),
        bound.stringQuestion(UtrPage, "leadtrustee.organisation.utr", controllers.leadtrustee.organisation.routes.UtrController.onPageLoad().url),
        bound.yesNoQuestion(BasedInTheUkYesNoPage, "leadtrustee.organisation.basedInTheUkYesNo", controllers.leadtrustee.organisation.routes.BasedInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "leadtrustee.organisation.ukAddress", controllers.leadtrustee.organisation.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "leadtrustee.organisation.nonUkAddress", controllers.leadtrustee.organisation.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(EmailAddressYesNoPage, "leadtrustee.organisation.emailAddressYesNo", controllers.leadtrustee.organisation.routes.EmailAddressYesNoController.onPageLoad().url),
        bound.stringQuestion(EmailAddressPage, "leadtrustee.organisation.emailAddress", controllers.leadtrustee.organisation.routes.EmailAddressController.onPageLoad().url),
        bound.stringQuestion(TelephoneNumberPage, "leadtrustee.organisation.telephoneNumber", controllers.leadtrustee.organisation.routes.TelephoneNumberController.onPageLoad().url)
      ).flatten
    )
  }
}
