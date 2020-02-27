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

package services

import java.time.LocalDate

import connectors.TrustConnector
import models.{Name, RemoveTrustee, TrustIdentification, TrusteeIndividual, TrusteeType}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TrustServiceSpec() extends FreeSpec with MockitoSugar with MustMatchers with ScalaFutures {

  val mockConnector: TrustConnector = mock[TrustConnector]

  "Trust service" - {

    "get trustees" in {

      val trusteeInd = TrusteeIndividual(
        name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
        dateOfBirth = Some(LocalDate.of(1983, 9, 24)),
        phoneNumber = None,
        identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
        entityStart = LocalDate.of(2019,2,28))

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(List(TrusteeType(Some(trusteeInd), None))))

      val service = new TrustService(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getTrustees("1234567890")

      whenReady(result) { r =>
        r mustBe List(TrusteeType(Some(trusteeInd), None))
      }

    }

    "remove a trustee" in {

      when(mockConnector.removeTrustee(any(),any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, None)))

      val service = new TrustService(mockConnector)

      val trustee : RemoveTrustee =  RemoveTrustee(trustee = TrusteeType(
        trusteeInd = Some(TrusteeIndividual(
          name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
          dateOfBirth = Some(LocalDate.of(1983, 9, 24)),
          phoneNumber = None,
          identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
          entityStart = LocalDate.of(2019,2,28))
        ),
        trusteeOrg = None),
        endDate = LocalDate.now()
      )

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.removeTrustee(trustee, "1234567890")

      whenReady(result) { r =>
        r.status mustBe 200
      }

    }

  }

}
