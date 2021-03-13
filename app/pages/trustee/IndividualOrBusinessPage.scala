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

package pages.trustee

import models.IndividualOrBusiness._
import models.{IndividualOrBusiness, UserAnswers}
import pages.QuestionPage
import pages.trustee.{individual => ind, organisation => org}
import play.api.libs.json.JsPath
import scala.util.Try

case object IndividualOrBusinessPage extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = basePath \ toString

  override def toString: String = "individualOrBusiness"

  private def removeIndividualData(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(ind.NamePage)
      .flatMap(_.remove(ind.DateOfBirthYesNoPage))
      .flatMap(_.remove(ind.DateOfBirthPage))
      .flatMap(_.remove(ind.CountryOfNationalityYesNoPage))
      .flatMap(_.remove(ind.CountryOfNationalityInTheUkYesNoPage))
      .flatMap(_.remove(ind.CountryOfNationalityPage))
      .flatMap(_.remove(ind.NationalInsuranceNumberYesNoPage))
      .flatMap(_.remove(ind.NationalInsuranceNumberPage))
      .flatMap(_.remove(ind.CountryOfResidenceYesNoPage))
      .flatMap(_.remove(ind.CountryOfResidenceInTheUkYesNoPage))
      .flatMap(_.remove(ind.CountryOfResidencePage))
      .flatMap(_.remove(ind.AddressYesNoPage))
      .flatMap(_.remove(ind.LiveInTheUkYesNoPage))
      .flatMap(_.remove(ind.UkAddressPage))
      .flatMap(_.remove(ind.NonUkAddressPage))
      .flatMap(_.remove(ind.PassportDetailsYesNoPage))
      .flatMap(_.remove(ind.PassportDetailsPage))
      .flatMap(_.remove(ind.IdCardDetailsYesNoPage))
      .flatMap(_.remove(ind.IdCardDetailsPage))
      .flatMap(_.remove(ind.MentalCapacityYesNoPage))
      .flatMap(_.remove(WhenAddedPage))
  }

  private def removeOrganisationData(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(org.NamePage)
      .flatMap(_.remove(org.UtrYesNoPage))
      .flatMap(_.remove(org.UtrPage))
      .flatMap(_.remove(org.CountryOfResidenceYesNoPage))
      .flatMap(_.remove(org.CountryOfResidenceInTheUkYesNoPage))
      .flatMap(_.remove(org.CountryOfResidencePage))
      .flatMap(_.remove(org.AddressYesNoPage))
      .flatMap(_.remove(org.AddressInTheUkYesNoPage))
      .flatMap(_.remove(org.UkAddressPage))
      .flatMap(_.remove(org.NonUkAddressPage))
      .flatMap(_.remove(WhenAddedPage))
  }

  override def cleanup(value: Option[IndividualOrBusiness], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Business) => removeIndividualData(userAnswers)
      case Some(Individual) => removeOrganisationData(userAnswers)
      case _ => for  {
        a <- removeIndividualData(userAnswers)
        b <- removeOrganisationData(a)
      } yield b
    }
  }
}
