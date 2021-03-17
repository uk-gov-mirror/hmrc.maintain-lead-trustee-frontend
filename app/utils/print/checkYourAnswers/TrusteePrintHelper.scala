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

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import viewmodels.AnswerSection
import javax.inject.Inject

class TrusteePrintHelper @Inject()(trusteeIndividualPrintHelper: TrusteeIndividualPrintHelper,
                                   trusteeOrganisationPrintHelper: TrusteeOrganisationPrintHelper,
                                   amendTrusteeIndividualPrintHelper: AmendTrusteeIndividualPrintHelper,
                                   leadTrusteeIndividualPrintHelper: LeadTrusteeIndividualPrintHelper,
                                   leadTrusteeOrganisationPrintHelper: LeadTrusteeOrganisationPrintHelper) {

  def printIndividualTrustee(userAnswers: UserAnswers, name: String)(implicit messages: Messages): AnswerSection = {
    trusteeIndividualPrintHelper.print(userAnswers, name)
  }

  def printOrganisationTrustee(userAnswers: UserAnswers, provisional: Boolean, name: String, mode: Mode)(implicit messages: Messages): AnswerSection = {
    trusteeOrganisationPrintHelper.print(userAnswers, provisional, name, mode)
  }

  def printAmendedIndividualTrustee(userAnswers: UserAnswers, name: String)(implicit messages: Messages): AnswerSection = {
    amendTrusteeIndividualPrintHelper.print(userAnswers, name)
  }

  def printLeadIndividualTrustee(userAnswers: UserAnswers, name: String)(implicit messages: Messages): AnswerSection = {
    leadTrusteeIndividualPrintHelper.print(userAnswers, name)
  }

  def printLeadOrganisationTrustee(userAnswers: UserAnswers, name: String)(implicit messages: Messages): AnswerSection = {
    leadTrusteeOrganisationPrintHelper.print(userAnswers, name)
  }

}
