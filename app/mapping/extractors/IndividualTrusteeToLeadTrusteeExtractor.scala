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

import models._
import pages.QuestionPage
import pages.leadtrustee.IndividualOrBusinessPage
import pages.leadtrustee.individual._
import play.api.libs.json.JsPath

import scala.util.Try

class IndividualTrusteeToLeadTrusteeExtractor extends IndividualExtractor {

  override def basePath: JsPath = pages.leadtrustee.basePath
  override def individualOrBusinessPage: QuestionPage[IndividualOrBusiness] = IndividualOrBusinessPage

  override def ukCountryOfNationalityYesNoPage: QuestionPage[Boolean] = CountryOfNationalityInTheUkYesNoPage
  override def countryOfNationalityPage: QuestionPage[String] = CountryOfNationalityPage

  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceInTheUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

  override def ukAddressYesNoPage: QuestionPage[Boolean] = LiveInTheUkYesNoPage
  override def ukAddressPage: QuestionPage[UkAddress] = UkAddressPage
  override def nonUkAddressPage: QuestionPage[NonUkAddress] = NonUkAddressPage

  override def ninoYesNoPage: QuestionPage[Boolean] = UkCitizenPage
  override def ninoPage: QuestionPage[String] = NationalInsuranceNumberPage
  override def passportOrIdCardDetailsPage: QuestionPage[CombinedPassportOrIdCard] = PassportOrIdCardDetailsPage

  def extract(userAnswers: UserAnswers, trustee: TrusteeIndividual, index: Int): Try[UserAnswers] = {
    super.extract(userAnswers)
      .flatMap(_.set(IndexPage, index))
      .flatMap(_.set(NamePage, trustee.name))
      .flatMap(answers => extractValue(trustee.dateOfBirth, DateOfBirthPage, answers))
      .flatMap(answers => extractCountryOfNationality(trustee.nationality, answers))
      .flatMap(answers => extractIdentification(trustee.identification, answers))
      .flatMap(answers => extractAddress(trustee.address, answers))
      .flatMap(answers => extractCountryOfResidence(trustee.countryOfResidence, answers))
      .flatMap(answers => extractValue(trustee.phoneNumber, TelephoneNumberPage, answers))
  }

}
