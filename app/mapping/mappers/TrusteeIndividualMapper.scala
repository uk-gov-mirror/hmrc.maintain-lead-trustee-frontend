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
import pages.trustee.WhenAddedPage
import pages.trustee.amend.individual.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import pages.trustee.individual._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}
import java.time.LocalDate

import models.Constant.GB

class TrusteeIndividualMapper extends Logging {

  def map(userAnswers: UserAnswers, adding: Boolean): Option[TrusteeIndividual] = {
    val reads: Reads[TrusteeIndividual] =
      (
        NamePage.path.read[Name] and
          DateOfBirthPage.path.readNullable[LocalDate] and
          Reads(_ => JsSuccess(None)) and
          readIdentification(adding) and
          readCountryOfResidence and
          readAddress and
          WhenAddedPage.path.read[LocalDate] and
          Reads(_ => JsSuccess(true))
        ).apply(TrusteeIndividual.apply _ )

    userAnswers.data.validate[TrusteeIndividual](reads) match {
      case JsError(errors) =>
        logger.error(s"[UTR: ${userAnswers.identifier}] Failed to rehydrate TrusteeIndividual from UserAnswers due to $errors")
        None
      case JsSuccess(value, _) =>
        Some(value)
    }
  }

  private def readIdentification(adding: Boolean): Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap[Option[IndividualIdentification]] {
      case true => NationalInsuranceNumberPage.path.read[String].map(nino => Some(NationalInsuranceNumber(nino)))
      case false => if (adding) readSeparatePassportOrIdCard else readCombinedPassportOrIdCard
    }
  }

  private def readSeparatePassportOrIdCard: Reads[Option[IndividualIdentification]] = {
    (for {
      hasNino <- NationalInsuranceNumberYesNoPage.path.readWithDefault(false)
      hasAddress <- AddressYesNoPage.path.readWithDefault(false)
      hasPassport <- PassportDetailsYesNoPage.path.readWithDefault(false)
      hasIdCard <- IdCardDetailsYesNoPage.path.readWithDefault(false)
    } yield (hasNino, hasAddress, hasPassport, hasIdCard)).flatMap[Option[IndividualIdentification]] {
      case (false, true, true, false) => PassportDetailsPage.path.read[Passport].map(Some(_))
      case (false, true, false, true) => IdCardDetailsPage.path.read[IdCard].map(Some(_))
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readCombinedPassportOrIdCard: Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap {
      case true => Reads(_ => JsSuccess(None))
      case false => readPassportOrIdIfAddressExists
    }
  }

  private def readPassportOrIdIfAddressExists: Reads[Option[IndividualIdentification]] = {
    AddressYesNoPage.path.read[Boolean].flatMap {
      case true => PassportOrIdCardDetailsYesNoPage.path.read[Boolean].flatMap[Option[IndividualIdentification]] {
        case true => PassportOrIdCardDetailsPage.path.read[CombinedPassportOrIdCard].map(Some(_))
        case false => Reads(_ => JsSuccess(None))
      }
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readCountryOfResidence: Reads[Option[String]] = {
    CountryOfResidenceYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
      case Some(true) => CountryOfResidenceInTheUkYesNoPage.path.read[Boolean].flatMap {
        case true => Reads(_ => JsSuccess(Some(GB)))
        case false => CountryOfResidencePage.path.read[String].map(Some(_))
      }
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readAddress: Reads[Option[Address]] = {
    LiveInTheUkYesNoPage.path.readNullable[Boolean].flatMap {
      case Some(true) => UkAddressPage.path.readNullable[UkAddress].widen[Option[Address]]
      case Some(false) => NonUkAddressPage.path.readNullable[NonUkAddress].widen[Option[Address]]
      case _ => Reads(_ => JsSuccess(None)).widen[Option[Address]]
    }
  }

}
