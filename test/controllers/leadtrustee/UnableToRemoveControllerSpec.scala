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


import java.time.LocalDateTime

import base.SpecBase
import models.IdentificationDetailOptions.Passport
import models.{CombinedPassportOrIdCard, IndividualIdentification, LeadTrusteeIndividual, Name, UkAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.trustee.individual.DateOfBirthPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import services.TrustService
import views.html.leadtrustee.UnableToRemoveView
import java.time.LocalDate

import connectors.TrustConnector
import navigation.Navigator
import play.api.inject.bind

import scala.concurrent.Future

class UnableToRemoveControllerSpec extends SpecBase   {

  "UnableToRemoveController" must {

    val name = "Test Name"

    "return OK and the correct view for a GET" in {

      val mockTrustService= mock[TrustService]

      val lt = LeadTrusteeIndividual(
        name = Name("Test", None, "Name"),
        dateOfBirth = LocalDate.parse("1983-09-24"),
        phoneNumber = "1234567890",
        None,
        identification = CombinedPassportOrIdCard("Great Briton", "12345678654", LocalDate.parse("1970-09-24")),
        address = Some(UkAddress("Test Line 1","Test Line 2", None,None,"AB1 1BA"))

      )

      when(mockTrustService.getLeadTrustee(any())(any(), any())).thenReturn(Future.successful(Some(lt)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustService].toInstance(mockTrustService)).build()

      val request = FakeRequest(GET, routes.UnableToRemoveController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[UnableToRemoveView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to AddToPage when no name is found for lead trustee" in {

      val mockTrustService = mock[TrustService]

      when(mockTrustService.getLeadTrustee(any())(any(), any())).thenReturn(Future.successful(None))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        )
        .build()

      val request = FakeRequest(GET, routes.UnableToRemoveController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddATrusteeController.onPageLoad().url

      application.stop()
    }
  }
}

