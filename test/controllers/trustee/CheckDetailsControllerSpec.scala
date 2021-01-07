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

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.IndividualOrBusiness.Individual
import models.{Name, UkAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.trustee.individual.{NamePage, UkAddressPage}
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.countryOptions.CountryOptions
import utils.print.{AnswerRowConverter, CheckAnswersFormatters}
import viewmodels.AnswerSection
import views.html.trustee.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val trusteeName: Name = Name("FirstName", None, "LastName")
  private val trusteeAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = controllers.routes.AddATrusteeController.onPageLoad().url

  private val checkAnswersFormatters: CheckAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Individual).success.value
        .set(NamePage, trusteeName).success.value
        .set(UkAddressPage, trusteeAddress).success.value

      val bound = new AnswerRowConverter(checkAnswersFormatters).bind(userAnswers, trusteeName.displayName, mock[CountryOptions])

      val answerSection = AnswerSection(None, Seq(
        bound.nameQuestion(NamePage, "trustee.individual.name", controllers.trustee.individual.routes.NameController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustee.individual.ukAddress", controllers.trustee.individual.routes.UkAddressController.onPageLoad().url)
      ).flatten)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection)(request, messages).toString
    }

    "redirect to the 'add a trustee' page when submitted" in {

      val mockTrustConnector = mock[TrustConnector]

      val userAnswers = emptyUserAnswers
        .set(IndividualOrBusinessPage, Individual).success.value
        .set(NamePage, trusteeName).success.value
        .set(WhenAddedPage, LocalDate.now).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
          .build()

      when(mockTrustConnector.addTrustee(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

  }
}
