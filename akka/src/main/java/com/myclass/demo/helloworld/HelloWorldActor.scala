package com.myclass.demo.helloworld

import akka.actor.Actor

class HelloWorldActor extends Actor {

  /**
   * 继承Actor类，实现对应的receive方法，通过该方法实现消息的处理
   */
  override def receive: Receive = {
    // 匹配任意值，输出hello world
    case _: Any => println("hello world")
  }
}
