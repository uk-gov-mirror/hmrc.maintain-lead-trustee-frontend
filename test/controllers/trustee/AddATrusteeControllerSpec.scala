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
import forms.YesNoFormProvider
import forms.trustee.AddATrusteeFormProvider
import models.{AddATrustee, AllTrustees, LeadTrustee, LeadTrusteeIndividual, Name, NationalInsuranceNumber, RemoveTrustee, RemoveTrusteeIndividual, TrustIdentification, TrusteeType, Trustees}
import org.joda.time.DateTime
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import viewmodels.addAnother.AddRow
import views.html.trustee.{AddATrusteeView, AddATrusteeYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddATrusteeControllerSpec extends SpecBase {

  lazy val getRoute : String = controllers.routes.AddATrusteeController.onPageLoad().url
  lazy val submitAnotherRoute : String = controllers.routes.AddATrusteeController.submitAnother().url
  lazy val submitYesNoRoute : String = controllers.routes.AddATrusteeController.submitOne().url

  val addTrusteeForm = new AddATrusteeFormProvider()()
  val yesNoForm = new YesNoFormProvider().withPrefix("addATrusteeYesNo")

  val trusteeRows = List(
    AddRow("First Last", typeLabel = "Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/0/remove"),
    AddRow("First Last", typeLabel = "Trustee Individual", "#", "/maintain-a-trust/trustees/trustee/1/remove")
  )

  private val leadTrusteeIndividual = Some(LeadTrusteeIndividual(
    name = Name(
      firstName = "First",
      middleName = None,
      lastName = "Last"
    ),
    dateOfBirth = LocalDate.parse("2010-10-10"),
    phoneNumber = "+446565657",
    email = None,
    identification = NationalInsuranceNumber("JP121212A"),
    address = None
  ))

  private val trustee = TrusteeType(Some(RemoveTrusteeIndividual(
    lineNo = Some("1"),
    bpMatchStatus = Some("01"),
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = Some(DateTime.parse("1983-9-24")),
    phoneNumber = None,
    identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
    entityStart = DateTime.parse("2019-2-28"))), None)
  val trustees = Trustees(List(trustee, trustee))


  class FakeService(data: Trustees, leadTrustee: Option[LeadTrustee] = leadTrusteeIndividual) extends TrustService {

    override def getLeadTrustee(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[LeadTrustee]] =
      Future.successful(leadTrustee)

    override def getAllTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllTrustees] =
      Future.successful(AllTrustees(leadTrustee, data.trustees))

    override def getTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Trustees] = Future.successful(data)

    override def removeTrustee(removeTrustee: RemoveTrustee, utr: String)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = Future.successful(HttpResponse(200))

  }

  "AddATrustee Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val fakeService = new FakeService(Trustees(Nil))

        val application = applicationBuilder(userAnswers = None).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no trustees" must {

      "return OK and the correct view for a GET" in {

        val fakeService = new FakeService(Trustees(Nil), None)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val fakeService = new FakeService(Trustees(Nil))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.trustee.individual.routes.NameController.onPageLoad(0).url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are trustees" must {

      "return OK and the correct view for a GET" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addTrusteeForm ,Nil, trusteeRows, isLeadTrusteeDefined = false, heading = "You have added 3 trustees")(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the 'add trustee' journey when the user elects to add a trustee" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.YesNow.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.trustee.routes.IndividualOrBusinessController.onPageLoad(2).url

        application.stop()
      }

      "redirect to the declaration when the user says they are done" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.NoComplete.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9788/maintain-a-trust/individual-declaration"

        application.stop()
      }

      "redirect to the declaration when the user says they want to add later (TBD)" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.YesLater.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9788/maintain-a-trust/individual-declaration"

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val fakeService = new FakeService(trustees)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            boundForm,
            Nil,
            trusteeRows,
            isLeadTrusteeDefined = false,
            heading = "You have added 3 trustees"
          )(fakeRequest, messages).toString

        application.stop()
      }

    }

  }
}
