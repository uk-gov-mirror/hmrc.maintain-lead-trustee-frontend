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

package utils

import java.time.format.DateTimeFormatter

import javax.inject.Inject
import models.{IdCard, IdentificationDetailOptions, Passport}
import play.twirl.api.{Html, HtmlFormat}
import utils.countryOptions.CountryOptions

class CheckAnswersFormatters @Inject()(countryOptions: CountryOptions) {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def country(code: String, countryOptions: CountryOptions): String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def passport(passport: Passport): Html = {
    val lines =
      Seq(
        Some(country(passport.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(passport.number)),
        Some(HtmlFormat.escape(passport.expirationDate.format(dateFormatter)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def idCard(idCard: IdCard): Html = {
    val lines =
      Seq(
        Some(country(idCard.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(idCard.number)),
        Some(HtmlFormat.escape(idCard.expirationDate.format(dateFormatter)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def identificationDetailOptions(identificationDetailOptions: IdentificationDetailOptions): Html = {
    identificationDetailOptions match {
      case IdentificationDetailOptions.IdCard => HtmlFormat.escape("ID card")
      case IdentificationDetailOptions.Passport => HtmlFormat.escape("Passport")
    }
  }
}
