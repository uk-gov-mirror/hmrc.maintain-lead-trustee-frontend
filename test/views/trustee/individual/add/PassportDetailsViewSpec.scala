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

package views.trustee.individual.add

import controllers.trustee.individual.add.routes
import forms.PassportDetailsFormProvider
import models.{Name, Passport}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.behaviours.QuestionViewBehaviours
import views.html.trustee.individual.add.PassportDetailsView

class PassportDetailsViewSpec extends QuestionViewBehaviours[Passport] {

  val messageKeyPrefix = "trustee.individual.passportDetails"
  val name: Name = Name("First", Some("Middle"), "Last")

  override val form: Form[Passport] = new PassportDetailsFormProvider().withPrefix(messageKeyPrefix)

  "PassportDetails view" must {

    val view = viewFor[PassportDetailsView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    "fields" must {

      behave like pageWithPassportOrIDCardDetailsFields(
        form,
        applyView,
        messageKeyPrefix,
        routes.PassportDetailsController.onSubmit().url,
        Seq(("country", None), ("number", None)),
        "expiryDate",
        name.displayName
      )
    }

    behave like pageWithASubmitButton(applyView(form))
  }
}
