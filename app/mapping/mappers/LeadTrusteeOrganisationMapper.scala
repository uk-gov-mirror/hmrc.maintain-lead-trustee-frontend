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

package mapping.mappers

import models._
import pages.leadtrustee.organisation._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json._

class LeadTrusteeOrganisationMapper extends Logging {

  def map(answers: UserAnswers): Option[LeadTrusteeOrganisation] = {
    val reads: Reads[LeadTrusteeOrganisation] =
      (
        NamePage.path.read[String] and
          TelephoneNumberPage.path.read[String] and
          EmailAddressPage.path.readNullable[String] and
          UtrPage.path.readNullable[String] and
          AddressInTheUkYesNoPage.path.read[Boolean].flatMap {
            case true => UkAddressPage.path.read[UkAddress].widen[Address]
            case false => NonUkAddressPage.path.read[Address].widen[Address]
          }
        ).apply(LeadTrusteeOrganisation.apply _)

    answers.data.validate[LeadTrusteeOrganisation](reads) match {
      case JsError(errors) =>
        logger.error(s"[UTR: ${answers.utr}] Failed to rehydrate LeadTrusteeOrganisation from UserAnswers due to $errors")
        None
      case JsSuccess(value, _) => Some(value)
    }
  }

}
