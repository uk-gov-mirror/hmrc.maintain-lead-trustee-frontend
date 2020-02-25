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

import play.api.libs.json.{Format, Json, Reads, Writes}

case class TrusteeType(trusteeInd: Option[TrusteeIndividual],
                    trusteeOrg: Option[TrusteeOrg])

object TrusteeType {
  implicit val trusteeTypeFormat: Format[TrusteeType] = Json.format[TrusteeType]
}

case class TrusteeOrg(lineNo: String,
                      bpMatchStatus: Option[String],
                      name: String,
                      phoneNumber: Option[String] = None,
                      email: Option[String] = None,
                      identification: Option[TrustIdentificationOrgType],
                      entityStart: String)

object TrusteeOrg {
  implicit val dateFormat: Format[LocalDate] = Format[LocalDate](Reads.DefaultLocalDateReads, Writes.DefaultLocalDateWrites)
  implicit val trusteeOrgTypeFormat: Format[TrusteeOrg] = Json.format[TrusteeOrg]
}

case class TrustIdentificationOrgType(safeId: Option[String],
                                      utr: Option[String],
                                      address: Option[AddressType])

object TrustIdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[TrustIdentificationOrgType] = Json.format[TrustIdentificationOrgType]
}
