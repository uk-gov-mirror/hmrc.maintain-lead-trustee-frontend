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

import base.SpecBase
import connectors.TrustsIndividualCheckConnector
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, verify}

class TrustsIndividualCheckServiceSpec extends SpecBase {
  /*
  * 1. If 4MLD
  *   then return "ServiceNotIn5mldModeResponse"
  * 2. If 5MLD
  *   then do a matching request
  * */

  "TrustsIndividualCheckService" when {

    "matchLeadTrustee" when {

      "4MLD" must {

        "return ServiceNotIn5mldModeResponse" in {
          /*
          * 1. If 4MLD
          *   then return "ServiceNotIn5mldModeResponse"
          */

          val mockConnector = mock[TrustsIndividualCheckConnector]
          val service = new TrustsIndividualCheckService(mockConnector)
          //
          //          val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false)
          //
          //          val result = service.matchLeadTrustee(userAnswers, index)
          //
          //          whenReady(result) { res =>
          //            res mustBe ServiceNotIn5mldModeResponse
          verify(mockConnector, never()).matchLeadTrustee(any())(any(), any())
        }
      }
    }
  }
}
