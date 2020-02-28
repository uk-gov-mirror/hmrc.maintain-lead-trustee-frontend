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

package navigation.leadtrustee

import base.SpecBase
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.leadtrustee.organisation._

class OrganisationLeadTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Organisation lead trustee navigator" when {

    "Is UK registered business -> Yes -> Name page" in {
      val answers = emptyUserAnswers
        .set(RegisteredInUkYesNoPage, true).success.value

      navigator.nextPage(RegisteredInUkYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.NameController.onPageLoad())
    }

    "Is UK registered business -> No -> Name page" in {
      val answers = emptyUserAnswers
        .set(RegisteredInUkYesNoPage, false).success.value

      navigator.nextPage(RegisteredInUkYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.NameController.onPageLoad())
    }

    "(Is UK registered business -> Yes) -> Name page -> UTR page" in {
      val answers = emptyUserAnswers
        .set(RegisteredInUkYesNoPage, true).success.value

      navigator.nextPage(NamePage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.UtrController.onPageLoad())
    }

    "UTR page -> Is address in UK page" in {
      navigator.nextPage(UtrPage, emptyUserAnswers)
        .mustBe(controllers.leadtrustee.organisation.routes.LiveInTheUkYesNoController.onPageLoad())
    }

    "(Is UK registered business -> No) -> Name page -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(RegisteredInUkYesNoPage, false).success.value

      navigator.nextPage(NamePage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.LiveInTheUkYesNoController.onPageLoad())
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, true).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.UkAddressController.onPageLoad())
    }

    "UK address page -> Do you know email address page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(controllers.leadtrustee.organisation.routes.EmailAddressYesNoController.onPageLoad())
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, false).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.NonUkAddressController.onPageLoad())
    }

    "Non-UK address page -> Do you know email address page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(controllers.leadtrustee.organisation.routes.EmailAddressYesNoController.onPageLoad())
    }

    "Do you know email address page -> Yes -> Email address page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage, true).success.value

      navigator.nextPage(EmailAddressYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.EmailAddressController.onPageLoad())
    }

    "Email address page -> Telephone number page" in {
      navigator.nextPage(EmailAddressPage, emptyUserAnswers)
        .mustBe(controllers.leadtrustee.organisation.routes.TelephoneNumberController.onPageLoad())
    }

    "Do you know email address page -> No -> Telephone number page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage, false).success.value

      navigator.nextPage(EmailAddressYesNoPage, answers)
        .mustBe(controllers.leadtrustee.organisation.routes.TelephoneNumberController.onPageLoad())
    }

    "Telephone number page -> Check details page" in {
      navigator.nextPage(TelephoneNumberPage, emptyUserAnswers)
        .mustBe(controllers.leadtrustee.routes.DetailsController.onPageLoad())
    }
  }
}
