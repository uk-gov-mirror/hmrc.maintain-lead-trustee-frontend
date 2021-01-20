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
import models.IndividualOrBusiness.Business
import models._
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.organisation.{UtrPage, _}
import play.api.Logging

import scala.util.{Success, Try}

class LeadTrusteeOrganisationExtractor @Inject()() extends Logging {

  def extract(answers: UserAnswers, leadOrganisation: LeadTrusteeOrganisation): Try[UserAnswers] = {
    answers.deleteAtPath(pages.leadtrustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Business))
      .flatMap(answers => extractRegisteredInUK(leadOrganisation.utr, answers))
      .flatMap(_.set(NamePage, leadOrganisation.name))
      .flatMap(answers => extractAddress(leadOrganisation.address, answers))
      .flatMap(answers => extractEmail(leadOrganisation.email, answers))
      .flatMap(_.set(TelephoneNumberPage, leadOrganisation.phoneNumber))
  }

  private def extractEmail(email: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    email match {
      case Some(x) =>
        answers.set(EmailAddressYesNoPage, true)
          .flatMap(_.set(EmailAddressPage, x))
      case _ => answers.set(EmailAddressYesNoPage, false)
    }
  }

  private def extractRegisteredInUK(utr: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    utr match {
      case Some(x) =>
        answers.set(RegisteredInUkYesNoPage, true)
        .flatMap(_.set(UtrPage, x))
      case _ => answers.set(RegisteredInUkYesNoPage, false)
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case uk: UkAddress =>
        answers.set(UkAddressPage, uk)
          .flatMap(_.set(AddressInTheUkYesNoPage, true))
      case nonUk: NonUkAddress =>
        answers.set(NonUkAddressPage, nonUk)
          .flatMap(_.set(AddressInTheUkYesNoPage, false))
      case _ => Success(answers)
    }
  }

}
