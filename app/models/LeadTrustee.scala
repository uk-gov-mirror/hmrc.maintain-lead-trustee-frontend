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
import play.api.libs.json.{Format, JsResult, JsValue, Json, Reads, Writes}
import play.api.libs.json.{JodaReads, JodaWrites}

case class PassportType(number: String,
                        expirationDate: DateTime,
                        countryOfIssue: String)

object PassportType {

  implicit val dateFormat: Format[DateTime] = Format[DateTime]( JodaReads.jodaDateReads(dateTimePattern), JodaWrites.jodaDateWrites(dateTimePattern) )
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class DisplayTrustIdentificationType(safeId: Option[String],
                                          nino: Option[String],
                                          passport: Option[PassportType],
                                          address: Option[Address])

object DisplayTrustIdentificationType {
  implicit val identificationTypeFormat: Format[DisplayTrustIdentificationType] = Json.format[DisplayTrustIdentificationType]
}

case class DisplayTrustIdentificationOrgType(safeId: Option[String],
                                             utr: Option[String],
                                             address: Option[Address])

object DisplayTrustIdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[DisplayTrustIdentificationOrgType] = Json.format[DisplayTrustIdentificationOrgType]
}

case class DisplayTrustLeadTrusteeIndType(
                                           lineNo: String,
                                           bpMatchStatus: Option[String],
                                           name: Name,
                                           dateOfBirth: DateTime,
                                           phoneNumber: String,
                                           email: Option[String] = None,
                                           identification: DisplayTrustIdentificationType,
                                           entityStart: String
                                         )

object DisplayTrustLeadTrusteeIndType {

  implicit val dateFormat: Format[DateTime] = Format[DateTime](JodaReads.jodaDateReads(dateTimePattern), JodaWrites.jodaDateWrites(dateTimePattern))
  implicit val leadTrusteeIndTypeFormat: Format[DisplayTrustLeadTrusteeIndType] = Json.format[DisplayTrustLeadTrusteeIndType]

}

case class DisplayTrustLeadTrusteeOrgType(
                                           lineNo: String,
                                           bpMatchStatus: Option[String],
                                           name: String,
                                           phoneNumber: String,
                                           email: Option[String] = None,
                                           identification: DisplayTrustIdentificationOrgType,
                                           entityStart: String
                                         )

object DisplayTrustLeadTrusteeOrgType {
  implicit val leadTrusteeOrgTypeFormat: Format[DisplayTrustLeadTrusteeOrgType] = Json.format[DisplayTrustLeadTrusteeOrgType]
}

case class DisplayTrustLeadTrusteeType(
                                        leadTrusteeInd: Option[DisplayTrustLeadTrusteeIndType] = None,
                                        leadTrusteeOrg: Option[DisplayTrustLeadTrusteeOrgType] = None
                                      )

object DisplayTrustLeadTrusteeType {

  implicit val dateFormat: Format[DateTime] = Format[DateTime](JodaReads.jodaDateReads(dateTimePattern), JodaWrites.jodaDateWrites(dateTimePattern))

  implicit val writes: Writes[DisplayTrustLeadTrusteeType] = Json.writes[DisplayTrustLeadTrusteeType]

  object LeadTrusteeReads extends Reads[DisplayTrustLeadTrusteeType] {

    override def reads(json: JsValue): JsResult[DisplayTrustLeadTrusteeType] = {

      json.validate[DisplayTrustLeadTrusteeIndType].map {
        leadTrusteeInd =>
          DisplayTrustLeadTrusteeType(leadTrusteeInd = Some(leadTrusteeInd))
      }.orElse {
        json.validate[DisplayTrustLeadTrusteeOrgType].map {
          org =>
            DisplayTrustLeadTrusteeType(leadTrusteeOrg = Some(org))
        }
      }
    }
  }

  implicit val reads : Reads[DisplayTrustLeadTrusteeType] = LeadTrusteeReads
}
