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
import models.{Address, IndividualOrBusiness, NonUkAddress, UkAddress, UserAnswers}
import pages.{EmptyPage, QuestionPage}
import play.api.libs.json.{JsPath, Writes}

import scala.util.{Success, Try}

trait Extractor {

  def extract(answers: UserAnswers, individualOrBusiness: IndividualOrBusiness): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(individualOrBusinessPage, individualOrBusiness))
  }

  def basePath: JsPath
  def individualOrBusinessPage: QuestionPage[IndividualOrBusiness]

  def ukAddressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressPage: QuestionPage[UkAddress] = new EmptyPage[UkAddress]
  def nonUkAddressPage: QuestionPage[NonUkAddress] = new EmptyPage[NonUkAddress]

  def extractOptionalAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case Some(value) => extractAddress(value, answers)
      case None => Success(answers)
    }
  }

  def extractAddress(address: Address, answers: UserAnswers): Try[UserAnswers] = {
    address match {
      case uk: UkAddress => answers
        .set(ukAddressYesNoPage, true)
        .flatMap(_.set(ukAddressPage, uk))
      case nonUk: NonUkAddress => answers
        .set(ukAddressYesNoPage, false)
        .flatMap(_.set(nonUkAddressPage, nonUk))
    }
  }

  def countryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean]
  def countryOfResidencePage: QuestionPage[String]

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
