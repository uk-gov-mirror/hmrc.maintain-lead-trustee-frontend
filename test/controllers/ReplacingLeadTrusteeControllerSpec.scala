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

package controllers

import java.time.LocalDate

import base.SpecBase
import forms.TrusteeTypeFormProvider
import models.{AllTrustees, LeadTrustee, LeadTrusteeIndividual, Name, NationalInsuranceNumber, RemoveTrustee, Trustee, TrusteeIndividual, TrusteeOrganisation, Trustees}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import viewmodels.RadioOption
import views.html.ReplacingLeadTrusteeView

import scala.concurrent.{ExecutionContext, Future}

class ReplacingLeadTrusteeControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new TrusteeTypeFormProvider()
  val messageKeyPrefix: String = "replacingLeadTrustee"
  val form = formProvider.withPrefix(messageKeyPrefix)
  
  lazy val replacingLeadTrusteeRoute = routes.ReplacingLeadTrusteeController.onPageLoad().url

  private val leadTrusteeIndividual = LeadTrusteeIndividual(
    name = Name(
      firstName = "Lead First",
      middleName = None,
      lastName = "Last"
    ),
    dateOfBirth = LocalDate.parse("2010-10-10"),
    phoneNumber = "+446565657",
    email = None,
    identification = NationalInsuranceNumber("JP121212A"),
    address = None
  )

  private val trustee = TrusteeIndividual(
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    phoneNumber = None,
    identification = Some(NationalInsuranceNumber("JS123456A")),
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = true
  )

  val trustees = Trustees(List(trustee, trustee))

  val radioOptions = trustees.trustees.map {
    case ind: TrusteeIndividual => ind.name.displayName
    case org: TrusteeOrganisation => org.name
  }.zipWithIndex.map(
    x => RadioOption(s"$messageKeyPrefix.${x._2}", s"${x._2}", x._1)
  )

  class FakeService(data: Trustees, leadTrustee: Option[LeadTrustee] = Some(leadTrusteeIndividual)) extends TrustService {

    override def getLeadTrustee(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[LeadTrustee]] =
      Future.successful(leadTrustee)

    override def getAllTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllTrustees] =
      Future.successful(AllTrustees(leadTrustee, data.trustees))

    override def getTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Trustees] = Future.successful(data)

    override def getTrustee(utr: String, index: Int)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Trustee] =
      Future.successful(trustee)

    override def removeTrustee(utr: String, trustee: RemoveTrustee)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = Future.successful(HttpResponse(200))

  }

  "ReplacingLeadTrustee controller" must {

    "return OK and the correct view for a GET" in {

      val fakeService = new FakeService(trustees)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind(classOf[TrustService]).toInstance(fakeService))
        .build()

      val request = FakeRequest(GET, replacingLeadTrusteeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ReplacingLeadTrusteeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, leadTrusteeIndividual.name.displayName, radioOptions)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val fakeService = new FakeService(trustees)

      val mockPlaybackRepository = mock[PlaybackRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, replacingLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", "0"))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind(classOf[TrustService]).toInstance(fakeService))
        .build()

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.leadtrustee.individual.routes.NeedToAnswerQuestionsController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val fakeService = new FakeService(trustees)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind(classOf[TrustService]).toInstance(fakeService))
        .build()

      val request =
        FakeRequest(POST, replacingLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ReplacingLeadTrusteeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, leadTrusteeIndividual.name.displayName, radioOptions)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, replacingLeadTrusteeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, replacingLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", "add-new"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
