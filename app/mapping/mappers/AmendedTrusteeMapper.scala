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

package mapping.mappers

import models._
import pages.trustee.amend.{individual => ind, organisation => org}

import java.time.LocalDate

class AmendedTrusteeMapper {

  def createTrusteeIndividual(userAnswers: UserAnswers, date: LocalDate): TrusteeIndividual = {
    TrusteeIndividual(
      userAnswers.get(ind.NamePage).get,
      userAnswers.get(ind.DateOfBirthPage),
      None,
      buildIdentification(userAnswers),
      buildIndAddress(userAnswers),
      date,
      provisional = true
    )
  }

  private def buildIdentification(userAnswers: UserAnswers) : Option[IndividualIdentification] = {
    userAnswers.get(ind.NationalInsuranceNumberPage).map (n => new NationalInsuranceNumber(n))
      .orElse(userAnswers.get(ind.PassportOrIdCardDetailsPage))
  }

  private def buildIndAddress(userAnswers: UserAnswers): Option[Address] = {

    val uk: Option[UkAddress] = userAnswers.get(ind.UkAddressPage)
    val nonUk: Option[NonUkAddress] = userAnswers.get(ind.NonUkAddressPage)
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress)
      case _ => None
    }
  }

  def createTrusteeOrganisation(userAnswers: UserAnswers, date: LocalDate): TrusteeOrganisation = {
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

  private def buildOrgAddress(userAnswers: UserAnswers): Option[Address] = {

    val uk: Option[UkAddress] = userAnswers.get(org.UkAddressPage)
    val nonUk: Option[NonUkAddress] = userAnswers.get(org.NonUkAddressPage)
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress)
      case _ => None
    }
  }
}
