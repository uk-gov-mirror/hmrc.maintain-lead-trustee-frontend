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

package controllers.leadtrustee.individual

import base.SpecBase
import forms.NationalInsuranceNumberFormProvider
import models.{IssueBuildingPayloadResponse, LockedMatchResponse, Name, ServiceNotIn5mldModeResponse, ServiceUnavailableErrorResponse, SuccessfulMatchResponse, UnsuccessfulMatchResponse}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.leadtrustee.individual.{NamePage, NationalInsuranceNumberPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsIndividualCheckService
import views.html.leadtrustee.individual.NationalInsuranceNumberView

import scala.concurrent.Future

class NationalInsuranceNumberControllerSpec extends SpecBase with MockitoSugar {

  val form = new NationalInsuranceNumberFormProvider().withPrefix("leadtrustee.individual.nationalInsuranceNumber")

  val name = Name("Lead", None, "Trustee")

  val nino = "JH123456C"

  lazy val nationalInsuranceNumberRoute = routes.NationalInsuranceNumberController.onPageLoad().url

  override val emptyUserAnswers = super.emptyUserAnswers
    .set(NamePage, name).success.value

  "NationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, nationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, name.displayName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NationalInsuranceNumberPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, nationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "in 4mld mode" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(ServiceNotIn5mldModeResponse))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator()),
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "in 5mld mode and SuccessfulMatchResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(SuccessfulMatchResponse))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator()),
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

    }

    "redirect to matching failed page" when {

      "UnsuccessfulMatchResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(UnsuccessfulMatchResponse))

        when(mockService.failedAttempts(any())(any(), any()))
          .thenReturn(Future.successful(1))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.MatchingFailedController.onPageLoad().url

        application.stop()
      }
    }

    "redirect to matching locked page" when {
      "LockedMatchResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(LockedMatchResponse))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.MatchingLockedController.onPageLoad().url

        application.stop()
      }
    }

    "return INTERNAL_SERVER_ERROR" when {

      "IssueBuildingPayloadResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(IssueBuildingPayloadResponse))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        application.stop()
      }

      "ServiceUnavailableErrorResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any())(any(), any()))
          .thenReturn(Future.successful(ServiceUnavailableErrorResponse))

        val userAnswers = emptyUserAnswers
          .set(NationalInsuranceNumberPage, nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", nino))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, nationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
