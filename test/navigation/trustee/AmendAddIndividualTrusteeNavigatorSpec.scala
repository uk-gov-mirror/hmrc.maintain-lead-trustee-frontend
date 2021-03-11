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
import models.CheckMode
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.amend.individual._
import pages.trustee.individual.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage, CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage, MentalCapacityYesNoPage}
import controllers.trustee.amend.individual.{routes => rts}

class AmendAddIndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  val index = 0

  "Amend Individual trustee navigator" when {
    val mode = CheckMode
    
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

      "NINO page -> Check your answers page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value
          .set(IndexPage, index).success.value

        navigator.nextPage(NationalInsuranceNumberPage, answers)
          .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
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
          .mustBe(rts.UkAddressController.onPageLoad())
      }

      "UK address page -> Do you know passport or ID card details page" in {
        navigator.nextPage(UkAddressPage, emptyUserAnswers)
          .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
      }

      "Is address in UK page -> No -> Non-UK address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, answers)
          .mustBe(rts.NonUkAddressController.onPageLoad())
      }

      "Non-UK address page -> Do you know passport or ID card details page" in {
        navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
          .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
      }

      "Do you know passport or ID card details page -> Yes -> Passport or ID card details page" in {
        val answers = emptyUserAnswers
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value

        navigator.nextPage(PassportOrIdCardDetailsYesNoPage, answers)
          .mustBe(rts.PassportOrIdCardDetailsController.onPageLoad())
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

    "5mld" must {
     
      "taxable" must {
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

        "Do you know date of birth page -> No -> Country of Nationality Yes No page" in {
          val answers = baseAnswers
            .set(DateOfBirthYesNoPage, false).success.value

          navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
            .mustBe(controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(CheckMode))
        }

        "Date of birth page -> Country of Nationality page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(controllers.trustee.individual.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
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
              .set(IndexPage, index).success.value

            navigator.nextPage(NationalInsuranceNumberPage, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know NINO page -> No -> Country of Residence Yes No page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(IndexPage, index).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
              .mustBe(controllers.trustee.individual.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }
        }

        "Mental Capacity Yes No page" must {
          "Yes -> Check Details page" in {
            val answers = baseAnswers
              .set(IndexPage, index).success.value
              .set(MentalCapacityYesNoPage, true).success.value

            navigator.nextPage(MentalCapacityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
          }

          "No -> Check Details page" in {
            val answers = baseAnswers
              .set(IndexPage, index).success.value
              .set(MentalCapacityYesNoPage, false).success.value

            navigator.nextPage(MentalCapacityYesNoPage, mode, answers)
              .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
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
