package com.myclass.demo.helloworld

import akka.actor.{ActorSystem, Props}

class HelloWorldServer {
  val actorSystem: ActorSystem = ActorSystem()
  actorSystem.actorOf(Props(classOf[HelloWorldActor]), "my-actor")

}
