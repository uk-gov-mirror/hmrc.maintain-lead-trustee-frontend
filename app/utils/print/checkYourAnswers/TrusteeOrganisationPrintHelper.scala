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
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.trustee.WhenAddedPage
import pages.trustee.organisation._
import play.api.i18n.Messages
import utils.print.AnswerRowConverter
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def print(userAnswers: UserAnswers, provisional: Boolean, trusteeName: String, mode: Mode)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, trusteeName)

    def answerRows(mode: Mode): Seq[Option[AnswerRow]] = Seq(
      bound.stringQuestion(NamePage, "trustee.organisation.name", controllers.trustee.organisation.routes.NameController.onPageLoad(mode).url),
      bound.yesNoQuestion(UtrYesNoPage, "trustee.organisation.utrYesNo", controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode).url),
      bound.stringQuestion(UtrPage, "trustee.organisation.utr", controllers.trustee.organisation.routes.UtrController.onPageLoad(mode).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage, "trustee.organisation.countryOfResidenceYesNo", controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage, "trustee.organisation.countryOfResidenceInTheUkYesNo", controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, "trustee.organisation.countryOfResidence", controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode).url),
      bound.yesNoQuestion(AddressYesNoPage, "trustee.organisation.addressYesNo", controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(AddressInTheUkYesNoPage, "trustee.organisation.addressInTheUkYesNo", controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode).url),
      bound.addressQuestion(UkAddressPage, "trustee.organisation.ukAddress", controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode).url),
      bound.addressQuestion(NonUkAddressPage, "trustee.organisation.nonUkAddress", controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode).url),
    )

    lazy val add: Seq[AnswerRow] = (
      answerRows(NormalMode) :+
        bound.dateQuestion(WhenAddedPage, "trustee.whenAdded", controllers.trustee.routes.WhenAddedController.onPageLoad().url)
      ).flatten

    lazy val amend: Seq[AnswerRow] = answerRows(CheckMode).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )

  }
}
