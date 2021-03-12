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
import pages.{EmptyPage, QuestionPage}
import play.api.libs.json.{JsPath, Writes}

import scala.util.{Success, Try}

trait IndividualExtractor {

  def extract(answers: UserAnswers): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(individualOrBusinessPage, Individual))
  }

  def basePath: JsPath
  def individualOrBusinessPage: QuestionPage[IndividualOrBusiness]

  def countryOfNationalityYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfNationalityYesNoPage: QuestionPage[Boolean]
  def countryOfNationalityPage: QuestionPage[String]

  def countryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean]
  def countryOfResidencePage: QuestionPage[String]

  def extractCountryOfNationality(countryOfNationality: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    extractCountryOfResidenceOrNationality(
      country = countryOfNationality,
      answers = answers,
      yesNoPage = countryOfNationalityYesNoPage,
      ukYesNoPage = ukCountryOfNationalityYesNoPage,
      page = countryOfNationalityPage
    )
  }

  def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    extractCountryOfResidenceOrNationality(
      country = countryOfResidence,
      answers = answers,
      yesNoPage = countryOfResidenceYesNoPage,
      ukYesNoPage = ukCountryOfResidenceYesNoPage,
      page = countryOfResidencePage
    )
  }

  def extractCountryOfResidenceOrNationality(country: Option[String],
                                             answers: UserAnswers,
                                             yesNoPage: QuestionPage[Boolean],
                                             ukYesNoPage: QuestionPage[Boolean],
                                             page: QuestionPage[String]): Try[UserAnswers] = {
    if (answers.is5mldEnabled) {
      country match {
        case Some(GB) => answers
          .set(ukYesNoPage, true)
          .flatMap(_.set(page, GB))
        case Some(country) => answers
          .set(ukYesNoPage, false)
          .flatMap(_.set(page, country))
        case None => Success(answers)
      }
    } else {
      Success(answers)
    }
  }

  def ukAddressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressPage: QuestionPage[UkAddress] = new EmptyPage[UkAddress]
  def nonUkAddressPage: QuestionPage[NonUkAddress] = new EmptyPage[NonUkAddress]

  def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case Some(uk: UkAddress) => answers
        .set(ukAddressYesNoPage, true)
        .flatMap(_.set(ukAddressPage, uk))
      case Some(nonUk: NonUkAddress) => answers
        .set(ukAddressYesNoPage, false)
        .flatMap(_.set(nonUkAddressPage, nonUk))
      case _ => Success(answers)
    }
  }

  def ninoYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ninoPage: QuestionPage[String] = new EmptyPage[String]
  def passportOrIdCardDetailsPage: QuestionPage[CombinedPassportOrIdCard] = new EmptyPage[CombinedPassportOrIdCard]

  def extractIdentification(identification: Option[IndividualIdentification], answers: UserAnswers): Try[UserAnswers] = {
    identification map {
      case NationalInsuranceNumber(nino) => answers
        .set(ninoYesNoPage, true)
        .flatMap(_.set(ninoPage, nino))
      case p: Passport => answers
        .set(ninoYesNoPage, false)
        .flatMap(_.set(passportOrIdCardDetailsPage, p.asCombined))
      case id: IdCard => answers
        .set(ninoYesNoPage, false)
        .flatMap(_.set(passportOrIdCardDetailsPage, id.asCombined))
      case c: CombinedPassportOrIdCard => answers
        .set(ninoYesNoPage, false)
        .flatMap(_.set(passportOrIdCardDetailsPage, c))
    } getOrElse {
      Success(answers)
    }
  }

  def extractValue[T](optionalValue: Option[T],
                      page: QuestionPage[T],
                      answers: UserAnswers)
                     (implicit wts: Writes[T]): Try[UserAnswers] = {
    optionalValue match {
      case Some(value) => answers.set(page, value)
      case _ => Success(answers)
    }
  }

  def extractConditionalValue[T](optionalValue: Option[T],
                                 yesNoPage: QuestionPage[Boolean],
                                 page: QuestionPage[T],
                                 answers: UserAnswers)
                                (implicit wts: Writes[T]): Try[UserAnswers] = {
    optionalValue match {
      case Some(value) => answers
        .set(yesNoPage, true)
        .flatMap(_.set(page, value))
      case None => answers
        .set(yesNoPage, false)
    }
  }

}
