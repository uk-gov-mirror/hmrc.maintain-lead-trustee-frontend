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

package utils

import models.TrusteeType
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddATrusteeViewHelper(trustees: List[TrusteeType])(implicit messages: Messages) {

  private def render(trustee : (TrusteeType, Int)) : AddRow = {

    trustee match {
      case (TrusteeType(Some(trusteeInd), None), index) =>
        AddRow(
          name = trusteeInd.name.toString,
          typeLabel = messages(s"entities.trustee.individual"),
          changeUrl = "#",
          removeUrl = controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(index).url
        )
      case (TrusteeType(None, Some(trusteeOrg)), index) =>
        AddRow(
          name = trusteeOrg.name,
          typeLabel = messages(s"entities.trustee.organisation"),
          changeUrl = "#",
          removeUrl = controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(index).url
        )
    }
  }

  def rows : AddToRows = {

    val complete = trustees.zipWithIndex.map(render)

    AddToRows(Nil, complete)
  }

}
