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
import pages.trustee.individual._
import controllers.trustee.individual.{routes => rts}

class AddIndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Individual trustee navigator" when {
    val mode = NormalMode
    
    "4mld" must {
      "Name page -> Do you know date of birth page" in {
        navigator.nextPage(NamePage, emptyUserAnswers)
          .mustBe(rts.DateOfBirthYesNoController.onPageLoad())
      }

      "Do you know date of birth page -> Yes -> Date of birth page" in {
        val answers = emptyUserAnswers
          .set(DateOfBirthYesNoPage, true).success.value

        navigator.nextPage(DateOfBirthYesNoPage, answers)
          .mustBe(rts.DateOfBirthController.onPageLoad())
      }

      "Date of birth page -> Do you know NINO page" in {
        navigator.nextPage(DateOfBirthPage, emptyUserAnswers)
          .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad())
      }

      "Do you know date of birth page -> No -> Do you know NINO page" in {
        val answers = emptyUserAnswers
          .set(DateOfBirthYesNoPage, false).success.value

        navigator.nextPage(DateOfBirthYesNoPage, answers)
          .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad())
      }

      "Do you know NINO page -> Yes -> NINO page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
          .mustBe(rts.NationalInsuranceNumberController.onPageLoad())
      }

      "NINO page -> When added as trustee page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberPage, answers)
          .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
      }

      "Do you know NINO page -> No -> Do you know address page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad())
      }

      "Do you know address page -> Yes -> Is address in UK page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, answers)
          .mustBe(rts.LiveInTheUkYesNoController.onPageLoad())
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
          .mustBe(rts.UkAddressController.onPageLoad())
      }

      "UK address page -> Do you know passport details page" in {
        navigator.nextPage(UkAddressPage, emptyUserAnswers)
          .mustBe(rts.PassportDetailsYesNoController.onPageLoad())
      }

      "Is address in UK page -> No -> Non-UK address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, answers)
          .mustBe(rts.NonUkAddressController.onPageLoad())
      }

      "Non-UK address page -> Do you know passport details page" in {
        navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
          .mustBe(rts.PassportDetailsYesNoController.onPageLoad())
      }

      "Do you know passport details page -> Yes -> Passport details page" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage, true).success.value

        navigator.nextPage(PassportDetailsYesNoPage, answers)
          .mustBe(rts.PassportDetailsController.onPageLoad())
      }

      "Passport details page -> When added as trustee page" in {
        navigator.nextPage(PassportDetailsPage, emptyUserAnswers)
          .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
      }

      "Do you know passport details page -> No -> Do you know ID card details page" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage, false).success.value

        navigator.nextPage(PassportDetailsYesNoPage, answers)
          .mustBe(rts.IdCardDetailsYesNoController.onPageLoad())
      }

      "Do you know ID card details page -> Yes -> ID card details page" in {
        val answers = emptyUserAnswers
          .set(IdCardDetailsYesNoPage, true).success.value

        navigator.nextPage(IdCardDetailsYesNoPage, answers)
          .mustBe(rts.IdCardDetailsController.onPageLoad())
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

    "5mld" must {
      
      "taxable" must {
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

        "Do you know date of birth page -> No -> Country of Nationality page" in {
          val answers = baseAnswers
            .set(DateOfBirthYesNoPage, false).success.value

          navigator.nextPage(DateOfBirthYesNoPage, answers)
            .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(NormalMode))
        }

        "Date of birth page -> Country of Nationality page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(NormalMode))
        }

        "Country of Nationality Pages" must {
          "Yes No page -> Yes -> Country of Nationality In The Uk page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode))
          }

          "Yes No page -> No -> Nino Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad())
          }

          "In The Uk page -> Yes -> Nino Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityInTheUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, mode, answers)
              .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad())
          }

          "In The Uk page -> No -> Country of Nationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityInTheUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "page -> Nino Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, "ES").success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad())
          }
        }

        "Nino Pages" must {
          "Do you know NINO page -> Yes -> NINO page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
              .mustBe(rts.NationalInsuranceNumberController.onPageLoad())
          }

          "NINO page -> Country of Residence Yes No page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value

            navigator.nextPage(NationalInsuranceNumberPage, answers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know NINO page -> No -> Country of Residence Yes No page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }
        }

        "Country of Residence Pages" must {
          "Yes No page -> Yes -> Country of Residence In The Uk page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
          }

          "Yes No page -> No (with Nino) -> Mental Capacity Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Yes No page -> No (without Nino) -> Address Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad())
          }

          "In The Uk page -> Yes (with Nino) -> Mental Capacity Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "In The Uk page -> Yes (without Nino) -> Address Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad())
          }

          "In The Uk page -> No -> Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "page (with Nino) -> Mental Capacity Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(CountryOfResidencePage, "ES").success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "page (without Nino) -> Address Page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(CountryOfResidencePage, "ES").success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad())
          }
        }

        "Mental Capacity Yes No page" must {
          "-> Yes -> When Added page" in {
            val answers = baseAnswers
              .set(MentalCapacityYesNoPage, true).success.value

            navigator.nextPage(MentalCapacityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
          }

          "-> No -> When Added page" in {
            val answers = baseAnswers
              .set(MentalCapacityYesNoPage, false).success.value

            navigator.nextPage(MentalCapacityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
          }
        }
      }

      "non taxable" must {
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

        "Country of Nationality Pages" must {
          "Yes No page -> Yes -> Country of Nationality In The Uk page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(mode))
          }

          "Yes No page -> No -> Country of Residence Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "In The Uk page -> Yes -> Country of Residence Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityInTheUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "In The Uk page -> No -> Country of Nationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityInTheUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "page -> Country of Residence  Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, "ES").success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }
        }

        "Country of Residence Pages" must {
          "Yes No page -> Yes -> Country of Residence In The Uk page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
          }

          "Yes No page -> No -> Mental Capacity Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "In The Uk page -> Yes -> Mental Capacity Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "In The Uk page -> No -> Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "page -> Mental Capacity Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfResidencePage, "ES").success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.trustee.individual.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }
        }
      }
    }
  }
}
