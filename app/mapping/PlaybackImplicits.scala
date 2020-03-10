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

import models.{NonUkAddress, UkAddress, _}
import org.joda.time.DateTime

object PlaybackImplicits {

  implicit class DateTimeConverter(date : DateTime) {
    def convert : LocalDate = LocalDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
  }

  private def convertAddress(address: AddressType) : Address = address.postCode match {
    case Some(post) =>
      UkAddress(
        line1 = address.line1,
        line2 = address.line2,
        line3 = address.line3,
        line4 = address.line4,
        postcode = post
      )
    case None =>
      NonUkAddress(
        line1 = address.line1,
        line2 = address.line2,
        line3 = address.line3,
        country = address.country
      )
  }

  private def convertAddress(address: Address) : AddressType = address match {
    case UkAddress(line1, line2, line3, line4, postcode) =>
      AddressType(
        line1,
        line2,
        line3,
        line4,
        Some(postcode),
        "GB"
      )
    case NonUkAddress(line1, line2, line3, country) =>
      AddressType(
        line1,
        line2,
        line3,
        None,
        None,
        country
      )
  }

  implicit class AddressConverter(address : Address) {
    def convert : AddressType = convertAddress(address)
  }

  implicit class AddressTypeConverter(address : AddressType) {
    def convert : Address = convertAddress(address)
  }
}