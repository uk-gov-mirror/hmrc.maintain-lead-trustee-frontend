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
import controllers.trustee.individual.add.routes._
import controllers.trustee.individual.amend.routes._
import controllers.trustee.individual.routes._
import models.{CheckMode, NormalMode}
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.individual._
import pages.trustee.individual.add._
import pages.trustee.individual.amend._

class IndividualTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator
  val index = 0
  val nino = "nino"

  "Individual trustee navigator" when {
    
    "4mld" when {

      "adding" must {

        val mode = NormalMode
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(mode))
        }

        "Do you know date of birth page" when {
          val page = DateOfBirthYesNoPage

          "-> Yes -> Date of birth page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(DateOfBirthController.onPageLoad(mode))
          }

          "-> No -> Do you know NINO page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }
        }

        "Date of birth page -> Do you know NINO page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
        }

        "Do you know NINO page" when {
          val page = NationalInsuranceNumberYesNoPage

          "-> Yes -> NINO page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberController.onPageLoad(mode))
          }

          "-> No -> Do you know address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(AddressYesNoController.onPageLoad(mode))
          }
        }

        "NINO page -> When added as trustee page" in {
          val answers = baseAnswers
            .set(NationalInsuranceNumberPage, nino).success.value

          navigator.nextPage(NationalInsuranceNumberPage, mode, answers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "Do you know address page" when {
          val page = AddressYesNoPage

          "-> Yes -> Is address in UK page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(LiveInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> When added as trustee page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
          }
        }

        "Is address in UK page" when {
          val page = LiveInTheUkYesNoPage

          "-> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(UkAddressController.onPageLoad(mode))
          }

          "-> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NonUkAddressController.onPageLoad(mode))
          }
        }

        "UK address page -> Do you know passport details page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad())
        }

        "Non-UK address page -> Do you know passport details page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad())
        }

        "Do you know passport details page" when {
          val page = PassportDetailsYesNoPage

          "-> Yes Passport details page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(PassportDetailsController.onPageLoad())
          }

          "-> No -> Do you know ID card details page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(IdCardDetailsYesNoController.onPageLoad())
          }
        }

        "Passport details page -> When added as trustee page" in {
          navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "Do you know ID card details page" when {
          val page = IdCardDetailsYesNoPage

          "-> Yes -> ID card details page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(IdCardDetailsController.onPageLoad())
          }

          "-> No -> When added as trustee page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
          }
        }

        "ID card details page -> When added as trustee page" in {
          navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }
      }

      "amending" must {

        val mode = CheckMode
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)
          .set(IndexPage, index).success.value

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(mode))
        }

        "Do you know date of birth page" when {
          val page = DateOfBirthYesNoPage

          "-> Yes -> Date of birth page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(DateOfBirthController.onPageLoad(mode))
          }

          "-> No -> Do you know NINO page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }
        }

        "Date of birth page -> Do you know NINO page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
        }

        "Do you know NINO page" when {
          val page = NationalInsuranceNumberYesNoPage

          "-> Yes -> NINO page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberController.onPageLoad(mode))
          }

          "-> No -> Do you know address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(AddressYesNoController.onPageLoad(mode))
          }
        }

        "NINO page -> Check details page" in {
          val answers = baseAnswers
            .set(NationalInsuranceNumberPage, nino).success.value

          navigator.nextPage(NationalInsuranceNumberPage, mode, answers)
            .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
        }

        "Do you know address page" when {
          val page = AddressYesNoPage

          "-> Yes -> Is address in UK page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(LiveInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> Check details page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
          }
        }

        "Is address in UK page" when {
          val page = LiveInTheUkYesNoPage

          "-> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(UkAddressController.onPageLoad(mode))
          }

          "-> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NonUkAddressController.onPageLoad(mode))
          }
        }

        "UK address page -> Do you know passport or ID card details page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(PassportOrIdCardDetailsYesNoController.onPageLoad())
        }

        "Non-UK address page -> Do you know passport or ID card details page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(PassportOrIdCardDetailsYesNoController.onPageLoad())
        }

        "Do you know passport or ID card details page" when {
          val page = PassportOrIdCardDetailsYesNoPage

          "-> Yes -> Passport or ID card details page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(PassportOrIdCardDetailsController.onPageLoad())
          }

          "-> No -> Check details page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
          }
        }

        "Passport or ID card details page -> Check details page" in {
          navigator.nextPage(PassportOrIdCardDetailsPage, mode, baseAnswers)
            .mustBe(controllers.trustee.amend.routes.CheckDetailsController.onPageLoadUpdated(index))
        }
      }
    }

    "5mld" must {
      
      "taxable" must {

        val mode = NormalMode
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(mode))
        }

        "Do you know date of birth page" when {
          val page = DateOfBirthYesNoPage

          "-> Yes -> Date of birth page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(DateOfBirthController.onPageLoad(mode))
          }

          "-> No -> Do you know country of nationality page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode))
          }
        }

        "Date of birth page -> Do you know country of nationality page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode))
        }

        "Do you know country of nationality page" when {
          val page = CountryOfNationalityYesNoPage

          "-> Yes -> UK country of nationality yes/no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> Do you know NINO page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }
        }

        "UK country of nationality yes/no page" when {
          val page = CountryOfNationalityInTheUkYesNoPage

          "-> Yes -> Do you know NINO page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "-> No -> Country of nationality page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityController.onPageLoad(mode))
          }
        }

        "Country of nationality page -> Do you know NINO page" in {
          navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
            .mustBe(NationalInsuranceNumberYesNoController.onPageLoad(mode))
        }

        "Do you know NINO page" when {
          val page = NationalInsuranceNumberYesNoPage

          "-> Yes -> NINO page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NationalInsuranceNumberController.onPageLoad(mode))
          }

          "-> No -> Do you know county of residence page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceYesNoController.onPageLoad(mode))
          }
        }

        "NINO page -> Do you know county of residence page" in {
          navigator.nextPage(NationalInsuranceNumberPage, mode, baseAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(mode))
        }

        "Do you know country of residence page" when {
          val page = CountryOfResidenceYesNoPage

          "-> Yes -> Is country of residence in UK page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No" when {
            "NINO known" must {
              "-> Mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
              }
            }

            "NINO not known" must {
              "-> Do you know address page" in {
                val answers = baseAnswers
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(AddressYesNoController.onPageLoad(mode))
              }
            }
          }
        }

        "Is country of residence in UK page" when {
          val page = CountryOfResidenceInTheUkYesNoPage

          "-> Yes" when {
            "NINO known" must {
              "-> Mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
              }
            }

            "NINO not known" must {
              "-> Do you know address page" in {
                val answers = baseAnswers
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(AddressYesNoController.onPageLoad(mode))
              }
            }
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceController.onPageLoad(mode))
          }
        }

        "Country of residence page" when {
          val page = CountryOfResidencePage

          "NINO known" must {
            "-> Mental capacity page" in {
              val answers = baseAnswers
                .set(NationalInsuranceNumberPage, nino).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "NINO not known" must {
            "-> Do you know address page" in {
              navigator.nextPage(page, mode, baseAnswers)
                .mustBe(AddressYesNoController.onPageLoad(mode))
            }
          }
        }

        "Do you know address page" when {
          val page = AddressYesNoPage

          "-> Yes -> Is address in UK page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(LiveInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> Mental capacity page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
          }
        }

        "Is address in UK page" when {
          val page = LiveInTheUkYesNoPage

          "-> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(UkAddressController.onPageLoad(mode))
          }

          "-> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(NonUkAddressController.onPageLoad(mode))
          }
        }

        "UK address page -> Do you know passport details page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad())
        }

        "Non-UK address page -> Do you know passport details page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad())
        }

        "Do you know passport details page" when {
          val page = PassportDetailsYesNoPage

          "-> Yes Passport details page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(PassportDetailsController.onPageLoad())
          }

          "-> No -> Do you know ID card details page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(IdCardDetailsYesNoController.onPageLoad())
          }
        }

        "Passport details page -> Mental capacity page" in {
          navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
        }

        "Do you know ID card details page" when {
          val page = IdCardDetailsYesNoPage

          "-> Yes -> ID card details page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(IdCardDetailsController.onPageLoad())
          }

          "-> No -> Mental capacity page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
          }
        }

        "ID card details page -> Mental capacity page" in {
          navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
        }

        "Mental Capacity page -> When added page" in {
          navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }
      }

      "non taxable" must {

        val mode = NormalMode
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(mode))
        }

        "Do you know date of birth page" when {
          val page = DateOfBirthYesNoPage

          "-> Yes -> Date of birth page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(DateOfBirthController.onPageLoad(mode))
          }

          "-> No -> Do you know country of nationality page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode))
          }
        }

        "Date of birth page -> Do you know country of nationality page" in {
          navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode))
        }

        "Do you know country of nationality page" when {
          val page = CountryOfNationalityYesNoPage

          "-> Yes -> UK country of nationality yes/no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> Do you know country of residency page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceYesNoController.onPageLoad(mode))
          }
        }

        "UK country of nationality yes/no page" when {
          val page = CountryOfNationalityInTheUkYesNoPage

          "-> Yes -> Do you know country of residency page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "-> No -> Country of nationality page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfNationalityController.onPageLoad(mode))
          }
        }

        "Country of nationality page -> Do you know country of residency page" in {
          navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(mode))
        }

        "Do you know country of residence page" when {
          val page = CountryOfResidenceYesNoPage

          "-> Yes -> Is country of residence in UK page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
          }

          "-> No -> Mental capacity page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
          }
        }

        "Is country of residence in UK page" when {
          val page = CountryOfResidenceInTheUkYesNoPage

          "-> Yes -> Mental capacity page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, mode, answers)
              .mustBe(CountryOfResidenceController.onPageLoad(mode))
          }
        }

        "Country of residence page -> Mental capacity page" in {
          navigator.nextPage(CountryOfResidencePage, mode, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(mode))
        }

        "Mental Capacity page -> When added page" in {
          navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }
      }
    }
  }
}
