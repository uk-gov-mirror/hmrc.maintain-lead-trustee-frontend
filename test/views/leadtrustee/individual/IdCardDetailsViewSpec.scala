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

package views.leadtrustee.individual

import forms.IdCardDetailsFormProvider
import models.PassportOrIdCardDetails
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.leadtrustee.individual.IdCardDetailsView

class IdCardDetailsViewSpec extends QuestionViewBehaviours[PassportOrIdCardDetails] {

  val messageKeyPrefix = "leadtrustee.individual.idCardDetails"

  val name = "Lead Trustee"

  val form = new IdCardDetailsFormProvider().withPrefix("leadtrustee")

  "IdCardDetailsView view" must {

    val view = viewFor[IdCardDetailsView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, name, Seq.empty)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    "date fields" must {

      behave like pageWithDateFields(
        form,
        applyView,
        messageKeyPrefix,
        "expiryDate",
        name.toString
      )
    }

    "text fields" must {

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        controllers.leadtrustee.individual.routes.IdCardDetailsController.onSubmit().url,
        "country", "number"
      )
    }

  }
}
