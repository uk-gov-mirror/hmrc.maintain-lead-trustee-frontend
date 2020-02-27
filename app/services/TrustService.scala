package services

import connectors.TrustConnector
import javax.inject.Inject
import models.RemoveTrustee
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class TrustService @Inject()(
                            connector: TrustConnector
                            ) {

  def removeTrustee(removeTrustee: RemoveTrustee, utr: String)(implicit hc:HeaderCarrier, ec:ExecutionContext) = {
    connector.removeTrustee(utr, removeTrustee)
  }

}
