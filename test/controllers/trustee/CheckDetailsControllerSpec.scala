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
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import base.SpecBase
import connectors.TrustConnector
import models.IndividualOrBusiness.Individual
import models.requests.{AgentUser, DataRequest}
import models.{Name, UkAddress}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import pages.trustee.individual.{NamePage, UkAddressPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import uk.gov.hmrc.http.HttpResponse
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection
import views.html.trustee.CheckDetailsView
import views.html.trustee.individual.UkAddressView

import scala.concurrent.Future
import scala.concurrent.duration.Duration

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar {
  def onwardRoute = Call("GET", "/foo")
  
  val index = 0

  val trusteeName = Name("FirstName", None, "LastName")
  val trusteeAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")

  lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad(index).url
  lazy val sendDetailsRoute = routes.CheckDetailsController.onSubmit(index).url

  "UkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage(index), Individual).success.value
        .set(NamePage(index), trusteeName).success.value
        .set(UkAddressPage(index), trusteeAddress).success.value

      val bound = new AnswerRowConverter().bind(userAnswers, trusteeName.displayName, mock[CountryOptions])

      val answerSection = AnswerSection(None, Seq(
        bound.nameQuestion(NamePage(index), "trustee.individual.name", controllers.trustee.individual.routes.NameController.onPageLoad(index).url),
        bound.addressQuestion(UkAddressPage(index), "trustee.individual.ukAddress", controllers.trustee.individual.routes.UkAddressController.onPageLoad(index).url)
      ).flatten
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection, index)(fakeRequest, messages).toString
    }
  }

    "redirect to the Agent declaration journey when affinity group is agent" in {

      val mockTrustConnector = mock[TrustConnector]

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName).success.value
        .set(WhenAddedPage(index), LocalDate.now).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers),
          affinityGroup = Agent)
          .overrides(
            bind[TrustConnector].toInstance(mockTrustConnector)
          ).build()

      when(mockTrustConnector.addTrusteeIndividual(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

      val request = FakeRequest(POST, sendDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value must include("/is-agency-address-in-uk")
    }

  "redirect to the Ind/Org declaration journey when affinity group is not an agent" in {

    val mockTrustConnector = mock[TrustConnector]

    val userAnswers = emptyUserAnswers
      .set(NamePage(index), trusteeName).success.value
      .set(WhenAddedPage(index), LocalDate.now).success.value

    val application =
      applicationBuilder(userAnswers = Some(userAnswers),
        affinityGroup = Organisation)
        .overrides(
          bind[TrustConnector].toInstance(mockTrustConnector)
        ).build()

    when(mockTrustConnector.addTrusteeIndividual(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

    val request = FakeRequest(POST, sendDetailsRoute)

    val result = route(application, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value must include("/individual-declaration")
  }

}
