package com.example

import akka.actor.{Actor, ActorLogging, Props}
import com.example.ShardWorker.{ShardSize, Value}

import scala.collection.immutable.HashMap

/**
  * Actor representing a worker, that handles a single shard, for the sharded
  * key-value store.
  */
object ShardWorker {
  val props = Props[ShardWorker]

  /////////////////////////////////////////////////////////////////////////////
  // Messages defined for the shard worker.
  // ///////////////////////////////////////////////////////////////////////////
  case class Value(key: String, value: Option[String])
  case class ShardSize(n: Int)
}


class ShardWorker extends Actor with ActorLogging {
  import ShardedKvStore._

  // Shard worker state: an immutable hash map from String to String
  var kvStore = new HashMap[String, String]()

  /**
    * Handle the messages Put, Delete, Get and GetSize.  This should be pretty
    * similar to what you did in the homework assignment for week 1.
    * @return
    */
  // TODO:  Implement receive behavior for a shard worker
  //        Be mindful of who your sender is for the messages that mandate
  //        replies.
  override def receive: Receive = {
    case Put(k, v) =>
    case Delete(k) =>
    case Get(k) =>
    case GetSize =>
  }
}
