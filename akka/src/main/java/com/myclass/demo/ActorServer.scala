package com.myclass.demo

import akka.actor.{ActorSystem, Props}
import com.myclass.actor.MyAkkaActor

object ActorServer extends App {

  // create and started actor system
  val actorSystem: ActorSystem = ActorSystem("akka-server")

  // create actor
  actorSystem.actorOf(Props(classOf[MyAkkaActor]), "actor-server")
  // create actor with router, provider the actor pool
//  actorSystem.actorOf(Props(classOf[MyAkkaActor]).withRouter(new RoundRobinPool(8)), "actor-server")

  // create actor with group, provider the actor path list
//  actorSystem.actorOf(new RoundRobinGroup(List[String]("/actor-server")).props())

  // create actor with group and supervisor strategy
//    actorSystem.actorOf(
//      Props(classOf[MyAkkaActor])
//            .withRouter(
//              new RoundRobinPool(8)
//                .withSupervisorStrategy(SupervisorStrategy.defaultStrategy)
//            ), "actor-server"
//    )
}
