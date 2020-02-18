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

import forms.mappings.Mappings
import javax.inject.Inject
import models.Name
import play.api.data.Form
import play.api.data.Forms._

class NameFormProvider @Inject() extends Mappings {

  def apply(): Form[Name] =   Form(
    mapping(
      "firstName" -> text("leadtrustee.individual.name.error.firstName.required")
        .verifying(
          firstError(
            maxLength(35, "leadtrustee.individual.name.error.firstName.length"),
            nonEmptyString("firstName", "leadtrustee.individual.name.error.firstName.required"),
            regexp(Validation.nameRegex, "leadtrustee.individual.name.error.firstName.invalid")
          )
        ),
      "middleName" -> optional(text()
        .verifying(
          firstError(
            maxLength(35, "leadtrustee.individual.name.error.middleName.length"),
            regexp(Validation.nameRegex, "leadtrustee.individual.name.error.middleName.invalid"))
        )
      ),
      "lastName" -> text("leadtrustee.individual.name.error.lastName.required")
        .verifying(
          firstError(
            maxLength(35, "leadtrustee.individual.name.error.lastName.length"),
            nonEmptyString("lastName", "leadtrustee.individual.name.error.lastName.required"),
            regexp(Validation.nameRegex, "leadtrustee.individual.name.error.lastName.invalid")
          )
        )
    )(Name.apply)(Name.unapply)
  ) }
