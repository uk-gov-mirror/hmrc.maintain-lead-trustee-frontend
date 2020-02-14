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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.leadtrustee.individual._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryTelephoneNumberUserAnswersEntry: Arbitrary[(TelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TelephoneNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIdentificationDetailOptionsUserAnswersEntry: Arbitrary[(IdentificationDetailOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IdentificationDetailOptionsPage.type]
        value <- arbitrary[IdentificationDetailOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmailAddressYesNoUserAnswersEntry: Arbitrary[(EmailAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmailAddressUserAnswersEntry: Arbitrary[(EmailAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUkCitizenUserAnswersEntry: Arbitrary[(UkCitizenPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UkCitizenPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUkAddressUserAnswersEntry: Arbitrary[(UkAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UkAddressPage.type]
        value <- arbitrary[UkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPassportDetailsUserAnswersEntry: Arbitrary[(PassportDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PassportDetailsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNonUkAddressUserAnswersEntry: Arbitrary[(NonUkAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NonUkAddressPage.type]
        value <- arbitrary[NonUkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNationalInsuranceNumberUserAnswersEntry: Arbitrary[(NationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNameUserAnswersEntry: Arbitrary[(NamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NamePage.type]
        value <- arbitrary[Name].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLiveInTheUkYesNoPageUserAnswersEntry: Arbitrary[(LiveInTheUkYesNoPagePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LiveInTheUkYesNoPagePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIdCardDetailsUserAnswersEntry: Arbitrary[(IdCardDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IdCardDetailsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateOfBirthUserAnswersEntry: Arbitrary[(DateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

}
