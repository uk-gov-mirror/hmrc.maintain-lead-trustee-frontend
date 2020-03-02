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

import java.time.LocalDate

import com.google.inject.Inject
import models.IndividualOrBusiness.Individual
import models.{Address, IdCard, IdentificationDetailOptions, IndividualIdentification, LeadTrusteeIndividual, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import org.slf4j.LoggerFactory
import pages.leadtrustee.{IndividualOrBusinessPage, individual => ltind}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

import scala.util.{Success, Try}

class LeadTrusteeIndividualExtractor @Inject()() {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def mapLeadTrusteeIndividual(answers: UserAnswers) : Option[LeadTrusteeIndividual] = {
    val readFromUserAnswers: Reads[LeadTrusteeIndividual] =
      (
        ltind.NamePage.path.read[Name] and
        ltind.DateOfBirthPage.path.read[LocalDate] and
        ltind.TelephoneNumberPage.path.read[String] and
        ltind.EmailAddressYesNoPage.path.read[Boolean].flatMap[Option[String]] {
          case true => ltind.EmailAddressPage.path.read[String].map(Some(_))
          case false => Reads( _=> JsSuccess(None))
        } and
        ltind.UkCitizenPage.path.read[Boolean].flatMap {
          case true => ltind.NationalInsuranceNumberPage.path.read[String].map(NationalInsuranceNumber(_)).widen[IndividualIdentification]
          case false => ltind.IdentificationDetailOptionsPage.path.read[IdentificationDetailOptions].flatMap {
            case IdentificationDetailOptions.Passport => ltind.PassportDetailsPage.path.read[Passport].widen[IndividualIdentification]
            case IdentificationDetailOptions.IdCard => ltind.IdCardDetailsPage.path.read[IdCard].widen[IndividualIdentification]
          }
        } and
        ltind.LiveInTheUkYesNoPage.path.readNullable[Boolean].flatMap {
          case Some(true) => ltind.UkAddressPage.path.readNullable[UkAddress].widen[Option[Address]]
          case Some(false) => ltind.NonUkAddressPage.path.readNullable[NonUkAddress].widen[Option[Address]]
          case _ => Reads(_ => JsSuccess(None)).widen[Option[Address]]
        }
      ).apply(LeadTrusteeIndividual.apply _ )

    answers.data.validate[LeadTrusteeIndividual](readFromUserAnswers) match {
      case JsError(errors) =>
        logger.error("Failed to rehydrate LeadTrusteeIndividual from UserAnswers", errors)
        None
      case JsSuccess(value, _) => Some(value)
    }
  }

  def extractLeadTrusteeIndividual(answers: UserAnswers, leadIndividual: LeadTrusteeIndividual): Try[UserAnswers] = {
    answers.set(IndividualOrBusinessPage, Individual)
      .flatMap(_.set(ltind.NamePage, leadIndividual.name))
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
          .flatMap(_.set(ltind.IdentificationDetailOptionsPage, IdentificationDetailOptions.Passport))
          .flatMap(_.set(ltind.PassportDetailsPage, passport))
      case idCard:IdCard =>
        answers.set(ltind.UkCitizenPage, false)
          .flatMap(_.set(ltind.IdentificationDetailOptionsPage, IdentificationDetailOptions.IdCard))
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