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

import play.api.libs.json.{Format, JsError, JsPath, JsSuccess, JsValue, Json, JsonValidationError, Reads, Writes}

sealed trait Trustee {
  val provisional : Boolean

  def isNewlyAdded : Boolean = provisional
}

object Trustee {

  implicit val writes: Writes[Trustee] = Writes[Trustee] {
    case lti:TrusteeIndividual => Json.toJson(lti)(TrusteeIndividual.formats)
    case lto:TrusteeOrganisation => Json.toJson(lto)(TrusteeOrganisation.formats)
  }

  implicit val reads : Reads[Trustee] = Reads { data : JsValue =>
    val allErrors: Either[Seq[(JsPath, Seq[JsonValidationError])], Trustee] = for {
      indErrs <- (data \ "trusteeInd").validate[TrusteeIndividual].asEither.left
      orgErrs <- (data \ "trusteeOrg").validate[TrusteeOrganisation].asEither.left
    } yield indErrs.map(pair => (pair._1, JsonValidationError("Failed to read as TrusteeIndividual") +: pair._2)) ++
      orgErrs.map(pair => (pair._1, JsonValidationError("Failed to read as TrusteeOrganisation") +: pair._2))

    allErrors match {
      case Right(t) => JsSuccess(t)
      case Left(errs) => JsError(errs)
    }
  }
}

case class TrusteeIndividual(name: Name,
                             dateOfBirth: Option[LocalDate],
                             phoneNumber: Option[String],
                             identification: Option[TrustIdentification],
                             entityStart: LocalDate,
                             provisional: Boolean) extends Trustee

object TrusteeIndividual {

  implicit val dateFormat: Format[LocalDate] = Format[LocalDate](Reads.DefaultLocalDateReads, Writes.DefaultLocalDateWrites)

  implicit val formats: Format[TrusteeIndividual] = Json.format[TrusteeIndividual]
}

case class TrusteeOrganisation(name: String,
                               phoneNumber: Option[String] = None,
                               email: Option[String] = None,
                               identification: Option[TrustIdentificationOrgType],
                               entityStart: LocalDate,
                               provisional: Boolean) extends Trustee

object TrusteeOrganisation {
  implicit val dateFormat: Format[LocalDate] = Format[LocalDate](Reads.DefaultLocalDateReads, Writes.DefaultLocalDateWrites)

  implicit val formats: Format[TrusteeOrganisation] = Json.format[TrusteeOrganisation]
}

case class TrustIdentification(safeId: Option[String],
                               nino: Option[String],
                               passport: Option[Passport],
                               address: Option[AddressType])

object TrustIdentification {
  implicit val formats: Format[TrustIdentification] = Json.format[TrustIdentification]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {
  implicit val formats: Format[AddressType] = Json.format[AddressType]
}


case class TrustIdentificationOrgType(safeId: Option[String],
                                      utr: Option[String],
                                      address: Option[AddressType])

object TrustIdentificationOrgType {
  implicit val formats: Format[TrustIdentificationOrgType] = Json.format[TrustIdentificationOrgType]
}
