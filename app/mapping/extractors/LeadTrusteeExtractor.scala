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

import models.{CombinedPassportOrIdCard, IdCard, IndividualIdentification, NationalInsuranceNumber, Passport, UserAnswers}
import pages.QuestionPage

import scala.util.{Success, Try}

trait LeadTrusteeExtractor extends Extractor {

  def ninoYesNoPage: QuestionPage[Boolean]
  def ninoPage: QuestionPage[String]
  def passportOrIdCardDetailsPage: QuestionPage[CombinedPassportOrIdCard]

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

}
