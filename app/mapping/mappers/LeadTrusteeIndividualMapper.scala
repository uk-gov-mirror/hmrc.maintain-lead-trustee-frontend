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
import pages.leadtrustee.individual._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

import java.time.LocalDate

class LeadTrusteeIndividualMapper extends Logging {

  def map(answers: UserAnswers): Option[LeadTrusteeIndividual] = {
    val reads: Reads[LeadTrusteeIndividual] =
      (
        NamePage.path.read[Name] and
          DateOfBirthPage.path.read[LocalDate] and
          TelephoneNumberPage.path.read[String] and
          EmailAddressYesNoPage.path.read[Boolean].flatMap[Option[String]] {
            case true => EmailAddressPage.path.read[String].map(Some(_))
            case false => Reads( _=> JsSuccess(None))
          } and
          UkCitizenPage.path.read[Boolean].flatMap {
            case true => NationalInsuranceNumberPage.path.read[String].map(NationalInsuranceNumber(_)).widen[IndividualIdentification]
            case false => PassportOrIdCardDetailsPage.path.read[CombinedPassportOrIdCard].widen[IndividualIdentification]
          } and
          LiveInTheUkYesNoPage.path.read[Boolean].flatMap {
            case true => UkAddressPage.path.read[UkAddress].widen[Address]
            case false => NonUkAddressPage.path.read[NonUkAddress].widen[Address]
          }
        )(LeadTrusteeIndividual.apply _)

    answers.data.validate[LeadTrusteeIndividual](reads) match {
      case JsError(errors) =>
        logger.error(s"[UTR: ${answers.identifier}] Failed to rehydrate LeadTrusteeIndividual from UserAnswers due to $errors")
        None
      case JsSuccess(value, _) => Some(value)
    }
  }

}
