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

package views.trustee.individual

import controllers.trustee.individual.routes
import forms.NonUkAddressFormProvider
import models.{Name, NonUkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.behaviours.NonUkAddressViewBehaviours
import views.html.trustee.individual.NonUkAddressView

class NonUkAddressViewSpec extends NonUkAddressViewBehaviours {

  val messageKeyPrefix = "trustee.individual.nonUkAddress"
  val index = 0
  val name: Name = Name("First", Some("Middle"), "Last")

  override val form: Form[NonUkAddress] = new NonUkAddressFormProvider().apply()

  "NonUkAddressView" must {

    val view = viewFor[NonUkAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, index, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like nonUkAddress(
      applyView,
      Some(messageKeyPrefix),
      routes.NonUkAddressController.onSubmit(index).url,
      name.displayName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}