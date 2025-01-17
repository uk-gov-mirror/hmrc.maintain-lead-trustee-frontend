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

package controllers.trustee

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import connectors.TrustConnector
import forms.DateRemovedFromTrustFormProvider
import models.{Name, NationalInsuranceNumber, TrusteeIndividual, Trustees}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{TrustService, TrustServiceImpl}
import uk.gov.hmrc.http.HttpResponse
import views.html.trustee.WhenRemovedView

import scala.concurrent.Future

class WhenRemovedControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new DateRemovedFromTrustFormProvider()

  private def form = formProvider.withPrefixAndEntityStartDate("trustee.whenRemoved", LocalDate.now())

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  val index = 0

  lazy val name : String = "First 1 Last 1"

  val mockConnector = mock[TrustConnector]

  val fakeService = new TrustServiceImpl(mockConnector)

  lazy val dateRemovedFromTrustRoute = routes.WhenRemovedController.onPageLoad(index).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, dateRemovedFromTrustRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, dateRemovedFromTrustRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  def trusteeInd(id: Int) = TrusteeIndividual(
    name = Name(firstName = s"First $id", middleName = None, lastName = s"Last $id"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    phoneNumber = None,
    identification = Some(NationalInsuranceNumber("JS123456A")),
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = true
  )

  val trustees = List(trusteeInd(1), trusteeInd(2), trusteeInd(3))

  "WhenRemoved Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(Trustees(trustees)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[WhenRemovedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, index, name)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockConnector.removeTrustee(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(200, "")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[TrustService].toInstance(fakeService)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request =
        FakeRequest(POST, dateRemovedFromTrustRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhenRemovedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
