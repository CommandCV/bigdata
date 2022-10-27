package com.myclass.demo.helloworld

import akka.actor.{Actor, ActorSystem, Props}

object HelloWorldActorMain extends App {
  // 启动actor system
  val actorSystem: ActorSystem = ActorSystem()

  // 注册hello world actor（注意这里actor不能通过new的方式去创建，只能通过actorOf的方式去创建，否则会抛出异常）
  // 第一种创建方式通过props指定actor具体类型创建
//  val actor = actorSystem.actorOf(Props.create(classOf[HelloWorldActor]))
  // 也可以通过这种方式去写
  val actor = actorSystem.actorOf(Props(classOf[HelloWorldActor]))
  // 第二种创建的时候指定参数


  // 给actor发送消息
  actor.tell("test", Actor.noSender)
}
