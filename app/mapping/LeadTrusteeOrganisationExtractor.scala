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
import models.IndividualOrBusiness.Business
import models.{Address, LeadTrusteeOrganisation, NonUkAddress, UkAddress, UserAnswers}
import pages.leadtrustee.organisation.UtrPage
import pages.leadtrustee.{IndividualOrBusinessPage, organisation => ltorg}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

import scala.util.{Success, Try}

class LeadTrusteeOrganisationExtractor @Inject()() {

  private val logger = Logger(getClass)

  def mapLeadTrusteeOrganisation(answers: UserAnswers): Option[LeadTrusteeOrganisation] = {
    val readFromUserAnswers: Reads[LeadTrusteeOrganisation] =
      (
        ltorg.NamePage.path.read[String] and
        ltorg.TelephoneNumberPage.path.read[String] and
        ltorg.EmailAddressPage.path.readNullable[String] and
        ltorg.UtrPage.path.readNullable[String] and
        ltorg.AddressInTheUkYesNoPage.path.read[Boolean].flatMap {
          case true => ltorg.UkAddressPage.path.read[UkAddress].widen[Address]
          case false => ltorg.NonUkAddressPage.path.read[Address].widen[Address]
        }
      ).apply(LeadTrusteeOrganisation.apply _)

    answers.data.validate[LeadTrusteeOrganisation](readFromUserAnswers) match {
      case JsError(errors) =>
        logger.error(s"[Mapper][UTR: ${answers.utr}] Failed to rehydrate LeadTrusteeOrganisation from UserAnswers due to $errors")
        None
      case JsSuccess(value, _) => Some(value)
    }
  }

  def extractLeadTrusteeOrganisation(answers: UserAnswers, leadOrganisation: LeadTrusteeOrganisation): Try[UserAnswers] = {
    answers.set(IndividualOrBusinessPage, Business)
      .flatMap(answers => extractRegisteredInUK(leadOrganisation.utr, answers))
      .flatMap(_.set(ltorg.NamePage, leadOrganisation.name))
      .flatMap(answers => extractAddress(leadOrganisation.address, answers))
      .flatMap(answers => extractEmail(leadOrganisation.email, answers))
      .flatMap(_.set(ltorg.TelephoneNumberPage, leadOrganisation.phoneNumber))
  }

  private def extractEmail(email: Option[String], answers: UserAnswers) = {
    email match {
      case Some(x) =>
        answers.set(ltorg.EmailAddressYesNoPage, true)
          .flatMap(_.set(ltorg.EmailAddressPage, x))
      case _ => answers.set(ltorg.EmailAddressYesNoPage, false)
    }
  }

  private def extractRegisteredInUK(utr: Option[String], answers: UserAnswers) = {
    utr match {
      case Some(x) =>
        answers.set(ltorg.RegisteredInUkYesNoPage, true)
        .flatMap(_.set(UtrPage, x))
      case _ => answers.set(ltorg.RegisteredInUkYesNoPage, false)
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(ltorg.UkAddressPage, uk)
          .flatMap(_.set(ltorg.AddressInTheUkYesNoPage, true))
      case nonUk: NonUkAddress =>
        answers.set(ltorg.NonUkAddressPage, nonUk)
          .flatMap(_.set(ltorg.AddressInTheUkYesNoPage, false))
      case _ => Success(answers)
    }
  }

}