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

package controllers.leadtrustee

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.IndividualOrBusiness.{Business, Individual}
import models.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Name, UkAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection
import views.html.leadtrustee.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar {
  def onwardRoute = Call("GET", "/foo")

  val trusteeName = Name("FirstName", None, "LastName")
  val trusteeAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")

  lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoadUpdated().url
  lazy val sendDetailsRoute = routes.CheckDetailsController.onSubmit().url

  val submittableUserAnswers = emptyUserAnswers
    .set(IndividualOrBusinessPage, Individual).success.value
    .set(NamePage, trusteeName).success.value
    .set(DateOfBirthPage, LocalDate.of(1996, 2, 3)).success.value
    .set(UkCitizenPage, true).success.value
    .set(NationalInsuranceNumberPage, "nino").success.value
    .set(LiveInTheUkYesNoPage, true).success.value
    .set(UkAddressPage, UkAddress("line1", "line2", None, None, "postcode")).success.value
    .set(EmailAddressYesNoPage, false).success.value
    .set(TelephoneNumberPage, "tel").success.value


  val submittableUserAnswersOrg = emptyUserAnswers
    .set(IndividualOrBusinessPage, Business).success.value
    .set(pages.leadtrustee.organisation.NamePage, "Lead Org").success.value
    .set(pages.leadtrustee.organisation.BasedInTheUkYesNoPage, true).success.value
    .set(pages.leadtrustee.organisation.UtrPage, "1234567892").success.value
    .set(pages.leadtrustee.organisation.UkAddressPage, UkAddress("line1", "line2", None, None, "postcode")).success.value
    .set(pages.leadtrustee.organisation.EmailAddressYesNoPage, false).success.value
    .set(pages.leadtrustee.organisation.TelephoneNumberPage, "0191222222").success.value

  "CheckDetails Controller" when {

    "trust has a lead trustee individual" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Individual).success.value
          .set(NamePage, trusteeName).success.value
          .set(UkAddressPage, trusteeAddress).success.value

        val bound = new AnswerRowConverter().bind(userAnswers, trusteeName.displayName, mock[CountryOptions])

        val answerSection = AnswerSection(None, Seq(
          bound.nameQuestion(NamePage, "leadtrustee.individual.name", controllers.leadtrustee.individual.routes.NameController.onPageLoad().url),
          bound.addressQuestion(UkAddressPage, "leadtrustee.individual.ukAddress", controllers.leadtrustee.individual.routes.UkAddressController.onPageLoad().url)
        ).flatten
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckDetailsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(answerSection)(fakeRequest, messages).toString
      }

      "redirect to the the next page" in {

        val mockTrustConnector = mock[TrustConnector]

        val application =
          applicationBuilder(userAnswers = Some(submittableUserAnswers),
            affinityGroup = Agent)
            .overrides(
              bind[TrustConnector].toInstance(mockTrustConnector)
            ).build()

        when(mockTrustConnector.amendLeadTrustee(any(), any[LeadTrusteeIndividual]())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

        val request = FakeRequest(POST, sendDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url
      }

    }

    "trust has a lead trustee organisation" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Business).success.value
          .set(pages.leadtrustee.organisation.NamePage, "Lead Org").success.value
          .set(pages.leadtrustee.organisation.UkAddressPage, trusteeAddress).success.value

        val bound = new AnswerRowConverter().bind(userAnswers, "Lead Org", mock[CountryOptions])

        val answerSection = AnswerSection(None, Seq(
          bound.stringQuestion(pages.leadtrustee.organisation.NamePage, "leadtrustee.organisation.name", controllers.leadtrustee.organisation.routes.NameController.onPageLoad().url),
          bound.addressQuestion(pages.leadtrustee.organisation.UkAddressPage, "leadtrustee.organisation.ukAddress", controllers.leadtrustee.organisation.routes.UkAddressController.onPageLoad().url)
        ).flatten
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckDetailsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(answerSection)(fakeRequest, messages).toString
      }

      "redirect to the the next page" in {

        val mockTrustConnector = mock[TrustConnector]

        val application =
          applicationBuilder(userAnswers = Some(submittableUserAnswersOrg),
            affinityGroup = Agent)
            .overrides(
              bind[TrustConnector].toInstance(mockTrustConnector)
            ).build()

        when(mockTrustConnector.amendLeadTrustee(any(), any[LeadTrusteeOrganisation]())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

        val request = FakeRequest(POST, sendDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url
      }
    }

  }
}
