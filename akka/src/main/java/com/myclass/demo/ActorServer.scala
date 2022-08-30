package com.myclass.demo

import akka.actor.{ActorSystem, Props}
import com.myclass.actor.MyAkkaActor

object ActorServer extends App {

  // create and started actor system
  val actorSystem: ActorSystem = ActorSystem("akka-server")

  // create actor
  actorSystem.actorOf(Props(classOf[MyAkkaActor]), "actor-server")

}
