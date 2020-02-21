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

package controllers.trustee.individual.actions

import javax.inject.Inject
import models.requests.DataRequest
import pages.trustee.individual.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class NameRequiredAction (index: Int)(val executionContext: ExecutionContext, val messagesApi: MessagesApi)
  extends ActionTransformer[DataRequest, TrusteeNameRequest] with I18nSupport {

  override protected def transform[A](request: DataRequest[A]): Future[TrusteeNameRequest[A]] = {
    Future.successful(TrusteeNameRequest[A](request,
      request.userAnswers.get(NamePage(index))
        .map(_.displayName)
        .getOrElse(request.messages(messagesApi)("trusteeName.defaultText"))
    ))
  }
}

class TrusteeNameRequiredProvider @Inject()(implicit ec: ExecutionContext, messagesApi: MessagesApi) {

  def apply[T](index : Int) =
    new NameRequiredAction(index)(ec, messagesApi)
}