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
import models.NormalMode
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.organisation._

class OrganisationTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Organisation trustee navigator" when {
    
    val mode = NormalMode

    "Name page -> Do you know UTR page" in {
      navigator.nextPage(NamePage, mode, emptyUserAnswers)
        .mustBe(controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode))
    }

    "Do you know UTR page -> Yes -> UTR page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage, true).success.value

      navigator.nextPage(UtrYesNoPage, mode, answers)
        .mustBe(controllers.trustee.organisation.routes.UtrController.onPageLoad(mode))
    }

    "UTR page -> When added as trustee page" in {
      navigator.nextPage(UtrPage, mode, emptyUserAnswers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Do you know UTR page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage, false).success.value

      navigator.nextPage(UtrYesNoPage, mode, answers)
        .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, mode, answers)
        .mustBe(controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode))
    }

    "Do you know address page -> No -> When added as trustee page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, mode, answers)
        .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressInTheUkYesNoPage, true).success.value

      navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
        .mustBe(controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode))
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressInTheUkYesNoPage, false).success.value

      navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
        .mustBe(controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode))
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
