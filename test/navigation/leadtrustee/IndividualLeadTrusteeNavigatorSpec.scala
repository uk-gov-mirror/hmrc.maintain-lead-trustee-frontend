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

package navigation.leadtrustee

import base.SpecBase
import controllers.leadtrustee.individual.routes._
import models.UserAnswers
import navigation.Navigator
import pages.leadtrustee.individual._

class IndividualLeadTrusteeNavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "IndividualLeadTrusteeNavigator" when {

    "4mld" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      "Name page -> Date of birth page" in {
        navigator.nextPage(NamePage, baseAnswers)
          .mustBe(DateOfBirthController.onPageLoad())
      }

      "Date of birth page -> NINO yes/no page" in {
        navigator.nextPage(DateOfBirthPage, baseAnswers)
          .mustBe(UkCitizenController.onPageLoad())
      }

      "NINO yes/no page" when {
        val page = UkCitizenPage

        "-> YES -> NINO page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(NationalInsuranceNumberController.onPageLoad())
        }

        "-> NO -> Passport/ID card page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(PassportOrIdCardController.onPageLoad())
        }
      }

      "NINO page -> UK address yes/no page" in {
        navigator.nextPage(NationalInsuranceNumberPage, baseAnswers)
          .mustBe(LiveInTheUkYesNoController.onPageLoad())
      }

      "Passport/ID card page -> UK address yes/no page" in {
        navigator.nextPage(PassportOrIdCardDetailsPage, baseAnswers)
          .mustBe(LiveInTheUkYesNoController.onPageLoad())
      }

      "UK address yes/no page" when {
        val page = LiveInTheUkYesNoPage

        "-> YES -> UK address page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(UkAddressController.onPageLoad())
        }

        "-> NO -> Non-UK address page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(NonUkAddressController.onPageLoad())
        }
      }

      "UK address page -> Email yes/no page" in {
        navigator.nextPage(UkAddressPage, baseAnswers)
          .mustBe(EmailAddressYesNoController.onPageLoad())
      }

      "Non-UK address page -> Email address yes/no page" in {
        navigator.nextPage(NonUkAddressPage, baseAnswers)
          .mustBe(EmailAddressYesNoController.onPageLoad())
      }

      "Email address yes/no page" when {
        val page = EmailAddressYesNoPage

        "-> YES -> Email address page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(EmailAddressController.onPageLoad())
        }

        "-> NO -> Telephone number page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(TelephoneNumberController.onPageLoad())
        }
      }

      "Email address page -> Telephone number page" in {
        navigator.nextPage(EmailAddressPage, baseAnswers)
          .mustBe(TelephoneNumberController.onPageLoad())
      }

      "Telephone number page -> Check details page" in {
        navigator.nextPage(TelephoneNumberPage, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoadUpdated())
      }
    }

    "5mld" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

      "Name page -> Date of birth page" in {
        navigator.nextPage(NamePage, baseAnswers)
          .mustBe(DateOfBirthController.onPageLoad())
      }

      "Date of birth page -> UK nationality yes/no page" in {
        navigator.nextPage(DateOfBirthPage, baseAnswers)
          .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad())
      }

      "UK nationality yes/no page" when {
        val page = CountryOfNationalityInTheUkYesNoPage

        "-> YES -> NINO yes/no page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(UkCitizenController.onPageLoad())
        }

        "-> NO -> Nationality page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(CountryOfNationalityController.onPageLoad())
        }
      }

      "Nationality page -> NINO yes/no page" in {
        navigator.nextPage(CountryOfNationalityPage, baseAnswers)
          .mustBe(UkCitizenController.onPageLoad())
      }

      "NINO yes/no page" when {
        val page = UkCitizenPage

        "-> YES -> NINO page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(NationalInsuranceNumberController.onPageLoad())
        }

        "-> NO -> Passport/ID card page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(PassportOrIdCardController.onPageLoad())
        }
      }

      "NINO page -> UK residency yes/no page" in {
        navigator.nextPage(NationalInsuranceNumberPage, baseAnswers)
          .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad())
      }

      "Passport/ID card page -> UK residency yes/no page" in {
        navigator.nextPage(PassportOrIdCardDetailsPage, baseAnswers)
          .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad())
      }

      "UK residency yes/no page" when {
        val page = CountryOfResidenceInTheUkYesNoPage

        "-> YES -> UK address page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(UkAddressController.onPageLoad())
        }

        "-> NO -> Residency page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(CountryOfResidenceController.onPageLoad())
        }
      }

      "Residency page -> Non-UK address page" in {
        navigator.nextPage(CountryOfResidencePage, baseAnswers)
          .mustBe(NonUkAddressController.onPageLoad())
      }

      "UK address page -> Email yes/no page" in {
        navigator.nextPage(UkAddressPage, baseAnswers)
          .mustBe(EmailAddressYesNoController.onPageLoad())
      }

      "Non-UK address page -> Email address yes/no page" in {
        navigator.nextPage(NonUkAddressPage, baseAnswers)
          .mustBe(EmailAddressYesNoController.onPageLoad())
      }

      "Email address yes/no page" when {
        val page = EmailAddressYesNoPage

        "-> YES -> Email address page" in {
          val answers = baseAnswers.set(page, true).success.value

          navigator.nextPage(page, answers)
            .mustBe(EmailAddressController.onPageLoad())
        }

        "-> NO -> Telephone number page" in {
          val answers = baseAnswers.set(page, false).success.value

          navigator.nextPage(page, answers)
            .mustBe(TelephoneNumberController.onPageLoad())
        }
      }

      "Email address page -> Telephone number page" in {
        navigator.nextPage(EmailAddressPage, baseAnswers)
          .mustBe(TelephoneNumberController.onPageLoad())
      }

      "Telephone number page -> Check details page" in {
        navigator.nextPage(TelephoneNumberPage, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoadUpdated())
      }
    }
  }
}
