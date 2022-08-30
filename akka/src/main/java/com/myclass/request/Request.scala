package com.myclass.request

trait Request

case class SetRequest(key: String, value: Object) extends Request with Serializable
case class GetRequest(key: String) extends Request with Serializable