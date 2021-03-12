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

import models.Constant.GB
import models._
import pages.trustee.WhenAddedPage
import pages.trustee.amend.individual.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import pages.trustee.individual._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads}

import java.time.LocalDate

class TrusteeIndividualMapper extends Mapper[TrusteeIndividual] {

  override val reads: Reads[TrusteeIndividual] = (
    NamePage.path.read[Name] and
      DateOfBirthPage.path.readNullable[LocalDate] and
      Reads(_ => JsSuccess(None)) and
      readIdentification and
      readAddress and
      readCountryOfResidence and
      readCountryOfNationality and
      readMentalCapacity and
      WhenAddedPage.path.read[LocalDate] and
      Reads(_ => JsSuccess(true))
    )(TrusteeIndividual.apply _)

  private def readIdentification: Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.readNullable[Boolean].flatMap[Option[IndividualIdentification]] {
      case Some(true) => NationalInsuranceNumberPage.path.read[String].map(nino => Some(NationalInsuranceNumber(nino)))
      case _ => readPassportOrIdCard
    }
  }

  private def readPassportOrIdCard: Reads[Option[IndividualIdentification]] = {
    val identification = for {
      hasNino <- NationalInsuranceNumberYesNoPage.path.readWithDefault(false)
      hasAddress <- AddressYesNoPage.path.readWithDefault(false)
      hasPassport <- PassportDetailsYesNoPage.path.readWithDefault(false)
      hasIdCard <- IdCardDetailsYesNoPage.path.readWithDefault(false)
      hasPassportOrIdCard <- PassportOrIdCardDetailsYesNoPage.path.readWithDefault(false)
    } yield (hasNino, hasAddress, hasPassport, hasIdCard, hasPassportOrIdCard)

    identification.flatMap[Option[IndividualIdentification]] {
      case (false, true, true, false, false) => PassportDetailsPage.path.read[Passport].map(Some(_))
      case (false, true, false, true, false) => IdCardDetailsPage.path.read[IdCard].map(Some(_))
      case (false, true, false, false, true) => PassportOrIdCardDetailsPage.path.read[CombinedPassportOrIdCard].map(Some(_))
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

  private def readCountryOfNationality: Reads[Option[String]] = {
    CountryOfNationalityYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
      case Some(true) => CountryOfNationalityInTheUkYesNoPage.path.read[Boolean].flatMap {
        case true => Reads(_ => JsSuccess(Some(GB)))
        case false => CountryOfNationalityPage.path.read[String].map(Some(_))
      }
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readMentalCapacity: Reads[Option[Boolean]] = {
    MentalCapacityYesNoPage.path.readNullable[Boolean].flatMap[Option[Boolean]] {
      case Some(true) => Reads(_ => JsSuccess(Some(false)))
      case Some(false) => Reads(_ => JsSuccess(Some(true)))
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
