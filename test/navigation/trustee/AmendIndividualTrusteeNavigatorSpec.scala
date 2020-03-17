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

package navigation.trustee

import base.SpecBase
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.amend.individual._

class AmendIndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  val index = 0

  "Amend Individual trustee navigator" when {

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(controllers.trustee.amend.individual.routes.DateOfBirthYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> Yes -> Date of birth page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, true).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.DateOfBirthController.onPageLoad())
    }

    "Date of birth page -> Do you know NINO page" in {
      navigator.nextPage(DateOfBirthPage, emptyUserAnswers)
        .mustBe(controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> No -> Do you know NINO page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, false).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "Do you know NINO page -> Yes -> NINO page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, true).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.NationalInsuranceNumberController.onPageLoad())
    }

    "NINO page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, index).success.value

      navigator.nextPage(NationalInsuranceNumberPage, answers)
        .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
    }

    "Do you know NINO page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.AddressYesNoController.onPageLoad())
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.LiveInTheUkYesNoController.onPageLoad())
    }

    "Do you know address page -> No -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value
          .set(IndexPage, index).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, true).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.UkAddressController.onPageLoad())
    }

    "UK address page -> Do you know passport or ID card details page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad())
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, false).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.NonUkAddressController.onPageLoad())
    }

    "Non-UK address page -> Do you know passport or ID card details page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsYesNoController.onPageLoad())
    }

    "Do you know passport or ID card details page -> Yes -> Passport or ID card details page" in {
      val answers = emptyUserAnswers
        .set(PassportOrIdCardDetailsYesNoPage, true).success.value

      navigator.nextPage(PassportOrIdCardDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.amend.individual.routes.PassportOrIdCardDetailsController.onPageLoad())
    }

    "Passport or ID card details page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, index).success.value

      navigator.nextPage(PassportOrIdCardDetailsPage, answers)
        .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
    }

    "Do you know passport or ID card details page -> No -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(PassportOrIdCardDetailsYesNoPage, false).success.value
        .set(IndexPage, index).success.value

      navigator.nextPage(PassportOrIdCardDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
    }
  }
}
