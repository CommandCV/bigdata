package com.myclass.actor

import akka.actor.{Actor, ActorRef, Status}
import com.myclass.exception.KeyNotFoundException
import com.myclass.request.{GetRequest, SetRequest}
import com.myclass.utils.implicitUtils.ActorRefAdvance
import org.slf4j.{Logger, LoggerFactory}

class MyAkkaActor extends Actor {

  val log: Logger = LoggerFactory.getLogger(classOf[MyAkkaActor])

  implicit val defaultSender: ActorRef = Actor.noSender

  val cache = new java.util.HashMap[String, Object]()

  override def receive: Receive = {
    case SetRequest(key, value) =>
      log.info(s"receive SetRequest, key: $key, value: $value")
      cache.put(key, value)
      sender().tell(Status.Success)
      // actorRef.tell(msg, sender) method equals actorRef!msg
      // the actorRef!msg used to Actor.noSender
    case GetRequest(key) =>
      log.info(s"receive GetRequest, key: $key")
      val value = cache.get(key)
      if (value != null) sender().tell(value)
      else sender().tell(Status.Failure(new KeyNotFoundException(key)))
    case _ =>
      log.error("unknown request type")
      sender().tell(Status.Failure)
  }
}
