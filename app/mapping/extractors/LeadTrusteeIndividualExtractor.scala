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

package mapping.extractors

import com.google.inject.Inject
import models.IndividualOrBusiness.Individual
import models._
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._
import play.api.Logging

import scala.util.Try

class LeadTrusteeIndividualExtractor @Inject()() extends Logging {

  def extract(answers: UserAnswers, leadIndividual: LeadTrusteeIndividual): Try[UserAnswers] = {
    answers.deleteAtPath(pages.leadtrustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Individual))
      .flatMap(_.set(NamePage, leadIndividual.name))
      .flatMap(_.set(DateOfBirthPage, leadIndividual.dateOfBirth))
      .flatMap(answers => extractLeadIndividualIdentification(leadIndividual, answers))
      .flatMap(answers => extractEmail(leadIndividual.email, answers))
      .flatMap(answers => extractAddress(leadIndividual.address, answers))
      .flatMap(_.set(TelephoneNumberPage, leadIndividual.phoneNumber))
  }

  private def extractLeadIndividualIdentification(leadIndividual: LeadTrusteeIndividual, answers: UserAnswers): Try[UserAnswers] = {
    leadIndividual.identification match {
      case NationalInsuranceNumber(nino) =>
        answers.set(UkCitizenPage, true)
          .flatMap(_.set(NationalInsuranceNumberPage, nino))
      case passport:Passport =>
        answers.set(UkCitizenPage, false)
          .flatMap(_.set(PassportOrIdCardDetailsPage, passport.asCombined))
      case idCard:IdCard =>
        answers.set(UkCitizenPage, false)
          .flatMap(_.set(PassportOrIdCardDetailsPage, idCard.asCombined))
      case comb:CombinedPassportOrIdCard =>
        answers.set(UkCitizenPage, false)
          .flatMap(_.set(PassportOrIdCardDetailsPage, comb))
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case uk: UkAddress => answers
        .set(UkAddressPage, uk)
        .flatMap(_.set(LiveInTheUkYesNoPage, true))
      case nonUk: NonUkAddress => answers
        .set(NonUkAddressPage, nonUk)
        .flatMap(_.set(LiveInTheUkYesNoPage, false))
    }
  }

  private def extractEmail(email: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    email match {
      case Some(x) => answers
        .set(EmailAddressYesNoPage, true)
        .flatMap(_.set(EmailAddressPage, x))
      case _ => answers
        .set(EmailAddressYesNoPage, false)
    }
  }

}
