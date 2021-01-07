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

package pages

import models.TrusteeType._
import models.{TrusteeType, UserAnswers}
import pages.leadtrustee.{individual => lind, organisation => lorg}
import pages.trustee.{WhenAddedPage, individual => ind}
import play.api.libs.json.JsPath

import scala.util.Try

object TrusteeTypePage extends QuestionPage[TrusteeType] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "trusteeType"

  override def cleanup(value: Option[TrusteeType], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(LeadTrustee) =>
        userAnswers.remove(pages.trustee.IndividualOrBusinessPage)
          .flatMap(_.remove(ind.NamePage))
          .flatMap(_.remove(ind.DateOfBirthYesNoPage))
          .flatMap(_.remove(ind.DateOfBirthPage))
          .flatMap(_.remove(ind.NationalInsuranceNumberYesNoPage))
          .flatMap(_.remove(ind.NationalInsuranceNumberPage))
          .flatMap(_.remove(ind.AddressYesNoPage))
          .flatMap(_.remove(ind.LiveInTheUkYesNoPage))
          .flatMap(_.remove(ind.UkAddressPage))
          .flatMap(_.remove(ind.NonUkAddressPage))
          .flatMap(_.remove(ind.PassportDetailsYesNoPage))
          .flatMap(_.remove(ind.PassportDetailsPage))
          .flatMap(_.remove(ind.IdCardDetailsYesNoPage))
          .flatMap(_.remove(ind.IdCardDetailsPage))
          .flatMap(_.remove(WhenAddedPage))
      case Some(Trustee) =>
        userAnswers.remove(pages.leadtrustee.IndividualOrBusinessPage)
          .flatMap(_.remove(lind.NamePage))
          .flatMap(_.remove(lind.DateOfBirthPage))
          .flatMap(_.remove(lind.UkCitizenPage))
          .flatMap(_.remove(lind.NationalInsuranceNumberPage))
          .flatMap(_.remove(lind.PassportOrIdCardDetailsPage))
          .flatMap(_.remove(lind.LiveInTheUkYesNoPage))
          .flatMap(_.remove(lind.UkAddressPage))
          .flatMap(_.remove(lind.NonUkAddressPage))
          .flatMap(_.remove(lind.EmailAddressYesNoPage))
          .flatMap(_.remove(lind.EmailAddressPage))
          .flatMap(_.remove(lind.TelephoneNumberPage))
          
          .flatMap(_.remove(lorg.RegisteredInUkYesNoPage))
          .flatMap(_.remove(lorg.NamePage))
          .flatMap(_.remove(lorg.UtrPage))
          .flatMap(_.remove(lorg.AddressInTheUkYesNoPage))
          .flatMap(_.remove(lorg.UkAddressPage))
          .flatMap(_.remove(lorg.NonUkAddressPage))
          .flatMap(_.remove(lorg.EmailAddressYesNoPage))
          .flatMap(_.remove(lorg.EmailAddressPage))
          .flatMap(_.remove(lorg.TelephoneNumberPage))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }

}
