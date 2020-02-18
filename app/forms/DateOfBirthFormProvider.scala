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

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class DateOfBirthFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "leadtrustee.individual.dateOfBirth.error.invalid",
        allRequiredKey = "leadtrustee.individual.dateOfBirth.error.required.all",
        twoRequiredKey = "leadtrustee.individual.dateOfBirth.error.required.two",
        requiredKey    = "leadtrustee.individual.dateOfBirth.error.required"
      ).verifying(firstError(
        maxDate(LocalDate.now, "leadtrustee.individual.dateOfBirth.error.future", "day", "month", "year"),
        minDate(LocalDate.of(1500,1,1), "leadtrustee.individual.dateOfBirth.error.past", "day", "month", "year")
      ))
    )
}
