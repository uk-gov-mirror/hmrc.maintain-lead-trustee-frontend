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

package navigation.trustee

import base.SpecBase
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.individual._

class IndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Individual trustee navigator" when {

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> Yes -> Date of birth page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, true).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthController.onPageLoad())
    }

    "Date of birth page -> Do you know NINO page" in {
      navigator.nextPage(DateOfBirthPage, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> No -> Do you know NINO page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, false).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "Do you know NINO page -> Yes -> NINO page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, true).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberController.onPageLoad())
    }

    "NINO page -> When added as trustee page" in {
      navigator.nextPage(NationalInsuranceNumberPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Do you know NINO page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.AddressYesNoController.onPageLoad())
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.LiveInTheUkYesNoController.onPageLoad())
    }

    "Do you know address page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, true).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.UkAddressController.onPageLoad())
    }

    "UK address page -> Do you know passport details page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad())
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, false).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.NonUkAddressController.onPageLoad())
    }

    "Non-UK address page -> Do you know passport details page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad())
    }

    "Do you know passport details page -> Yes -> Passport details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage, true).success.value

      navigator.nextPage(PassportDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsController.onPageLoad())
    }

    "Passport details page -> When added as trustee page" in {
      navigator.nextPage(PassportDetailsPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Do you know passport details page -> No -> Do you know ID card details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage, false).success.value

      navigator.nextPage(PassportDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad())
    }

    "Do you know ID card details page -> Yes -> ID card details page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage, true).success.value

      navigator.nextPage(IdCardDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad())
    }

    "ID card details page -> When added as trustee page" in {
      navigator.nextPage(IdCardDetailsPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Do you know ID card details page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage, false).success.value

      navigator.nextPage(IdCardDetailsYesNoPage, answers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }
  }
}
