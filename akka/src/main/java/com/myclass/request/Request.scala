package com.myclass.request

trait Request extends Serializable

case class SetRequest(key: String, value: Object) extends Request
case class SetIfNotExistsRequest(key: String, value: Object) extends Request
case class GetRequest(key: String) extends Request
case class DeleteRequest(key:String) extends Request