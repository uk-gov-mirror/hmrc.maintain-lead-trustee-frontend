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
import mapping.PlaybackExtractionErrors.InvalidExtractorState
import mapping.PlaybackImplicits._
import models.{Address, DisplayTrustIdentificationType, DisplayTrustLeadTrusteeIndType, IdentificationDetailOptions, NonUkAddress, PassportType, UkAddress, UserAnswers}
import pages.leadtrustee.{individual => ltind}
import play.api.Logger

import scala.util.{Failure, Try}

class LeadTrusteesExtractor @Inject()() {

  def extractLeadTrusteeIndividual(answers: UserAnswers, leadIndividual: DisplayTrustLeadTrusteeIndType) = {
    answers.set(ltind.NamePage, leadIndividual.name)
      .flatMap(_.set(ltind.DateOfBirthPage, leadIndividual.dateOfBirth))
      .flatMap(answers => extractLeadIndividualIdentification(leadIndividual, answers))
      .flatMap(answers => extractEmail(leadIndividual.email, answers))
      .flatMap(_.set(ltind.TelephoneNumberPage, leadIndividual.phoneNumber))

  }

  private def extractLeadIndividualIdentification(leadIndividual: DisplayTrustLeadTrusteeIndType, answers: UserAnswers) = {
    leadIndividual.identification match {

      case DisplayTrustIdentificationType(_, Some(nino), None, Some(address)) =>
        answers.set(ltind.UkCitizenPage, true)
          .flatMap(_.set(ltind.NationalInsuranceNumberPage, nino))
          .flatMap(answers => extractAddress(address, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), Some(address)) =>
        answers.set(ltind.UkCitizenPage, false)
          .flatMap(answers => extractPassportIdCard(passport, answers))
          .flatMap(answers => extractAddress(address, answers))

      case DisplayTrustIdentificationType(_, None, Some(_), None) =>
        Logger.error(s"[TrusteesExtractor] only passport identification for lead trustee individual returned in DisplayTrustOrEstate api")
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, Some(nino), None, None) =>
        Logger.error(s"[TrusteesExtractor] only national insurance number for lead trustee individual returned in DisplayTrustOrEstate api")
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, None, None, Some(address)) =>
        Logger.error(s"[TrusteesExtractor] only address identification for lead trustee individual returned in DisplayTrustOrEstate api")
        Failure(InvalidExtractorState)

      case DisplayTrustIdentificationType(_, _, _, _) =>
        Logger.error(s"[TrusteesExtractor] no identification for lead trustee individual returned in DisplayTrustOrEstate api")
        Failure(InvalidExtractorState)
    }
  }

  private def extractPassportIdCard(passport: PassportType, answers: UserAnswers) = {
    answers.set(ltind.IdentificationDetailOptionsPage, IdentificationDetailOptions.Passport)
      .flatMap(_.set(ltind.PassportDetailsPage, passport.number))

  }

  private def extractAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UkAddress =>
        answers.set(ltind.UkAddressPage, uk)
          .flatMap(_.set(ltind.LiveInTheUkYesNoPage, true))
      case nonUk: NonUkAddress =>
        answers.set(ltind.NonUkAddressPage, nonUk)
          .flatMap(_.set(ltind.LiveInTheUkYesNoPage, false))
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