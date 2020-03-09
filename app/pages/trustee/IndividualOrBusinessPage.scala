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

package pages.trustee

import models.IndividualOrBusiness._
import models.{IndividualOrBusiness, UserAnswers}
import pages.QuestionPage
import pages.trustee.individual._
import play.api.libs.json.JsPath
import scala.util.Try

case class IndividualOrBusinessPage(index: Int) extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = basePath \ index \ toString

  override def toString: String = "individualOrBusiness"

  private def removeIndividualData(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(NamePage(index))
      .flatMap(_.remove(DateOfBirthYesNoPage(index)))
      .flatMap(_.remove(DateOfBirthPage(index)))
      .flatMap(_.remove(NationalInsuranceNumberYesNoPage(index)))
      .flatMap(_.remove(NationalInsuranceNumberPage(index)))
      .flatMap(_.remove(AddressYesNoPage(index)))
      .flatMap(_.remove(LiveInTheUkYesNoPage(index)))
      .flatMap(_.remove(UkAddressPage(index)))
      .flatMap(_.remove(NonUkAddressPage(index)))
      .flatMap(_.remove(PassportDetailsYesNoPage(index)))
      .flatMap(_.remove(PassportDetailsPage(index)))
      .flatMap(_.remove(IdCardDetailsYesNoPage(index)))
      .flatMap(_.remove(IdCardDetailsPage(index)))
      .flatMap(_.remove(WhenAddedPage(index)))
  }

  private def removeOrganisationData(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(WhenAddedPage(index))
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