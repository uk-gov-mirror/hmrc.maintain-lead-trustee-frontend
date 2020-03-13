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

import models.{Address, IndividualIdentification, NationalInsuranceNumber, NonUkAddress, TrusteeIndividual, UkAddress, UserAnswers}
import pages.trustee.individual._


class TrusteeBuilder {
  def createTrusteeIndividual(userAnswers: UserAnswers, date: LocalDate, index: Int) = {
    TrusteeIndividual(
      userAnswers.get(NamePage(index)).get,
      userAnswers.get(DateOfBirthPage(index)),
      None,
      buildIdentfication(userAnswers, index),
      buildAddress(userAnswers, index),
      date,
      provisional = true
    )
  }

  private def buildIdentfication(userAnswers: UserAnswers, index: Int) : Option[IndividualIdentification] = {
    userAnswers.get(NationalInsuranceNumberPage(index)).map (n => new NationalInsuranceNumber(n))
      .orElse(userAnswers.get(PassportDetailsPage(index)))
      .orElse(userAnswers.get(IdCardDetailsPage(index)))
  }

  private def buildAddress(userAnswers: UserAnswers, index: Int): Option[Address] = {

    val uk: Option[UkAddress] = userAnswers.get(UkAddressPage(index))
    val nonUk: Option[NonUkAddress] = userAnswers.get(NonUkAddressPage(index))
    (uk, nonUk) match {
      case (Some(ukAddress), None) => Some(ukAddress)
      case (None, Some(nonUkAddress)) => Some(nonUkAddress)
      case _ => None
    }
  }
}
