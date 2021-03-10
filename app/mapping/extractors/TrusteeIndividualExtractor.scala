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
import pages.trustee.amend.{individual => ind}
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import java.time.LocalDate

import models.Constant.GB
import pages.trustee.individual.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage, CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage, MentalCapacityYesNoPage}

import scala.util.{Success, Try}

class TrusteeIndividualExtractor @Inject()() {

  def extract(answers: UserAnswers, trustee: TrusteeIndividual, index: Int): Try[UserAnswers] = {
    answers.deleteAtPath(pages.trustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Individual))
      .flatMap(_.set(ind.IndexPage, index))
      .flatMap(_.set(ind.NamePage, trustee.name))
      .flatMap(answers => extractDateOfBirth(trustee.dateOfBirth, answers))
      .flatMap(answers => extractAddress(trustee.address, answers))
      .flatMap(answers => extractCountryOfResidence(trustee.countryOfResidence, answers))
      .flatMap(answers => extractCountryOfNationality(trustee.nationality, answers))
      .flatMap(answers => extractMentalCapacity(trustee.mentalCapacityYesNo, answers))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(_.set(WhenAddedPage, trustee.entityStart))
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(ind.DateOfBirthYesNoPage, true)
          .flatMap(_.set(ind.DateOfBirthPage, dob))
      case _ =>
        answers.set(ind.DateOfBirthYesNoPage, false)
    }
  }

  private def extractCountryOfNationality(countryOfNationality: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      countryOfNationality match {
        case Some(GB) => answers
          .set(CountryOfNationalityYesNoPage, true)
          .flatMap(_.set(CountryOfNationalityInTheUkYesNoPage, true))
          .flatMap(_.set(CountryOfNationalityPage, GB))
        case Some(country) => answers
          .set(CountryOfNationalityYesNoPage, true)
          .flatMap(_.set(CountryOfNationalityInTheUkYesNoPage, false))
          .flatMap(_.set(CountryOfNationalityPage, country))
        case None => answers
          .set(CountryOfNationalityYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      countryOfResidence match {
        case Some(GB) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceInTheUkYesNoPage, true))
          .flatMap(_.set(CountryOfResidencePage, GB))
        case Some(country) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceInTheUkYesNoPage, false))
          .flatMap(_.set(CountryOfResidencePage, country))
        case None => answers
          .set(CountryOfResidenceYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractMentalCapacity(mentalCapacityYesNo: Option[Boolean], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      mentalCapacityYesNo match {
        case Some(true) => answers
          .set(MentalCapacityYesNoPage, true)
        case Some(false) => answers
          .set(MentalCapacityYesNoPage, false)
        case None => Success(answers)
      }
    } else {
      Success(answers)
    }
  }

  private def extractIdentification(identification: Option[IndividualIdentification], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable || !answers.is5mldEnabled) {
      identification match {
        case Some(NationalInsuranceNumber(nino)) =>
          answers.set(ind.NationalInsuranceNumberYesNoPage, true)
            .flatMap(_.set(ind.NationalInsuranceNumberPage, nino))
        case Some(p: Passport) =>
          answers.set(ind.NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(ind.PassportOrIdCardDetailsPage, p.asCombined))
        case Some(id: IdCard) =>
          answers.set(ind.NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(ind.PassportOrIdCardDetailsPage, id.asCombined))
        case Some(c: CombinedPassportOrIdCard) =>
          answers.set(ind.NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(ind.PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(ind.PassportOrIdCardDetailsPage, c))
        case _ =>
          answers.set(ind.NationalInsuranceNumberYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable || !answers.is5mldEnabled) {
      address match {
        case Some(uk: UkAddress) =>
          answers.set(ind.AddressYesNoPage, true)
            .flatMap(_.set(ind.LiveInTheUkYesNoPage, true))
            .flatMap(_.set(ind.UkAddressPage, uk))
        case Some(nonUk: NonUkAddress) =>
          answers.set(ind.AddressYesNoPage, true)
            .flatMap(_.set(ind.LiveInTheUkYesNoPage, false))
            .flatMap(_.set(ind.NonUkAddressPage, nonUk))
        case _ =>
          answers.set(ind.AddressYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

}
