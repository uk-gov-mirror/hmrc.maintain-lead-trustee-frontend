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

import models.{AddressType, NonUkAddress, TrustIdentification, TrustIdentificationOrgType, TrusteeIndividual, TrusteeOrganisation, UkAddress, UserAnswers}
import pages.trustee.{individual => ind, organisation => org}

class TrusteeBuilder {

  import mapping.PlaybackImplicits._

  def createTrusteeIndividual(userAnswers: UserAnswers, date: LocalDate) = {
    TrusteeIndividual(
      userAnswers.get(ind.NamePage).get,
      userAnswers.get(ind.DateOfBirthPage),
      None,
      Some(
        TrustIdentification(
          None,
          userAnswers.get(ind.NationalInsuranceNumberPage),
          userAnswers.get(ind.PassportDetailsPage).orElse(userAnswers.get(ind.IdCardDetailsPage)),
          buildIndAddress(userAnswers)
        )
      ),
      date,
      provisional = true
    )
  }

  private def buildIndAddress(userAnswers: UserAnswers): Option[AddressType] = {

    val uk: Option[UkAddress] = userAnswers.get(ind.UkAddressPage)
    val nonUk: Option[NonUkAddress] = userAnswers.get(ind.NonUkAddressPage)
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress.convert)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress.convert)
      case _ => None
    }
  }

  def createTrusteeOrganisation(userAnswers: UserAnswers, date: LocalDate) = {
    TrusteeOrganisation(
      userAnswers.get(org.NamePage).get,
      None,
      None,
      Some(
        TrustIdentificationOrgType(
          None,
          userAnswers.get(org.UtrPage),
          buildOrgAddress(userAnswers)
        )
      ),
      date,
      provisional = true
    )
  }

  private def buildOrgAddress(userAnswers: UserAnswers): Option[AddressType] = {

    val uk: Option[UkAddress] = userAnswers.get(org.UkAddressPage)
    val nonUk: Option[NonUkAddress] = userAnswers.get(org.NonUkAddressPage)
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress.convert)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress.convert)
      case _ => None
    }
  }

}
