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

import models.{AllTrustees, LeadTrustee, LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, TrusteeIndividual, TrusteeOrganisation}
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddATrusteeViewHelper(trustees: AllTrustees)(implicit messages: Messages) {

  private def render(trustee : (Trustee, Int)) : AddRow = {

    trustee match {
      case (trusteeInd: TrusteeIndividual, index) =>
        AddRow(
          name = trusteeInd.name.displayName,
          typeLabel = messages(s"entities.trustee.individual"),
          changeUrl = "#",
          removeUrl = controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(index).url
        )
      case (trusteeOrg : TrusteeOrganisation, index) =>
        AddRow(
          name = trusteeOrg.name,
          typeLabel = messages(s"entities.trustee.organisation"),
          changeUrl = "#",
          removeUrl = "#"
        )
    }
  }

  private def renderLead(lead : Option[LeadTrustee]) : List[AddRow] = {

    lead match {
      case Some(leadInd: LeadTrusteeIndividual) =>
        List(AddRow(
          name = leadInd.name.displayName,
          typeLabel = messages(s"entities.leadtrustee.individual"),
          changeUrl = controllers.leadtrustee.routes.CheckDetailsController.onPageLoad().url,
          removeUrl = "#"
        ))
      case Some(leadIOrg: LeadTrusteeOrganisation) =>
        List(AddRow(
          name = leadIOrg.name,
          typeLabel = messages(s"entities.leadtrustee.organisation"),
          changeUrl = controllers.leadtrustee.routes.CheckDetailsController.onPageLoad().url,
          removeUrl = "#"
        ))
      case _ => Nil
    }
  }

  def rows : AddToRows = {

    val complete = renderLead(trustees.lead) ++ trustees.trustees.zipWithIndex.map(render)

    AddToRows(Nil, complete)
  }

}
