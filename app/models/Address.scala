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

package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait Address

case class UkAddress (line1: String,
                      line2: String,
                      line3: Option[String],
                      line4: Option[String],
                      postCode: String) extends Address
object UkAddress {
  implicit val format = Json.format[UkAddress]
}

case class NonUkAddress (line1: String,
                         line2: String,
                         line3: Option[String],
                         line4: Option[String],
                         country: String) extends Address

object NonUkAddress {
  implicit val format = Json.format[NonUkAddress]
}

object Address {
  implicit val reads: Reads[Address] =
    ((__ \ 'line1).read[String] and
    (__ \ 'line2).read[String] and
    (__ \ 'line3).readNullable[String] and
    (__ \ 'line4).readNullable[String] and
    (__ \ 'postCode).readNullable[String] and
      (__ \ 'country).read[String]) ((line1, line2, line3, line4, postCode, country) => {
      if (postCode.isDefined) {
        UkAddress(line1, line2, line3, line4, postCode.get)
      }
      else {
        NonUkAddress(line1, line2, line3, line4, country)
      }
    })

  implicit val writes: Writes[Address] = Writes(_ => ???)
}

