package com.myclass.demo.helloworld

import akka.actor.Actor

class PrintMessageActor(messageParam: String = null) extends Actor {

  val message: String = messageParam

  /**
   * 继承Actor类，实现对应的receive方法，通过该方法实现消息的处理
   */
  override def receive: Receive = {
    // 匹配任意值，输出message
    case _: Any => println(message)
  }
}
