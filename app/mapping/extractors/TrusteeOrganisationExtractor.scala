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

package mapping.extractors

import models.IndividualOrBusiness.Business
import models.{Address, NonUkAddress, TrustIdentificationOrgType, TrusteeOrganisation, UkAddress, UserAnswers}
import pages.trustee.amend.{organisation => org}
import pages.trustee.organisation.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import pages.trustee.{IndividualOrBusinessPage, WhenAddedPage}
import models.Constant.GB

import scala.util.{Success, Try}

class TrusteeOrganisationExtractor {

  def extract(answers: UserAnswers, trustee: TrusteeOrganisation, index: Int): Try[UserAnswers] = {
    answers.deleteAtPath(pages.trustee.basePath)
      .flatMap(_.set(IndividualOrBusinessPage, Business))
      .flatMap(_.set(org.IndexPage, index))
      .flatMap(_.set(org.NamePage, trustee.name))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(answers => extractCountryOfResidence(trustee.countryOfResidence, answers))
      .flatMap(_.set(WhenAddedPage, trustee.entityStart))
  }

  private def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      countryOfResidence match {
        case Some(GB) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceInTheUkYesNoPage, true))
          .flatMap(_.set(CountryOfResidencePage, GB))
        case Some(country) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceInTheUkYesNoPage, false))
          .flatMap(_.set(CountryOfResidencePage, country))
        case None => answers
          .set(CountryOfResidenceYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractIdentification(identification: Option[TrustIdentificationOrgType], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable || !answers.is5mldEnabled) {
      identification match {
        case Some(TrustIdentificationOrgType(_, Some(utr), None)) =>
          answers.set(org.UtrYesNoPage, true)
            .flatMap(_.set(org.UtrPage, utr))
        case Some(TrustIdentificationOrgType(_, None, Some(address))) =>
          answers.set(org.UtrYesNoPage, false)
            .flatMap(answers => extractAddress(address, answers))
        case _ =>
          answers.set(org.UtrYesNoPage, false)
            .flatMap(_.set(org.AddressYesNoPage, false))
      }
    } else {
      Success(answers)
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable || !answers.is5mldEnabled) {
      address match {
        case uk: UkAddress =>
          answers.set(org.AddressYesNoPage, true)
            .flatMap(_.set(org.AddressInTheUkYesNoPage, true))
            .flatMap(_.set(org.UkAddressPage, uk))
        case nonUk: NonUkAddress =>
          answers.set(org.AddressYesNoPage, true)
            .flatMap(_.set(org.AddressInTheUkYesNoPage, false))
            .flatMap(_.set(org.NonUkAddressPage, nonUk))
      }
    } else {
      Success(answers)
    }
  }

}
