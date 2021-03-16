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

package controllers.trustee.amend

import base.SpecBase
import connectors.TrustConnector
import mapping.extractors.trustee.{TrusteeIndividualExtractor, TrusteeOrganisationExtractor}
import mapping.mappers.trustee.{TrusteeIndividualMapper, TrusteeOrganisationMapper}
import models.IndividualOrBusiness.{Business, Individual}
import models.{Name, TrusteeIndividual, TrusteeOrganisation, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.trustee.IndividualOrBusinessPage
import pages.trustee.{individual => ind, organisation => org}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.checkYourAnswers.{TrusteeIndividualPrintHelper, TrusteeOrganisationPrintHelper}
import viewmodels.AnswerSection
import views.html.trustee.amend.CheckDetailsView

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val indName: Name = Name("Joe", None, "Bloggs")
  private val orgName: String = "Name"
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val index = 0

  private lazy val onPageLoadRoute = routes.CheckDetailsController.onPageLoad(index).url
  private lazy val onPageLoadUpdatedRoute = routes.CheckDetailsController.onPageLoadUpdated(index).url
  private lazy val onSubmitRoute = routes.CheckDetailsController.onSubmit(index).url
  private lazy val onwardRoute = controllers.routes.AddATrusteeController.onPageLoad().url

  private val answerSection: AnswerSection = AnswerSection(None, Nil)

  "CheckDetails Controller" when {

    ".onPageLoad" must {

      "return OK and the correct view for a GET" when {

        "individual" in {

          val userAnswers = emptyUserAnswers

          val trustee: TrusteeIndividual = TrusteeIndividual(indName, None, None, None, None, None, None, None, date, provisional = false)

          val trustService = mock[TrustService]
          val extractor: TrusteeIndividualExtractor = mock[TrusteeIndividualExtractor]
          val printHelper: TrusteeIndividualPrintHelper = mock[TrusteeIndividualPrintHelper]

          when(trustService.getTrustee(any(), any())(any(), any())).thenReturn(Future.successful(trustee))
          when(extractor.extract(any(), any(), any())).thenReturn(Success(userAnswers))
          when(printHelper.print(any(), any(), any())(any())).thenReturn(answerSection)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[TrustService].toInstance(trustService))
            .overrides(bind[TrusteeIndividualExtractor].toInstance(extractor))
            .overrides(bind[TrusteeIndividualPrintHelper].toInstance(printHelper))
            .build()

          val request = FakeRequest(GET, onPageLoadRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckDetailsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(answerSection, index)(request, messages).toString

          verify(trustService).getTrustee(eqTo(userAnswers.identifier), eqTo(index))(any(), any())
          verify(extractor).extract(eqTo(userAnswers), eqTo(trustee), eqTo(index))
          verify(printHelper).print(eqTo(userAnswers), eqTo(false), eqTo(indName.displayName))(any())
        }

        "business" in {

          val userAnswers = emptyUserAnswers

          val trustee: TrusteeOrganisation = TrusteeOrganisation(orgName, None, None, None, None, date, provisional = false)

          val trustService = mock[TrustService]
          val extractor: TrusteeOrganisationExtractor = mock[TrusteeOrganisationExtractor]
          val printHelper: TrusteeOrganisationPrintHelper = mock[TrusteeOrganisationPrintHelper]

          when(trustService.getTrustee(any(), any())(any(), any())).thenReturn(Future.successful(trustee))
          when(extractor.extract(any(), any(), any())).thenReturn(Success(userAnswers))
          when(printHelper.print(any(), any(), any())(any())).thenReturn(answerSection)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[TrustService].toInstance(trustService))
            .overrides(bind[TrusteeOrganisationExtractor].toInstance(extractor))
            .overrides(bind[TrusteeOrganisationPrintHelper].toInstance(printHelper))
            .build()

          val request = FakeRequest(GET, onPageLoadRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckDetailsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(answerSection, index)(request, messages).toString

          verify(trustService).getTrustee(eqTo(userAnswers.identifier), eqTo(index))(any(), any())
          verify(extractor).extract(eqTo(userAnswers), eqTo(trustee), eqTo(index))
          verify(printHelper).print(eqTo(userAnswers), eqTo(false), eqTo(orgName))(any())
        }
      }
    }

    ".onPageLoadUpdated" must {

      "return OK and the correct view for a GET" when {

        "individual" in {

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Individual).success.value
            .set(ind.NamePage, indName).success.value

          val printHelper: TrusteeIndividualPrintHelper = mock[TrusteeIndividualPrintHelper]

          when(printHelper.print(any(), any(), any())(any())).thenReturn(answerSection)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[TrusteeIndividualPrintHelper].toInstance(printHelper))
            .build()

          val request = FakeRequest(GET, onPageLoadUpdatedRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckDetailsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(answerSection, index)(request, messages).toString

          verify(printHelper).print(eqTo(userAnswers), eqTo(false), eqTo(indName.displayName))(any())
        }

        "business" in {

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Business).success.value
            .set(org.NamePage, orgName).success.value

          val printHelper: TrusteeOrganisationPrintHelper = mock[TrusteeOrganisationPrintHelper]

          when(printHelper.print(any(), any(), any())(any())).thenReturn(answerSection)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[TrusteeOrganisationPrintHelper].toInstance(printHelper))
            .build()

          val request = FakeRequest(GET, onPageLoadUpdatedRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckDetailsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(answerSection, index)(request, messages).toString

          verify(printHelper).print(eqTo(userAnswers), eqTo(false), eqTo(orgName))(any())
        }
      }

      "return InternalServerError" when {

        "individual or business unanswered" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          val request = FakeRequest(GET, onPageLoadUpdatedRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }
    }

    ".onSubmit" must {

      "redirect to the 'add a trustee' page when submitted and clear out user answers" when {

        "individual" in {

          reset(playbackRepository)

          val mapper: TrusteeIndividualMapper = mock[TrusteeIndividualMapper]

          val mockTrustConnector = mock[TrustConnector]

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Individual).success.value
            .set(ind.NamePage, indName).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(bind[TrusteeIndividualMapper].toInstance(mapper))
            .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
            .build()

          val trustee = TrusteeIndividual(indName, None, None, None, None, None, None, None, date, provisional = true)

          when(mapper.map(any())).thenReturn(Some(trustee))

          when(mockTrustConnector.amendTrustee(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          when(playbackRepository.set(any())).thenReturn(Future.successful(true))

          val captor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass[UserAnswers](classOf[UserAnswers])

          val request = FakeRequest(POST, onSubmitRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          application.stop()

          verify(playbackRepository).set(captor.capture)
          captor.getValue.data mustBe Json.obj()

          verify(mapper).map(userAnswers)
          verify(mockTrustConnector).amendTrustee(eqTo(userAnswers.identifier), eqTo(index), eqTo(trustee))(any(), any())
        }

        "business" in {

          reset(playbackRepository)

          val mapper: TrusteeOrganisationMapper = mock[TrusteeOrganisationMapper]

          val mockTrustConnector = mock[TrustConnector]

          val userAnswers = emptyUserAnswers
            .set(IndividualOrBusinessPage, Business).success.value
            .set(org.NamePage, orgName).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
            .overrides(bind[TrusteeOrganisationMapper].toInstance(mapper))
            .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
            .build()

          val trustee = TrusteeOrganisation(orgName, None, None, None, None, date, provisional = true)

          when(mapper.map(any())).thenReturn(Some(trustee))

          when(mockTrustConnector.amendTrustee(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          when(playbackRepository.set(any())).thenReturn(Future.successful(true))

          val captor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass[UserAnswers](classOf[UserAnswers])

          val request = FakeRequest(POST, onSubmitRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute

          application.stop()

          verify(playbackRepository).set(captor.capture)
          captor.getValue.data mustBe Json.obj()

          verify(mapper).map(userAnswers)
          verify(mockTrustConnector).amendTrustee(eqTo(userAnswers.identifier), eqTo(index), eqTo(trustee))(any(), any())
        }
      }

      "return InternalServerError" when {

        "individual or business unanswered" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent).build()

          val request = FakeRequest(POST, onSubmitRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }

        "mapper fails" when {

          "individual" in {

            val mapper: TrusteeIndividualMapper = mock[TrusteeIndividualMapper]

            val userAnswers = emptyUserAnswers
              .set(IndividualOrBusinessPage, Individual).success.value
              .set(ind.NamePage, indName).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
              .overrides(bind[TrusteeIndividualMapper].toInstance(mapper))
              .build()

            when(mapper.map(any())).thenReturn(None)

            val request = FakeRequest(POST, onSubmitRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()

            verify(mapper).map(userAnswers)
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

            val request = FakeRequest(POST, onSubmitRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            application.stop()

            verify(mapper).map(userAnswers)
          }
        }
      }
    }
  }
}
