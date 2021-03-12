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

import models.{IndividualOrBusiness, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.{JsPath, Writes}

import scala.util.{Success, Try}

trait Extractor {

  def extract(answers: UserAnswers, individualOrBusiness: IndividualOrBusiness): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(individualOrBusinessPage, individualOrBusiness))
  }

  def basePath: JsPath
  def individualOrBusinessPage: QuestionPage[IndividualOrBusiness]

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
