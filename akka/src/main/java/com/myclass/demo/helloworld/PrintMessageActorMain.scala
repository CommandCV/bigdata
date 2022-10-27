package com.myclass.demo.helloworld

import akka.actor.{Actor, ActorSystem, Props}

object PrintMessageActorMain extends App {
  // 启动actor system
  val actorSystem: ActorSystem = ActorSystem()

  // 注册actor
  val actor = actorSystem.actorOf(Props.create(classOf[PrintMessageActor], "hello world"), "my-actor")
  // 第二种创建的时候指定参数
//  val actor = actorSystem.actorOf(Props(classOf[PrintMessageActor], "hello world"))

  // 给actor发送消息
  actor.tell("test", Actor.noSender)
  println(actor.path)
}
