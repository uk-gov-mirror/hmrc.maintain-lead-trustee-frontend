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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.{LeadTrusteeOrganisation, Name, RemoveTrustee, TrustIdentification, TrustStartDate, TrusteeIndividual, TrusteeType, UkAddress}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class TrustConnectorSpec extends SpecBase with Generators with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def getLeadTrusteeUrl(utr: String): String = s"/trusts/$utr/transformed/lead-trustee"
  private def getTrustStartDateUrl(utr: String): String = s"/trusts/$utr/trust-start-date"
  private def removeTrusteeUrl(utr: String) = s"/trusts/$utr/trustee"

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  "TrustConnector amendLeadTrustee" must {
    "Be fine when request is successful" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo("/trusts/amend-lead-trustee/UTRUTRUTR"))
          .willReturn(ok)
      )

      val result = connector.amendLeadTrustee("UTRUTRUTR", arbitraryLeadTrusteeIndividual.arbitrary.sample.get)
      result.futureValue mustBe()

      application.stop()
    }

    "Deal with failures" ignore {
    }
  }


  "TrustConnector getLeadTrustee" must {

    "must return playback data inside a Processed trust" in {
      val utr = "1000000007"
      val json = Json.obj(
        "lineNo" -> "lineNo",
        "name" -> "name",
        "dateOfBirth" -> "1956-01-01",
        "phoneNumber" -> "phoneNumber",
        "identification" -> Json.obj(
          "utr" -> "anUtr",
            "address" -> Json.obj(
              "line1" -> "address1",
              "line2" -> "address2",
              "postcode" -> "Postcode"
            )
        ),
        "entityStart" -> "now"
      )

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        get(urlEqualTo(getLeadTrusteeUrl(utr)))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getLeadTrustee(utr)

      whenReady(processed) { leadTrustee =>
        leadTrustee mustBe LeadTrusteeOrganisation(
            "lineNo",
            None,
            "name",
            "phoneNumber",
            None,
            Some("anUtr"),
            UkAddress("address1", "address2", None, None, "Postcode"),
            "now"
          )
      }
      application.stop()
    }
  }

  "TrustConnector getTrustStartDate" must {

    "must return trust start date" in {
      val utr = "1000000007"
      val json = Json.obj(
        "startDate" -> "2019-06-01"
      )

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        get(urlEqualTo(getTrustStartDateUrl(utr)))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getTrustStartDate(utr)

      whenReady(processed) { startDate =>
        startDate mustBe TrustStartDate(
          "2019-06-01"
        )
      }
      application.stop()
    }
  }

  "TrustConnector removeTrustee" must {

    "must remove a trustee inside a Processed trust" in {

      val utr = "1000000008"

      val trustee = RemoveTrustee(trustee = TrusteeType(
          trusteeInd = Some(TrusteeIndividual(
            lineNo = "1",
            bpMatchStatus = Some("01"),
            name = Name(firstName = "first", middleName = Some(""), lastName = ""),
            dateOfBirth = Some(LocalDate.of(1983, 9, 24)),
            phoneNumber = None,
            identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
            entityStart = LocalDate.of(2019,2,28))
          ),
          trusteeOrg = None),
          endDate = LocalDate.now()
      )

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo(removeTrusteeUrl(utr)))
          .willReturn(ok)
      )

      val result = connector.removeTrustee(utr, trustee)
      result.futureValue mustBe()

      application.stop()
    }


  }


}
