package com.myclass.demo

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.Patterns
import akka.util.Timeout
import com.myclass.request.{DeleteRequest, GetRequest, SetIfNotExistsRequest, SetRequest}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

class ActorClient(remoteAddress: String) {

  private val config: Config = ConfigFactory.parseResources("application-25530.properties")
  implicit val system: ActorSystem = ActorSystem("actor-client", config)
  implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)
  val remoteActorSystemPath: String = s"akka://akka-server@$remoteAddress/user/actor-server"
  val remoteSystem: ActorSelection = system.actorSelection(remoteActorSystemPath)

  def set(key: String, value: String): Future[Any] = {
    Patterns.ask(remoteSystem, SetRequest(key, value), timeout)
  }

  def setIfNotExists(key: String, value: String): Future[Any] = {
    Patterns.ask(remoteSystem, SetIfNotExistsRequest(key, value), timeout)
  }

  def get(key: String): Future[Any] = {
    Patterns.ask(remoteSystem, GetRequest(key), timeout)
  }

  def delete(key: String): Future[Any] = {
    Patterns.ask(remoteSystem, DeleteRequest(key), timeout)
  }

}
