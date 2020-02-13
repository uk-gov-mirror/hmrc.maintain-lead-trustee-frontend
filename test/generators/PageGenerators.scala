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

import org.scalacheck.Arbitrary
import pages._
import pages.leadtrustee.individual.{AddressYesNoPagePage, DateOfBirthPage, DateOfBirthYesNoPagePage, IdCardDetailsPage, IdCardYesNoPagePage, LiveInTheUkYesNoPagePage, NamePage, NationalInsuranceNumberPage, NationalInsuranceNumberyesNoPagePage, NonUkAddressPage, PassportDetailsPage, PassportYesNoPagePage, UkAddressPage}

trait PageGenerators {

  implicit lazy val arbitraryUkAddressPage: Arbitrary[UkAddressPage.type] =
    Arbitrary(UkAddressPage)

  implicit lazy val arbitraryPassportYesNoPagePage: Arbitrary[PassportYesNoPagePage.type] =
    Arbitrary(PassportYesNoPagePage)

  implicit lazy val arbitraryPassportDetailsPage: Arbitrary[PassportDetailsPage.type] =
    Arbitrary(PassportDetailsPage)

  implicit lazy val arbitraryNonUkAddressPage: Arbitrary[NonUkAddressPage.type] =
    Arbitrary(NonUkAddressPage)

  implicit lazy val arbitraryNationalInsuranceNumberyesNoPagePage: Arbitrary[NationalInsuranceNumberyesNoPagePage.type] =
    Arbitrary(NationalInsuranceNumberyesNoPagePage)

  implicit lazy val arbitraryNationalInsuranceNumberPage: Arbitrary[NationalInsuranceNumberPage.type] =
    Arbitrary(NationalInsuranceNumberPage)

  implicit lazy val arbitraryNamePage: Arbitrary[NamePage.type] =
    Arbitrary(NamePage)

  implicit lazy val arbitraryLiveInTheUkYesNoPagePage: Arbitrary[LiveInTheUkYesNoPagePage.type] =
    Arbitrary(LiveInTheUkYesNoPagePage)

  implicit lazy val arbitraryIdCardYesNoPagePage: Arbitrary[IdCardYesNoPagePage.type] =
    Arbitrary(IdCardYesNoPagePage)

  implicit lazy val arbitraryIdCardDetailsPage: Arbitrary[IdCardDetailsPage.type] =
    Arbitrary(IdCardDetailsPage)

  implicit lazy val arbitraryDateOfBirthYesNoPagePage: Arbitrary[DateOfBirthYesNoPagePage.type] =
    Arbitrary(DateOfBirthYesNoPagePage)

  implicit lazy val arbitraryDateOfBirthPage: Arbitrary[DateOfBirthPage.type] =
    Arbitrary(DateOfBirthPage)

  implicit lazy val arbitraryAddressYesNoPagePage: Arbitrary[AddressYesNoPagePage.type] =
    Arbitrary(AddressYesNoPagePage)
}
