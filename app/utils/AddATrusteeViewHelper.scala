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

import models.IndividualOrBusiness.Individual
import models.TrusteeStatus.{Completed, InProgress}
import models.{IndividualOrBusiness, RemoveTrusteeIndividual, RemoveTrusteeOrg, TrusteeType, UserAnswers}
import play.api.i18n.Messages
import sections.{LeadTrusteeIndividual, Trustees}
import viewmodels.addAnother.{AddRow, AddToRows, LeadTrusteeIndividualViewModel, TrusteeViewModel}

class AddATrusteeViewHelper(trustees: List[TrusteeType])(implicit messages: Messages) {

  private def render(trustee : TrusteeType) : AddRow = {

    trustee match {
      case TrusteeType(Some(trusteeInd), None) =>
        AddRow(
          name = trusteeInd.name.toString,
          typeLabel = messages(s"entities.trustee.individual"),
          changeUrl = "#",
          removeUrl = controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(0).url
        )
      case TrusteeType(None, Some(trusteeOrg)) =>
        AddRow(
          name = trusteeOrg.name,
          typeLabel = messages(s"entities.trustee.organisation"),
          changeUrl = "#",
          removeUrl = controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(0).url
        )
    }

  }

//  private def renderLeadIndividual(leadIndividual : LeadTrusteeIndividualViewModel) : AddRow = {
//
//    val nameOfTrustee = leadIndividual.name.getOrElse(messages("entities.no.name.added"))
//
//    val trusteeType = leadIndividual.`type` match {
//      case Some(_ : IndividualOrBusiness) =>
//        s"${messages("entities.lead")} ${messages("entities.trustee.individual")}"
//      case None =>
//        s"${messages("entities.trustee")}"
//    }
//
//    val removeLink = leadIndividual.`type` match {
//      case Some(Individual) =>
//        controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(0).url
//      case _ =>
//        controllers.trustee.routes.RemoveIndividualTrusteeController.onPageLoad(0).url
//    }
//
//    AddRow(
//      name = nameOfTrustee,
//      typeLabel = trusteeType,
//      changeUrl = "#",
//      removeUrl = removeLink
//    )
//  }

  def rows : AddToRows = {

//    val leadIndividual = userAnswers.get(LeadTrusteeIndividual).toList

//    val trustees = userAnswers.get(Trustees).toList.flatten.zipWithIndex

//    val complete = leadIndividual.filter(_.status == Completed).map(renderLeadIndividual) ++ trustees.filter(_._1.status == Completed).map(render)
//
//    val inProgress = leadIndividual.filter(_.status == InProgress).map(renderLeadIndividual) ++ trustees.filter(_._1.status == InProgress).map(render)

    val complete = trustees.map(render)

    val inProgress = trustees.map(render)

    AddToRows(inProgress, complete)
  }

}
