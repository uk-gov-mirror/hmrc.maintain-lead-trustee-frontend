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
import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait LeadTrustee

object LeadTrustee {

  implicit val writes: Writes[LeadTrustee] = Writes[LeadTrustee](_ => ???)

  implicit val reads : Reads[LeadTrustee] = Reads(json =>
    json.validate[LeadTrusteeIndividual] orElse
      json.validate[LeadTrusteeOrganisation])
}

case class LeadTrusteeIndividual(
                                  //lineNo: String,
                                  //bpMatchStatus: Option[String],
                                  name: Name,
                                  dateOfBirth: LocalDate,
                                  phoneNumber: String,
                                  email: Option[String] = None,
                                  identification: IndividualIdentification,
                                  address: Option[Address]//,
                                  //entityStart: String
                                ) extends LeadTrustee

object LeadTrusteeIndividual {
  implicit val reads: Reads[LeadTrusteeIndividual] =
    (//(__ \ 'lineNo).read[String] and
    //(__ \ 'bpMatchStatus).readNullable[String] and
    (__ \ 'name).read[Name] and
    (__ \ 'dateOfBirth).read[LocalDate] and
    (__ \ 'phoneNumber).read[String] and
    (__ \ 'email).readNullable[String] and
    (__ \ 'identification).read[IndividualIdentification] and
    (__ \ 'identification \ 'address).readNullable[Address] //and
    /*(__ \ 'entityStart).read[String]*/).apply(LeadTrusteeIndividual.apply _)
}

case class LeadTrusteeOrganisation(
                                    lineNo: String,
                                    bpMatchStatus: Option[String],
                                    name: String,
                                    phoneNumber: String,
                                    email: Option[String] = None,
                                    utr: Option[String],
                                    address: Address,
                                    entityStart: String
                                  ) extends LeadTrustee

object LeadTrusteeOrganisation {
  implicit val reads : Reads[LeadTrusteeOrganisation] =
    ((__ \ 'lineNo).read[String] and
    (__ \ 'bpMatchStatus).readNullable[String] and
    (__ \ 'name).read[String] and
    (__ \ 'phoneNumber).read[String] and
    (__ \ 'email).readNullable[String] and
    (__ \ 'identification \ 'utr).readNullable[String] and
    (__ \ 'identification \ 'address).read[Address] and
    (__ \ 'entityStart).read[String]).apply(LeadTrusteeOrganisation.apply _)
}


