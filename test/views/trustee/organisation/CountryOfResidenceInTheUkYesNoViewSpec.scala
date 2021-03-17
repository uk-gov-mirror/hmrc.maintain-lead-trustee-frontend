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

package views.trustee.organisation

import controllers.trustee.organisation.routes
import forms.YesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.trustee.organisation.CountryOfResidenceInTheUkYesNoView

class CountryOfResidenceInTheUkYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustee.organisation.countryOfResidenceInTheUkYesNo"
  val name = "Name"
  val mode = NormalMode

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "CountryOfResidenceInTheUkYesNoView view" must {

    val view = viewFor[CountryOfResidenceInTheUkYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, mode, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, Some(name), routes.CountryOfResidenceInTheUkYesNoController.onSubmit(mode).url)
  }
}