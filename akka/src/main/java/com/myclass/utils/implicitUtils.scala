package com.myclass.utils

import akka.actor.{Actor, ActorRef, ActorSelection}
import akka.testkit.TestActorRef

object implicitUtils {

  implicit class TestActorRefAdvance(actorRef: TestActorRef[_ <: Actor]) {
    def tell(msg: Any)(implicit defaultSender: ActorRef = Actor.noSender): Unit = {
      val sender = if (defaultSender == null) Actor.noSender else defaultSender
      actorRef.tell(msg, sender)
    }
  }

  implicit class ActorRefAdvance(actorRef: ActorRef) {
    def tell(msg: Any)(implicit defaultSender: ActorRef = Actor.noSender): Unit = {
      val sender = if (defaultSender == null) Actor.noSender else defaultSender
      actorRef.tell(msg, sender)
    }
  }

  implicit class ActorSelectionRefAdvance(actorRef: ActorSelection) {
    def tell(msg: Any)(implicit defaultSender: ActorRef = Actor.noSender): Unit = {
      val sender = if (defaultSender == null) Actor.noSender else defaultSender
      actorRef.tell(msg, sender)
    }
  }
}
