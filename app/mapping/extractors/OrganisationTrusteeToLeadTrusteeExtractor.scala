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

import models.IndividualOrBusiness.Business
import models._
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.organisation._

import scala.util.{Success, Try}

class OrganisationTrusteeToLeadTrusteeExtractor {

  def extract(userAnswers: UserAnswers, trustee: TrusteeOrganisation, index: Int): Try[UserAnswers] = {
    userAnswers.deleteAtPath(pages.leadtrustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Business))
      .flatMap(_.set(IndexPage, index))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(_.set(NamePage, trustee.name))
      .flatMap(answers => extractEmail(trustee.email, answers))
      .flatMap(answers => extractTelephoneNumber(trustee.phoneNumber, answers))
  }

  private def extractIdentification(identification: Option[TrustIdentificationOrgType], answers: UserAnswers): Try[UserAnswers] = {
    identification match {
      case Some(TrustIdentificationOrgType(_, Some(utr), None)) => answers
        .set(RegisteredInUkYesNoPage, true)
        .flatMap(_.set(UtrPage, utr))
      case Some(TrustIdentificationOrgType(_, None, Some(address))) => answers
        .set(RegisteredInUkYesNoPage, false)
        .flatMap(answers => extractAddress(address, answers))
      case _ => Success(answers)
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case uk: UkAddress => answers
        .set(AddressInTheUkYesNoPage, true)
        .flatMap(_.set(UkAddressPage, uk))
      case nonUk: NonUkAddress => answers
        .set(AddressInTheUkYesNoPage, false)
        .flatMap(_.set(NonUkAddressPage, nonUk))
    }
  }

  private def extractEmail(emailAddress: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    emailAddress match {
      case Some(email) => answers
        .set(EmailAddressYesNoPage, true)
        .flatMap(_.set(EmailAddressPage, email))
      case _ => Success(answers)
    }
  }

  private def extractTelephoneNumber(phoneNumber: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    phoneNumber match {
      case Some(tel) => answers.set(TelephoneNumberPage, tel)
      case _ => Success(answers)
    }
  }

}
