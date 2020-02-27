package services

import connectors.TrustConnector
import models.RemoveTrustee
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status._
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}

class TrustServiceSpec() extends FreeSpec with MockitoSugar with MustMatchers with ScalaFutures {

  val mockConnector: TrustConnector = mock[TrustConnector]

  "Trust service" - {

    "remove a trustee" in {

      when(mockConnector.removeTrustee(any(),any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, None)))

      val service = new TrustService(mockConnector)

      val trustee : RemoveTrustee = ???

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.removeTrustee(trustee, "1234567890")

      whenReady(result) { r =>
        r.status mustBe 200
      }

    }

  }

}
