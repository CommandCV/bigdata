package com.myclass.exception

class KeyNotFoundException(keyParam: String) extends Exception with Serializable {
  private val key: String = keyParam

  override def toString = s"key '$key' not found."
}
