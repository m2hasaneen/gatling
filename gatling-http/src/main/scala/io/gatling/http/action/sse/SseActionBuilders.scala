/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.action.sse

import akka.actor.ActorDSL._
import akka.actor.ActorRef
import io.gatling.core.config.Protocols
import io.gatling.core.session.Expression
import io.gatling.http.action.HttpActionBuilder
import io.gatling.http.check.sse.SseCheck
import io.gatling.http.check.ws.WsCheckBuilder
import io.gatling.http.request.builder.sse.SseGetRequestBuilder

class SseGetActionBuilder(
    requestName: Expression[String],
    sseName: String,
    requestBuilder: SseGetRequestBuilder,
    check: Option[SseCheck] = None) extends HttpActionBuilder {

  def check(check: SseCheck) = new SseGetActionBuilder(requestName, sseName, requestBuilder, Some(check))

  def check(wsCheckBuilder: WsCheckBuilder): SseGetActionBuilder = {
    val wsCheck = wsCheckBuilder.build
    check(SseCheck(wsCheck, wsCheck.blocking, wsCheck.timeout, wsCheck.expectation))
  }

  override def build(next: ActorRef, protocols: Protocols): ActorRef = {
    val request = requestBuilder.build(httpProtocol(protocols))
    val protocol = httpProtocol(protocols)

    actor(new SseGetAction(requestName, sseName, request, check, next, protocol))
  }
}

class SseReconciliateActionBuilder(requestName: Expression[String], sseName: String) extends HttpActionBuilder {

  override def build(next: ActorRef,
                     protocols: Protocols): ActorRef = actor(new SseReconciliateAction(requestName, sseName, next))
}

class SseCloseActionBuilder(requestName: Expression[String], sseName: String) extends HttpActionBuilder {

  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(new SseCloseAction(requestName, sseName, next))
}
