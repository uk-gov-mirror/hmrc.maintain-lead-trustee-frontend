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
import forms.DetailsChoiceFormProvider
import models.{DetailsChoice, Name, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.leadtrustee.individual.{NamePage, TrusteeDetailsChoicePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.leadtrustee.individual.TrusteeDetailsChoiceView

class DetailsChoiceControllerSpec extends SpecBase {

  val messageKeyPrefix = "leadTrustee.individual.trusteeDetailsChoice"
  val form: Form[DetailsChoice] = new DetailsChoiceFormProvider().withPrefix(messageKeyPrefix)

  val index = 0
  val trusteeName: Name = Name("FirstName", None, "LastName")

  lazy val trusteeDetailsChoiceUKRoute: String = controllers.leadtrustee.individual.routes.DetailsChoiceController.onPageLoad().url

  val answersWithName: UserAnswers = emptyUserAnswers
    .set(NamePage, trusteeName).success.value

  val validAnswer: DetailsChoice = DetailsChoice.IdCard

  "TrusteeDetailsChoice Controller" when {

    "4mld" must {

      val is5mldEnabled = false
      val baseAnswers = answersWithName.copy(is5mldEnabled = is5mldEnabled)

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        val request = FakeRequest(GET, trusteeDetailsChoiceUKRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = baseAnswers
          .set(TrusteeDetailsChoicePage, validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeDetailsChoiceUKRoute)

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(validAnswer), trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        val request = FakeRequest(POST, trusteeDetailsChoiceUKRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }
    }

    "5mld" must {

      val is5mldEnabled = true
      val baseAnswers = answersWithName.copy(is5mldEnabled = is5mldEnabled)

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        val request = FakeRequest(GET, trusteeDetailsChoiceUKRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = baseAnswers
          .set(TrusteeDetailsChoicePage, validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeDetailsChoiceUKRoute)

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(validAnswer), trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        val request = FakeRequest(POST, trusteeDetailsChoiceUKRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrusteeDetailsChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, trusteeName.displayName, is5mldEnabled)(request, messages).toString

        application.stop()
      }
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(answersWithName))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator())
          ).build()

      val request = FakeRequest(POST, trusteeDetailsChoiceUKRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeDetailsChoiceUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, trusteeDetailsChoiceUKRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
