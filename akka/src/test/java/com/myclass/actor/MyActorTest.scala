package com.myclass.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.TestActorRef
import com.myclass.request.{GetRequest, SetRequest}
import com.myclass.utils.implicitUtils.TestActorRefAdvance
import org.junit.{Assert, Test}

class MyActorTest{
  // implicit sender
  implicit val defaultSender: ActorRef = Actor.noSender

  // 1.create actor system
  implicit val system: ActorSystem = ActorSystem()

  // 2.create actorRef for test
  val actorRef: TestActorRef[MyAkkaActor] = TestActorRef(system.actorOf(Props(classOf[MyAkkaActor])))

  @Test
  def testAkkaActor(): Unit = {
    // 3.tell message
    actorRef.tell(SetRequest("key", "value"))
    // 4.get akka actor
    val myAkkaActor: MyAkkaActor = actorRef.underlyingActor
    // 5.get key value
    val value: Object = myAkkaActor.cache.get("key")
    // 6.assert value
    Assert.assertEquals("value", value)
  }

  @Test
  def testSetRequest(): Unit = {
    actorRef.tell(SetRequest("key", "value"))
  }


  @Test
  def testGetRequest(): Unit = {
    actorRef.tell(SetRequest("name", "jack"))
    actorRef.tell(GetRequest("name"))
  }




}
