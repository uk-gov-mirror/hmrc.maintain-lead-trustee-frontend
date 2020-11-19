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

package utils.print

import java.time.LocalDate

import com.google.inject.Inject
import models.{Address, CombinedPassportOrIdCard, IdCard, IdentificationDetailOptions, Name, Passport, UserAnswers}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, trusteeName: String, countryOptions: CountryOptions)
          (implicit messages: Messages): Bound = new Bound(userAnswers, trusteeName, countryOptions)

  class Bound(userAnswers: UserAnswers, trusteeName: String, countryOptions: CountryOptions)
             (implicit messages: Messages) {

    def nameQuestion(query: Gettable[Name],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
          HtmlFormat.escape(x.displayFullName),
          changeUrl
        )
      }
    }

    def stringQuestion(query: Gettable[String],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          HtmlFormat.escape(x),
          changeUrl
        )
      }
    }

    def yesNoQuestion(query: Gettable[Boolean],
                      labelKey: String,
                      changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.yesOrNo(x),
          changeUrl
        )
      }
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          HtmlFormat.escape(checkAnswersFormatters.formatDate(x)),
          changeUrl
        )
      }
    }

    def ninoQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatNino(x),
          changeUrl
        )
      }
    }

    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatAddress(x, countryOptions),
          changeUrl
        )
      }
    }

    def identificationOptionsQuestion(query: Gettable[IdentificationDetailOptions],
                                      labelKey: String,
                                      changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatIdentificationDetails(x),
          changeUrl
        )
      }
    }

    def passportOrIdCardDetailsQuestion(query: Gettable[CombinedPassportOrIdCard],
                                        labelKey: String,
                                        changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatPassportOrIdCardDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def passportDetailsQuestion(query: Gettable[Passport],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatPassportDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def idCardDetailsQuestion(query: Gettable[IdCard],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", trusteeName)),
          checkAnswersFormatters.formatIdCardDetails(x, countryOptions),
          changeUrl
        )
      }
    }
  }
}
