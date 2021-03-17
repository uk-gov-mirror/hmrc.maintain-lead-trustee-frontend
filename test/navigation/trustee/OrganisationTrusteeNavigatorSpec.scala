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
import models.{CheckMode, NormalMode}
import navigation.Navigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.trustee.WhenAddedPage
import pages.trustee.organisation._
import pages.trustee.organisation.amend.IndexPage

class OrganisationTrusteeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new Navigator

  "Organisation trustee navigator" when {

    "4mld" must {

      "add journey navigation" must {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false)
        val mode = NormalMode

        "Name page -> Do you know UTR page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode))
        }

        "Do you know UTR page -> Yes -> UTR page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, true).success.value

          navigator.nextPage(UtrYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.UtrController.onPageLoad(mode))
        }

        "UTR page -> When added as trustee page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, true).success.value

          navigator.nextPage(UtrPage, mode, answers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "Do you know UTR page -> No -> Do you know address page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, false).success.value

          navigator.nextPage(UtrYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
        }

        "Do you know address page -> Yes -> Is address in UK page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage, true).success.value

          navigator.nextPage(AddressYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode))
        }

        "Do you know address page -> No -> When added as trustee page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage, false).success.value

          navigator.nextPage(AddressYesNoPage, mode, answers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "Is address in UK page -> Yes -> UK address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage, true).success.value

          navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode))
        }

        "Is address in UK page -> No -> Non-UK address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage, false).success.value

          navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode))
        }

        "UK address page -> When added as trustee page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "Non-UK address page -> When added as trustee page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
        }

        "wWhen Added page -> Check details page" in {
          navigator.nextPage(WhenAddedPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.CheckDetailsController.onPageLoad())
        }

      }

      "amend journey navigation" must {

        val mode = CheckMode
        val index = 0
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false)
          .set(IndexPage, index).success.value

        "Name page -> Do you know UTR page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(controllers.trustee.organisation.routes.UtrYesNoController.onPageLoad(mode))
        }

        "Do you know UTR page -> Yes -> UTR page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, true).success.value

          navigator.nextPage(UtrYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.UtrController.onPageLoad(mode))
        }

        "UTR page -> Check details page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, true).success.value

          navigator.nextPage(UtrPage, mode, answers)
            .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
        }

        "Do you know UTR page -> No -> Do you know address page" in {
          val answers = baseAnswers
            .set(UtrYesNoPage, false).success.value

          navigator.nextPage(UtrYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
        }

        "Do you know address page -> Yes -> Is address in UK page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage, true).success.value

          navigator.nextPage(AddressYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.AddressInTheUkYesNoController.onPageLoad(mode))
        }

        "Do you know address page -> No -> Check details page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage, false).success.value

          navigator.nextPage(AddressYesNoPage, mode, answers)
            .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
        }

        "Is address in UK page -> Yes -> UK address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage, true).success.value

          navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.UkAddressController.onPageLoad(mode))
        }

        "Is address in UK page -> No -> Non-UK address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage, false).success.value

          navigator.nextPage(AddressInTheUkYesNoPage, mode, answers)
            .mustBe(controllers.trustee.organisation.routes.NonUkAddressController.onPageLoad(mode))
        }

        "UK address page -> Check details page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
        }

        "Non-UK address page -> Check details page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
        }


      }


    }

    "5mld" must {

      "taxable" must {

        "add journey navigation" must {

          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)
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
            val answers = baseAnswers
              .set(UtrYesNoPage, true).success.value

            navigator.nextPage(UtrPage, mode, answers)
              .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Country of Residence Pages" must {

            "Yes No page -> Yes -> Country of Residence In The Uk page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
            }

            "Yes No page -> No (with UTR) -> When Added Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "Yes No page -> No (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }

            "In The Uk page -> Yes (with UTR) -> When Added Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "In The Uk page -> Yes (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }

            "In The Uk page -> No -> Country of Residence page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode))
            }

            "page (with UTR) -> When Added Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "page (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }
          }

          "Address Pages" must {
            "Do you know address page -> No -> When Added page" in {
              val answers = baseAnswers
                .set(AddressYesNoPage, false).success.value

              navigator.nextPage(AddressYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "UK address page -> When Added page" in {
              navigator.nextPage(UkAddressPage, mode, baseAnswers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "Non-UK address page -> When Added page" in {
              navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }
          }


        }


        "amend journey navigation" must {

          val index = 0
          val mode = CheckMode
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)
            .set(IndexPage, index).success.value

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
            val answers = baseAnswers
              .set(UtrYesNoPage, true).success.value

            navigator.nextPage(UtrPage, mode, answers)
              .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Country of Residence Pages" must {

            "Yes No page -> Yes -> Country of Residence In The Uk page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
            }

            "Yes No page -> No (with UTR) -> check details Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "Yes No page -> No (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }

            "In The Uk page -> Yes (with UTR) -> check details Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "In The Uk page -> Yes (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }

            "In The Uk page -> No -> Country of Residence page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode))
            }

            "page (with UTR) -> Check details Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, true).success.value
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "page (without UTR) -> Address Page" in {
              val answers = baseAnswers
                .set(UtrYesNoPage, false).success.value
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.AddressYesNoController.onPageLoad(mode))
            }
          }

          "Address Pages" must {
            "Do you know address page -> No -> check details page" in {
              val answers = baseAnswers
                .set(AddressYesNoPage, false).success.value

              navigator.nextPage(AddressYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "UK address page -> check details page" in {
              navigator.nextPage(UkAddressPage, mode, baseAnswers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "Non-UK address page -> check details page" in {
              navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }
          }


        }


      }





      "non-taxable" must {

        "add journey navigation" must {

          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)
          val mode = NormalMode

          "Name page -> Country of Residence Yes No page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Country of Residence Pages" must {

            "Yes No page -> Yes -> Country of Residence In The Uk page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
            }

            "Yes No page -> No -> When Added Page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "In The Uk page -> Yes -> When Added Page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

            "In The Uk page -> No -> Country of Residence page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode))
            }

            "page -> When Added Page" in {
              val answers = baseAnswers
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.routes.WhenAddedController.onPageLoad())
            }

          }

        }


        "amend journey navigation" must {

          val index = 0
          val mode = CheckMode
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)
            .set(IndexPage, index).success.value

          "Name page -> Country of Residence Yes No page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Country of Residence Pages" must {

            "Yes No page -> Yes -> Country of Residence In The Uk page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(mode))
            }

            "Yes No page -> No -> check details Page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "In The Uk page -> Yes -> check details Page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

            "In The Uk page -> No -> Country of Residence page" in {
              val answers = baseAnswers
                .set(CountryOfResidenceYesNoPage, true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, mode, answers)
                .mustBe(controllers.trustee.organisation.routes.CountryOfResidenceController.onPageLoad(mode))
            }

            "page -> check details Page" in {
              val answers = baseAnswers
                .set(CountryOfResidencePage, "ES").success.value

              navigator.nextPage(CountryOfResidencePage, mode, answers)
                .mustBe(controllers.trustee.routes.CheckUpdatedDetailsController.onPageLoadUpdated(index))
            }

          }


        }


      }

    }

  }
}
