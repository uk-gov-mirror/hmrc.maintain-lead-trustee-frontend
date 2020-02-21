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

package mapping

import com.google.inject.Inject
import models.{Address, IdCard, LeadTrusteeIndividual, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import pages.leadtrustee.{individual => ltind}

import scala.util.{Success, Try}

class LeadTrusteesExtractor @Inject()() {

  def extractLeadTrusteeIndividual(answers: UserAnswers, leadIndividual: LeadTrusteeIndividual): Try[UserAnswers] = {
    answers.set(ltind.NamePage, leadIndividual.name)
      .flatMap(_.set(ltind.DateOfBirthPage, leadIndividual.dateOfBirth))
      .flatMap(answers => extractLeadIndividualIdentification(leadIndividual, answers))
      .flatMap(answers => extractEmail(leadIndividual.email, answers))
      .flatMap(answers => extractAddress(leadIndividual.address, answers))
      .flatMap(_.set(ltind.TelephoneNumberPage, leadIndividual.phoneNumber))
  }

  private def extractLeadIndividualIdentification(leadIndividual: LeadTrusteeIndividual, answers: UserAnswers) = {
    leadIndividual.identification match {

      case NationalInsuranceNumber(nino) =>
        answers.set(ltind.UkCitizenPage, true)
          .flatMap(_.set(ltind.NationalInsuranceNumberPage, nino))
      case passport:Passport =>
        answers.set(ltind.UkCitizenPage, false)
          .flatMap(_.set(ltind.PassportDetailsPage, passport))
      case idCard:IdCard =>
        answers.set(ltind.UkCitizenPage, false)
          .flatMap(_.set(ltind.IdCardDetailsPage, idCard))
    }
  }

  private def extractAddress(address: Option[Address], answers: UserAnswers) = {
    address match {
      case Some(uk: UkAddress) =>
        answers.set(ltind.UkAddressPage, uk)
          .flatMap(_.set(ltind.LiveInTheUkYesNoPage, true))
      case Some(nonUk: NonUkAddress) =>
        answers.set(ltind.NonUkAddressPage, nonUk)
          .flatMap(_.set(ltind.LiveInTheUkYesNoPage, false))
      case None => Success(answers)
    }
  }

  private def extractEmail(email: Option[String], answers: UserAnswers) = {
    email match {
      case Some(x) =>
        answers.set(ltind.EmailAddressYesNoPage, true)
          .flatMap(_.set(ltind.EmailAddressPage, x))
      case _ => answers.set(ltind.EmailAddressYesNoPage, false)
    }
  }

}