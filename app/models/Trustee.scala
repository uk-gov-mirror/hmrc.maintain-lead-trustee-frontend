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

package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._


sealed trait Trustee {
  val provisional: Boolean
  def isNewlyAdded: Boolean = provisional
}

object Trustee {

  implicit val writes: Writes[Trustee] = Writes[Trustee] {
    case trustee: TrusteeIndividual => Json.toJson(trustee)(TrusteeIndividual.writes)
    case trustee: TrusteeOrganisation => Json.toJson(trustee)(TrusteeOrganisation.formats)
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
                             identification: Option[IndividualIdentification],
                             address: Option[Address],
                             countryOfResidence: Option[String] = None,
                             nationality: Option[String] = None,
                             mentalCapacityYesNo: Option[Boolean] = None,
                             entityStart: LocalDate,
                             provisional: Boolean) extends Trustee

object TrusteeIndividual {

  def readNullableAtSubPath[T:Reads](subPath : JsPath) : Reads[Option[T]] = Reads (
    _.transform(subPath.json.pick)
      .flatMap(_.validate[T])
      .map(Some(_))
      .recoverWith(_ => JsSuccess(None))
  )

  implicit val reads: Reads[TrusteeIndividual] = (
    (__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      (__ \ 'phoneNumber).readNullable[String] and
      __.lazyRead(readNullableAtSubPath[IndividualIdentification](__ \ 'identification)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'countryOfResidence).readNullable[String] and
      (__ \ 'nationality).readNullable[String] and
      (__ \ 'legallyIncapable).readNullable[Boolean].map(_.map(!_)) and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").read[Boolean]
    )(TrusteeIndividual.apply _)

  implicit val writes: Writes[TrusteeIndividual] = (
    (__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).writeNullable[LocalDate] and
      (__ \ 'phoneNumber).writeNullable[String] and
      (__ \ 'identification).writeNullable[IndividualIdentification] and
      (__ \ 'identification \ 'address).writeNullable[Address] and
      (__ \ 'countryOfResidence).writeNullable[String] and
      (__ \ 'nationality).writeNullable[String] and
      (__ \ 'legallyIncapable).writeNullable[Boolean](x => JsBoolean(!x)) and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    )(unlift(TrusteeIndividual.unapply))
}

case class TrusteeOrganisation(name: String,
                               phoneNumber: Option[String] = None,
                               email: Option[String] = None,
                               identification: Option[TrustIdentificationOrgType],
                               countryOfResidence: Option[String] = None,
                               entityStart: LocalDate,
                               provisional: Boolean) extends Trustee

object TrusteeOrganisation {
  implicit val formats: Format[TrusteeOrganisation] = Json.format[TrusteeOrganisation]
}


case class TrustIdentificationOrgType(safeId: Option[String],
                                      utr: Option[String],
                                      address: Option[Address])

object TrustIdentificationOrgType {
  implicit val formats: Format[TrustIdentificationOrgType] = Json.format[TrustIdentificationOrgType]
}
