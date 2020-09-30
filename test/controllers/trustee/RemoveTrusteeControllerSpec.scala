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

package controllers.trustee

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import forms.RemoveIndexFormProvider
import models.{Name, NationalInsuranceNumber, TrusteeIndividual, Trustees}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import views.html.RemoveIndexView

import scala.concurrent.Future

class RemoveTrusteeControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ScalaFutures {

  val messagesPrefix = "removeATrustee"

  lazy val formProvider = new RemoveIndexFormProvider()
  lazy val form = formProvider(messagesPrefix)

  lazy val formRoute = routes.RemoveTrusteeController.onSubmit(0)

  lazy val content : String = "First 1 Last 1"
  lazy val defaultContent : String = "the trustee"

  val mockConnector: TrustConnector = mock[TrustConnector]

  def trusteeInd(id: Int, provisional : Boolean) = TrusteeIndividual(
    name = Name(firstName = s"First $id", middleName = None, lastName = s"Last $id"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    phoneNumber = None,
    identification = Some(NationalInsuranceNumber("JS123456A")),
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = provisional
  )

  val expectedResult = trusteeInd(2, provisional = true)

  val trustees = List(
    trusteeInd(1, provisional = false),
    expectedResult,
    trusteeInd(3, provisional = true)
  )

  "RemoveTrustee Controller" when {

    "return OK and the correct view for a GET" in {

      val index = 0

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(Trustees(trustees)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TrustConnector].toInstance(mockConnector))
        .build()

      val request = FakeRequest(GET, routes.RemoveTrusteeController.onPageLoad(index).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveIndexView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(messagesPrefix, form, index, content, formRoute)(request, messages).toString

      application.stop()
    }

    "not removing the trustee" must {

      "redirect to the add to page when valid data is submitted" in {

        val index = 0

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        val request =
          FakeRequest(POST, routes.RemoveTrusteeController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url

        application.stop()
      }
    }

    "removing an existing trustee" must {

      "redirect to the next page when valid data is submitted" in {

        val index = 0

        when(mockConnector.getTrustees(any())(any(), any()))
          .thenReturn(Future.successful(Trustees(trustees)))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        val request =
          FakeRequest(POST, routes.RemoveTrusteeController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.trustee.routes.WhenRemovedController.onPageLoad(0).url

        application.stop()
      }
    }

    "removing a new trustee" must {

      "redirect to the add to page, removing the trustee" in {

        val index = 2

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        when(mockConnector.getTrustees(any())(any(), any()))
          .thenReturn(Future.successful(Trustees(trustees)))

        when(mockConnector.removeTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val request =
          FakeRequest(POST, routes.RemoveTrusteeController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val index = 0

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request =
        FakeRequest(POST, routes.RemoveTrusteeController.onSubmit(index).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(messagesPrefix, boundForm, index, content, formRoute)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val index = 0

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemoveTrusteeController.onPageLoad(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val index = 0

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemoveTrusteeController.onSubmit(index).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
