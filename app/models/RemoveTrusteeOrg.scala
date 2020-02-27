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

import java.time.LocalDate

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json, Reads, Writes}

case class RemoveTrusteeOrg(lineNo: Option[String],
                            bpMatchStatus: Option[String],
                            name: String,
                            phoneNumber: Option[String] = None,
                            email: Option[String] = None,
                            identification: Option[TrustIdentificationOrgType],
                            entityStart: DateTime)

object RemoveTrusteeOrg {
  implicit val dateFormat: Format[DateTime] = Format[DateTime](Reads.jodaDateReads(dateTimePattern), Writes.jodaDateWrites(dateTimePattern))
  implicit val trusteeOrgTypeFormat: Format[RemoveTrusteeOrg] = Json.format[RemoveTrusteeOrg]
}

case class TrustIdentificationOrgType(safeId: Option[String],
                                      utr: Option[String],
                                      address: Option[AddressType])

object TrustIdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[TrustIdentificationOrgType] = Json.format[TrustIdentificationOrgType]
}

