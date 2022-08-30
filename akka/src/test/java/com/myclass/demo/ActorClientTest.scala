package com.myclass.demo

import java.util.concurrent.TimeUnit

import akka.actor.Status
import com.myclass.exception.KeyNotFoundException
import org.junit.{Assert, Test}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ActorClientTest{

  private val client = new ActorClient("127.0.0.1:25520")

  @Test
  def setTest(): Unit = {
    val future: Future[Any] = client.set("name", "jack")
    val result = Await.result(future, Duration.create(10, TimeUnit.SECONDS))
    Assert.assertEquals(Status.Success, result)
  }

  @Test
  def getTest(): Unit = {
    setTest()
    val future: Future[Any] = client.get("name")
    val result: Any = Await.result(future, Duration.create(10, TimeUnit.SECONDS))
    Assert.assertEquals("jack", result.asInstanceOf[Some[String]].get)
  }

  @Test(expected = classOf[KeyNotFoundException])
  def getTestWithException(): Unit = {
    val future: Future[Any] = client.get("key")
    val result = Await.result(future, Duration.create(10, TimeUnit.SECONDS))
    Assert.assertEquals(Status.Failure, result)
  }

}
