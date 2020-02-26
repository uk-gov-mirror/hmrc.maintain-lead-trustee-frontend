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

import config.FrontendAppConfig
import javax.inject.Inject
import models.{LeadTrustee, LeadTrusteeIndividual, RemoveTrustee, TrustStartDate, TrusteeIndividual, TrusteeType}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  private def getLeadTrusteeUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/transformed/lead-trustee"

  def getLeadTrustee(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[LeadTrustee] = {
    http.GET[LeadTrustee](getLeadTrusteeUrl(utr))
  }

  private def getTrustStartDateUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/trust-start-date"

  def getTrustStartDate(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[TrustStartDate] = {
    http.GET[TrustStartDate](getTrustStartDateUrl(utr))
  }

  private def addTrusteeIndividualUrl(utr: String) = s"${config.trustsUrl}/trusts/add-trustee/$utr"

  def addTrusteeIndividual(utr: String, trustee: TrusteeIndividual)(implicit hc: HeaderCarrier, ec : ExecutionContext)= {
    http.POST[JsValue, HttpResponse](addTrusteeIndividualUrl(utr), Json.toJson(trustee))
  }

  private def getTrusteesUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/transformed/trustees"

  def getTrustees(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[TrusteeType] = {
    http.GET[TrusteeType](getTrusteesUrl(utr))
  }

  private def removeTrusteeUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/trustee"

  def removeTrustee(utr: String, trustee: RemoveTrustee)(implicit hc: HeaderCarrier, ec : ExecutionContext)= {
    http.POST[JsValue, HttpResponse](removeTrusteeUrl(utr), Json.toJson(trustee))
  }

  private def amendLeadTrusteeUrl(utr: String) = s"${config.trustsUrl}/trusts/amend-lead-trustee/$utr "
  def amendLeadTrustee(utr: String, leadTrustee: LeadTrusteeIndividual)(implicit hc: HeaderCarrier, ec : ExecutionContext) : Future[Unit] = {
    http.POST[LeadTrusteeIndividual, HttpResponse](amendLeadTrusteeUrl(utr), leadTrustee)(LeadTrusteeIndividual.writes, HttpReads.readRaw, hc, ec).map(_ => ())
  }
}


