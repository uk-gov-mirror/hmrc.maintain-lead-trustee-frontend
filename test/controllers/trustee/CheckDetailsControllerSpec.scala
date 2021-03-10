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

import base.SpecBase
import connectors.TrustConnector
import mapping.mappers.{TrusteeIndividualMapper, TrusteeOrganisationMapper}
import models.IndividualOrBusiness.{Business, Individual}
import models.{Name, TrusteeIndividual, TrusteeOrganisation}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.trustee.{IndividualOrBusinessPage, individual => ind, organisation => org}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.checkYourAnswers.{TrusteeIndividualPrintHelper, TrusteeOrganisationPrintHelper}
import viewmodels.AnswerSection
import views.html.trustee.CheckDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val indName: Name = Name("Joe", None, "Bloggs")
  private val orgName: String = "Name"
  private val date: LocalDate = LocalDate.parse("1996-02-03")

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = controllers.routes.AddATrusteeController.onPageLoad().url

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET" when {

      val answerSection: AnswerSection = AnswerSection(None, Nil)

      "individual" in {

        val printHelper: TrusteeIndividualPrintHelper = mock[TrusteeIndividualPrintHelper]

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Individual).success.value
          .set(ind.NamePage, indName).success.value

        when(printHelper.print(any(), any())(any())).thenReturn(answerSection)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[TrusteeIndividualPrintHelper].toInstance(printHelper))
          .build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckDetailsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(answerSection)(request, messages).toString

        verify(printHelper).print(eqTo(userAnswers), eqTo(indName.displayName))(any())
      }

      "business" in {

        val printHelper: TrusteeOrganisationPrintHelper = mock[TrusteeOrganisationPrintHelper]

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Business).success.value
          .set(org.NamePage, orgName).success.value

        when(printHelper.print(any(), any())(any())).thenReturn(answerSection)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[TrusteeOrganisationPrintHelper].toInstance(printHelper))
          .build()

        val request = FakeRequest(GET, checkDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckDetailsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(answerSection)(request, messages).toString

        verify(printHelper).print(eqTo(userAnswers), eqTo(orgName))(any())
      }
    }

    "return InternalServerError if individual or business unanswered for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "redirect to the 'add a trustee' page when submitted" when {

      val connector: TrustConnector = mock[TrustConnector]

      "individual" in {

        val mapper: TrusteeIndividualMapper = mock[TrusteeIndividualMapper]

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Individual).success.value
          .set(ind.NamePage, indName).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(connector))
          .overrides(bind[TrusteeIndividualMapper].toInstance(mapper))
          .build()

        when(connector.addTrustee(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

        when(mapper.map(any(), any()))
          .thenReturn(Some(TrusteeIndividual(indName, None, None, None, None, None, None, None, date, provisional = true)))

        val request = FakeRequest(POST, submitDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute

        application.stop()

        verify(mapper).map(userAnswers, adding = true)
      }

      "business" in {

        val mapper: TrusteeOrganisationMapper = mock[TrusteeOrganisationMapper]

        val userAnswers = emptyUserAnswers
          .set(IndividualOrBusinessPage, Business).success.value
          .set(org.NamePage, orgName).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(connector))
          .overrides(bind[TrusteeOrganisationMapper].toInstance(mapper))
          .build()

        when(connector.addTrustee(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

        when(mapper.map(any()))
          .thenReturn(Some(TrusteeOrganisation(orgName, None, None, None, date, provisional = true)))

        val request = FakeRequest(POST, submitDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute

        application.stop()

        verify(mapper).map(userAnswers)
      }
    }

    "return InternalServerError for a POST" when {

      "mapper fails" when {

        "individual" in {

          val mapper: TrusteeIndividualMapper = mock[TrusteeIndividualMapper]

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Individual).success.value
            .set(ind.NamePage, indName).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(bind[TrusteeIndividualMapper].toInstance(mapper))
            .build()

          when(mapper.map(any(), any())).thenReturn(None)

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          verify(mapper).map(userAnswers, adding = true)
        }

        "business" in {

          val mapper: TrusteeOrganisationMapper = mock[TrusteeOrganisationMapper]

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Business).success.value
            .set(org.NamePage, orgName).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(bind[TrusteeOrganisationMapper].toInstance(mapper))
            .build()

          when(mapper.map(any())).thenReturn(None)

          val request = FakeRequest(POST, submitDetailsRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()

          verify(mapper).map(userAnswers)
        }
      }

      "individual or business unanswered" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent).build()

        val request = FakeRequest(POST, submitDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
