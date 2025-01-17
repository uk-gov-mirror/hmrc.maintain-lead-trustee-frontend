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

package services

import connectors.TrustConnector
import javax.inject.Inject
import models.{AllTrustees, LeadTrustee, RemoveTrustee, Trustee, Trustees}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait TrustService {

  def getAllTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllTrustees]

  def getLeadTrustee(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[LeadTrustee]]

  def getTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Trustees]

  def removeTrustee(utr: String, trustee: RemoveTrustee)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[HttpResponse]

  def getTrustee(utr: String, index: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Trustee]

}

class TrustServiceImpl @Inject()(
                            connector: TrustConnector
                            ) extends TrustService {

  override def getAllTrustees(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllTrustees] = {
    for {
      lead <- getLeadTrustee(utr)
      trustees <- getTrustees(utr)
    } yield {
      AllTrustees(lead, trustees.trustees)
    }
  }

  override def getLeadTrustee(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[LeadTrustee]] =
    connector.getLeadTrustee(utr).map(Some(_))

  override def getTrustees(utr: String)(implicit hc:HeaderCarrier, ec:ExecutionContext) = {
    connector.getTrustees(utr)
  }

  override def removeTrustee(utr: String, trustee: RemoveTrustee)(implicit hc:HeaderCarrier, ec:ExecutionContext) = {
    connector.removeTrustee(utr, trustee)
  }

  override def getTrustee(utr: String, index: Int)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Trustee] = {
    getTrustees(utr).map(_.trustees(index))
  }

}
