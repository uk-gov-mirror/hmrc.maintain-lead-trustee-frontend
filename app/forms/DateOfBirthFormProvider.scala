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

package forms

import config.FrontendAppConfig

import java.time.LocalDate
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form

class DateOfBirthFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings {

  // TRUS-3881
  def withConfig(prefix: String, is5MldEnabled: Boolean = false): Form[LocalDate] = {
    val minimumDate: LocalDate = if (is5MldEnabled) appConfig.minLeadTrusteeDob else appConfig.minDate
    Form(
      "value" -> localDate(
        invalidKey = s"$prefix.error.invalid",
        allRequiredKey = s"$prefix.error.required.all",
        twoRequiredKey = s"$prefix.error.required.two",
        requiredKey = s"$prefix.error.required"
      ).verifying(firstError(
        maxDate(LocalDate.now, s"$prefix.error.future", "day", "month", "year"),
        minDate(minimumDate, s"$prefix.error.past", "day", "month", "year")
      ))
    )
  }
}
