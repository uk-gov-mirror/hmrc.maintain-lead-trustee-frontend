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

import models.Constant.GB
import models.IndividualOrBusiness.Individual
import models._
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._

import java.time.LocalDate
import scala.util.{Success, Try}

class IndividualTrusteeToLeadTrusteeExtractor {

  def extract(userAnswers: UserAnswers, trustee: TrusteeIndividual, index: Int): Try[UserAnswers] = {
    userAnswers.deleteAtPath(pages.leadtrustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Individual))
      .flatMap(_.set(IndexPage, index))
      .flatMap(_.set(NamePage, trustee.name))
      .flatMap(answers => extractDateOfBirth(trustee.dateOfBirth, answers))
      .flatMap(answers => extractCountryOfNationality(trustee.nationality, answers))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(answers => extractAddress(trustee.address, answers))
      .flatMap(answers => extractCountryOfResidence(trustee.countryOfResidence, answers))
      .flatMap(answers => extractTelephoneNumber(trustee.phoneNumber, answers))
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(DateOfBirthPage, dob)
      case _ =>
        Success(answers)
    }
  }

  private def extractCountryOfNationality(countryOfNationality: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled) {
      countryOfNationality match {
        case Some(GB) => answers
          .set(CountryOfNationalityInTheUkYesNoPage, true)
          .flatMap(_.set(CountryOfNationalityPage, GB))
        case Some(country) => answers
          .set(CountryOfNationalityInTheUkYesNoPage, false)
          .flatMap(_.set(CountryOfNationalityPage, country))
        case _ => Success(answers)
      }
    } else {
      Success(answers)
    }
  }

  private def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled) {
      countryOfResidence match {
        case Some(GB) => answers
          .set(CountryOfResidenceInTheUkYesNoPage, true)
          .flatMap(_.set(CountryOfResidencePage, GB))
        case Some(country) => answers
          .set(CountryOfResidenceInTheUkYesNoPage, false)
          .flatMap(_.set(CountryOfResidencePage, country))
        case _ => Success(answers)
      }
    } else {
      Success(answers)
    }
  }

  private def extractIdentification(identification: Option[IndividualIdentification], answers: UserAnswers): Try[UserAnswers] = {
    identification map {
      case NationalInsuranceNumber(nino) => answers
        .set(UkCitizenPage, true)
        .flatMap(_.set(NationalInsuranceNumberPage, nino))
      case p: Passport => answers
        .set(UkCitizenPage, false)
        .flatMap(_.set(PassportOrIdCardDetailsPage, p.asCombined))
      case id: IdCard => answers
        .set(UkCitizenPage, false)
        .flatMap(_.set(PassportOrIdCardDetailsPage, id.asCombined))
      case c: CombinedPassportOrIdCard => answers
        .set(UkCitizenPage, false)
        .flatMap(_.set(PassportOrIdCardDetailsPage, c))
    } getOrElse {
      Success(answers)
    }
  }

  private def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
    address.map {
      case uk: UkAddress => answers
        .set(LiveInTheUkYesNoPage, true)
        .flatMap(_.set(UkAddressPage, uk))
      case nonUk: NonUkAddress => answers
        .set(LiveInTheUkYesNoPage, false)
        .flatMap(_.set(NonUkAddressPage, nonUk))
    } getOrElse {
      Success(answers)
    }
  }

  private def extractTelephoneNumber(phoneNumber: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    phoneNumber match {
      case Some(tel) => answers.set(TelephoneNumberPage, tel)
      case _ => Success(answers)
    }
  }

}
