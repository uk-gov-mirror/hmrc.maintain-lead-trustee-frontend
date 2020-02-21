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
import models._
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.individual._

class IndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator
  val index = 0

  "Individual trustee navigator" when {

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(NamePage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthYesNoController.onPageLoad(index))
    }

    "Do you know date of birth page -> Yes -> Date of birth page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage(index), true).success.value

      navigator.nextPage(DateOfBirthYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.DateOfBirthController.onPageLoad(index))
    }

    "Date of birth page -> Do you know NINO page" in {
      navigator.nextPage(DateOfBirthPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index))
    }

    "Do you know date of birth page -> No -> Do you know NINO page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage(index), false).success.value

      navigator.nextPage(DateOfBirthYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberYesNoController.onPageLoad(index))
    }

    "Do you know NINO page -> Yes -> NINO page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage(index), true).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberController.onPageLoad(index))
    }

    "NINO page -> When added as trustee page" in {
      navigator.nextPage(NationalInsuranceNumberPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.NationalInsuranceNumberController.onPageLoad(index))
    }

    "Do you know NINO page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage(index), false).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.AddressYesNoController.onPageLoad(index))
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), true).success.value

      navigator.nextPage(AddressYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.LiveInTheUkYesNoController.onPageLoad(index))
    }

    "Do you know address page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), false).success.value

      navigator.nextPage(AddressYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.AddressYesNoController.onPageLoad(index))
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage(index), true).success.value

      navigator.nextPage(LiveInTheUkYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.UkAddressController.onPageLoad(index))
    }

    "UK address page -> Do you know passport details page" in {
      navigator.nextPage(UkAddressPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad(index))
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage(index), false).success.value

      navigator.nextPage(LiveInTheUkYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.NonUkAddressController.onPageLoad(index))
    }

    "Non-UK address page -> Do you know passport details page" in {
      navigator.nextPage(NonUkAddressPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsYesNoController.onPageLoad(index))
    }

    "Do you know passport details page -> Yes -> Passport details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage(index), true).success.value

      navigator.nextPage(PassportDetailsYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsController.onPageLoad(index))
    }

    "Passport details page -> When added as trustee page" in {
      navigator.nextPage(PassportDetailsPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.PassportDetailsController.onPageLoad(index))
    }

    "Do you know passport details page -> No -> Do you know ID card details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage(index), false).success.value

      navigator.nextPage(PassportDetailsYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad(index))
    }

    "Do you know ID card details page -> Yes -> ID card details page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage(index), true).success.value

      navigator.nextPage(IdCardDetailsYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad(index))
    }

    "ID card details page -> When added as trustee page" in {
      navigator.nextPage(IdCardDetailsPage(index), NormalMode, emptyUserAnswers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsController.onPageLoad(index))
    }

    "Do you know ID card details page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage(index), false).success.value

      navigator.nextPage(IdCardDetailsYesNoPage(index), NormalMode, answers)
        .mustBe(controllers.trustee.individual.routes.IdCardDetailsYesNoController.onPageLoad(index))
    }
  }
}
