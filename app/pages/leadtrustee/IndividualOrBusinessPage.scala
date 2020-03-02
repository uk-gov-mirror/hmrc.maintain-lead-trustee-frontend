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

package pages.leadtrustee

import models.IndividualOrBusiness._
import models.{IndividualOrBusiness, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

object IndividualOrBusinessPage extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = basePath \  toString

  override def toString: String = "individualOrBusiness"

  override def cleanup(value: Option[IndividualOrBusiness], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Business) =>
        userAnswers.remove(individual.NamePage)
          .flatMap(_.remove(individual.DateOfBirthPage))
          .flatMap(_.remove(individual.UkCitizenPage))
          .flatMap(_.remove(individual.NationalInsuranceNumberPage))
          .flatMap(_.remove(individual.IdentificationDetailOptionsPage))
          .flatMap(_.remove(individual.PassportDetailsPage))
          .flatMap(_.remove(individual.IdCardDetailsPage))
          .flatMap(_.remove(individual.LiveInTheUkYesNoPage))
          .flatMap(_.remove(individual.UkAddressPage))
          .flatMap(_.remove(individual.NonUkAddressPage))
          .flatMap(_.remove(individual.EmailAddressYesNoPage))
          .flatMap(_.remove(individual.EmailAddressPage))
          .flatMap(_.remove(individual.TelephoneNumberPage))
      case Some(Individual) =>
        userAnswers.remove(organisation.RegisteredInUkYesNoPage)
          .flatMap(_.remove(organisation.NamePage))
          .flatMap(_.remove(organisation.UtrPage))
          .flatMap(_.remove(organisation.LiveInTheUkYesNoPage))
          .flatMap(_.remove(organisation.UkAddressPage))
          .flatMap(_.remove(organisation.NonUkAddressPage))
          .flatMap(_.remove(organisation.EmailAddressYesNoPage))
          .flatMap(_.remove(organisation.EmailAddressPage))
          .flatMap(_.remove(organisation.TelephoneNumberPage))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }

}