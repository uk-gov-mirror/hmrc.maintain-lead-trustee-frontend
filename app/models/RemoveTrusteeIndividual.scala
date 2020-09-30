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

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class RemoveTrusteeIndividual(lineNo: Option[String],
                                   bpMatchStatus: Option[String],
                                   name: Name,
                                   dateOfBirth: Option[DateTime],
                                   phoneNumber: Option[String],
                                   identification: Option[IndividualIdentification],
                                   entityStart: DateTime)

object RemoveTrusteeIndividual {

  implicit val dateFormat: Format[DateTime] = Format[DateTime](JodaReads.jodaDateReads(dateTimePattern), JodaWrites.jodaDateWrites(dateTimePattern))

  implicit val trusteeIndividualTypeFormat: Format[RemoveTrusteeIndividual] = Json.format[RemoveTrusteeIndividual]

  implicit val reads : Reads[RemoveTrusteeIndividual] =
    ((__ \ 'lineNo).readNullable[String] and
      (__ \ 'bpMatchStatus).readNullable[String] and
      (__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[DateTime] and
      (__ \ 'phoneNumber).readNullable[String] and
      (__ \ 'identification).readNullable[IndividualIdentification] and
      (__ \ 'entityStart).read[DateTime]).apply(RemoveTrusteeIndividual.apply _)

}
