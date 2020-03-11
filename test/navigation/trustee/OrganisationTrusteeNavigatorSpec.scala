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
import pages.trustee.organisation._

class OrganisationTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Individual trustee navigator" when {

    "Name page -> Do you know UTR page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad())
    }

    "Do you know UTR page -> Yes -> UTR page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage, true).success.value

      navigator.nextPage(UtrYesNoPage, answers)
        .mustBe(controllers.trustee.organisation.routes.UtrController.onPageLoad())
    }

    "UTR page -> When added as trustee page" in {
      navigator.nextPage(UtrPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Do you know UTR page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage, false).success.value

      navigator.nextPage(UtrYesNoPage, answers)
        .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad())
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad())
    }

    "Do you know address page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, true).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(controllers.trustee.organisation.routes.UkAddressController.onPageLoad())
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, false).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad())
    }

    "UK address page -> When added as trustee page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Non-UK address page -> When added as trustee page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }
  }
}
