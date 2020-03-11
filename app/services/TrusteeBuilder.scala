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

import models.{AddressType, TrusteeIndividual, NonUkAddress, TrustIdentification, UkAddress, UserAnswers}
import pages.trustee.individual._


class TrusteeBuilder {

  import mapping.PlaybackImplicits._

  def createTrusteeIndividual(userAnswers: UserAnswers, date: LocalDate) = {
    TrusteeIndividual(
      userAnswers.get(NamePage).get,
      userAnswers.get(DateOfBirthPage),
      None,
      Some(
        TrustIdentification(
          None,
          userAnswers.get(NationalInsuranceNumberPage),
          userAnswers.get(PassportDetailsPage),
          buildAddress(userAnswers)
        )
      ), date,
      provisional = true
    )
  }

  private def buildAddress(userAnswers: UserAnswers): Option[AddressType] = {

    val uk: Option[UkAddress] = userAnswers.get(UkAddressPage)
    val nonUk: Option[NonUkAddress] = userAnswers.get(NonUkAddressPage)
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress.convert)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress.convert)
      case _ => None
    }
  }
}
