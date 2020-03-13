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
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class TrustsStoreConnectorSpec extends SpecBase with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

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

  "trusts store connector" must {

    "return OK with the current task status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustStoreConnector]

      val json = Json.parse(
        """
          |{
          |  "trustees": true,
          |  "beneficiaries": false,
          |  "settlors": false,
          |  "protectors": false,
          |  "other": false
          |}
          |""".stripMargin)

      server.stubFor(
        post(urlEqualTo("/trusts-store/maintain/tasks/trustees/123456789"))
          .willReturn(okJson(json.toString))
      )

      val result = connector.setTaskComplete("123456789")

      result.futureValue.status mustBe 200

      application.stop()
    }

    "return default tasks when a failure occurs" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustStoreConnector]

      server.stubFor(
        post(urlEqualTo("/trusts-store/maintain/tasks/trustees/123456789"))
          .willReturn(serverError())
      )

      val result = connector.setTaskComplete("123456789")

      result.futureValue.status mustBe 500

      application.stop()
    }

  }

}
