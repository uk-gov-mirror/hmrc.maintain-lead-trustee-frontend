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
import models._
import pages.QuestionPage
import pages.trustee.amend.{individual => ind}
import pages.trustee.individual._
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

class TrusteeIndividualExtractor extends IndividualExtractor {

  override def basePath: JsPath = pages.trustee.basePath
  override def individualOrBusinessPage: QuestionPage[IndividualOrBusiness] = IndividualOrBusinessPage

  override def countryOfNationalityYesNoPage: QuestionPage[Boolean] = CountryOfNationalityYesNoPage
  override def ukCountryOfNationalityYesNoPage: QuestionPage[Boolean] = CountryOfNationalityInTheUkYesNoPage
  override def countryOfNationalityPage: QuestionPage[String] = CountryOfNationalityPage

  override def countryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceYesNoPage
  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceInTheUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

  def extract(answers: UserAnswers, trustee: TrusteeIndividual, index: Int): Try[UserAnswers] = {
    super.extract(answers)
      .flatMap(_.set(ind.IndexPage, index))
      .flatMap(_.set(ind.NamePage, trustee.name))
      .flatMap(answers => extractConditionalValue(trustee.dateOfBirth, DateOfBirthYesNoPage, DateOfBirthPage, answers))
      .flatMap(answers => extractAddress(trustee.address, answers))
      .flatMap(answers => extractCountryOfResidence(trustee.countryOfResidence, answers))
      .flatMap(answers => extractCountryOfNationality(trustee.nationality, answers))
      .flatMap(answers => extractMentalCapacity(trustee.mentalCapacityYesNo, answers))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(_.set(WhenAddedPage, trustee.entityStart))
  }

  override def extractCountryOfResidenceOrNationality(country: Option[String],
                                                      answers: UserAnswers,
                                                      yesNoPage: QuestionPage[Boolean],
                                                      ukYesNoPage: QuestionPage[Boolean],
                                                      page: QuestionPage[String]): Try[UserAnswers] ={
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      country match {
        case Some(GB) => answers
          .set(yesNoPage, true)
          .flatMap(_.set(ukYesNoPage, true))
          .flatMap(_.set(page, GB))
        case Some(country) => answers
          .set(yesNoPage, true)
          .flatMap(_.set(ukYesNoPage, false))
          .flatMap(_.set(page, country))
        case None => answers
          .set(yesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractMentalCapacity(mentalCapacityYesNo: Option[Boolean], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      extractValue(mentalCapacityYesNo, MentalCapacityYesNoPage, answers)
    } else {
      Success(answers)
    }
  }

  override def extractIdentification(identification: Option[IndividualIdentification], answers: UserAnswers): Try[UserAnswers] = {
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

  override def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
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
