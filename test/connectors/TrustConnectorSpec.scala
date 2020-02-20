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

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.{DisplayTrustIdentificationOrgType, DisplayTrustLeadTrusteeOrgType, DisplayTrustLeadTrusteeType}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class TrustConnectorSpec extends SpecBase with Generators with ScalaFutures with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def getLeadTrusteeUrl(utr: String): String = s"/trusts/$utr/transformed/leadTrustee"

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

  "TrustConnector getLeadTrustee" must {

    "must return playback data inside a Processed trust" in {
      val utr = "1000000007"
      val json = Json.obj(
        "lineNo" -> "lineNo",
        "name" -> "name",
        "phoneNumber" -> "phoneNumber",
        "identification" -> Json.obj(
          "utr" -> "anUtr"
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
        leadTrustee mustBe DisplayTrustLeadTrusteeType(
          None,
          Some(DisplayTrustLeadTrusteeOrgType(
            "lineNo",
            None,
            "name",
            "phoneNumber",
            None,
            DisplayTrustIdentificationOrgType(None, Some("anUtr"), None),
            "now"
          ))
        )
      }
      application.stop()
    }
  }
}
