package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.SizeTallyWorker.GatherSizes

/**
  * Akka Actor representing the world facing component of a distributed
  * key-value store.
  */
object ShardedKvStore {
  val props = Props[ShardedKvStore]

  /////////////////////////////////////////////////////////////////////////////
  // Messages for the key-value store's user facing protocol
  /////////////////////////////////////////////////////////////////////////////
  case class Initialize(numShards: Int)

  case class Put(key: String, value: String)
  case class Delete(key: String)
  case class Get(key: String)

  case object GetSize
  case class Size(size: Int)
}


class ShardedKvStore extends Actor with ActorLogging {
  import ShardedKvStore._

  var numShards = 0

  // NOTE: The var reference to an immutable collection idiom below...
  var shardWorkers: Vector[ActorRef] = scala.collection.immutable.Vector.empty

  /**
    * <p>Convert the hashcode of a String, used as a map key, to a bucket number
    * that corresponds to the index of one of the child actors we're using
    * as a shard worker.  The bucket number can then be used to look up the
    * ActorRef for the corresponding shard.</p>
    *
    * <p>NOTE: hashCode for java.lang.String returns an int, but the int can be
    *       negative!  Because of how most programming languages, including
    *       Scala, define the modulus ('%') operator, a negative number, modulo
    *       a positive modulus can be negative.  For example:</p>
    * <pre>
    *           -3 % 5 = -3
    * </pre>
    *
    * <p>In a more decent universe, the answer really ought to be 2, but
    * countless programming languages have defined mod as above since time
    * immemorial.</p>
    *
    * <p>Thus, to avoid getting nonsensical negative bucket numbers, I
    * suggest you kill the highest order bit of the hashCode, i.e. the
    * sign bit, before the modular reduction, so that you don't end up
    * with negative bucket numbers.</p>
    *
    * @param key String being used as a key in our KV-store.
    * @return Bucket number between 0 and numShards-1 of the shard worker
    *         for this key.
    */
  def bucketNumber(key: String): Int = {
    val system = context.actorOf(Props[ShardedKvStore], name = "sharedKvstore")
    system !
  } // TODO: Implement this method.

  /**
    * The receive method needs to:
    * <ul>
    *   <li>Handle the Initialize message by recording the number of shards
    *   requested, and creating the correct number of shard workers.</li>
    *   <li>Handle the Put message by sending a Put message to the shard worker
    *   handling the bucket associated with the hashCode of the key.</li>
    *   <li>Handle the Delete message by sending a Delete message to the
    *   shard worker handling the bucket associated with the hasCode of
    *   the key.</li>
    *   <li>Handle the Get message by <i>forwarding</i> the Get message
    *   to the shard worker handling that key. We forward because the Get
    *   message has a reply and we, the key value store aren't the one who
    *   cares about it, rather it's destined for whoever asked for it
    *   originally.</li>
    *   <li>Handle the GetSize method by creating a SizeTallyWorker and
    *   forwarding it a GatherSizes message identifying the set of shard workers
    *   from which to gather sizes.</li>
    * </ul>
    *
    * <b>NOTE:</b> The final case statement in the receive method can be a handy
    * trick for debugging interrelated actors that may mix both tell and forward
    * since it can be easy to get confused and do one when you meant to do the
    * other and thus send answers back to an unexpected place.  This gives you
    * a nice logging message during debugging and can be more convenient than
    * using the unhandled mechanism that we'll talk more about later.
    *
    * @return
    */
  // TODO: Handle messages according to the above protocol and semantics.
  override def receive: Receive = {

    case Initialize(n) => n += numShards

    case Put(key, value) => 

    case Delete(key) =>

    case Get(key) =>

    case GetSize =>

    // NOTE: We'll talk more about 'unhandled' later...
    case m@_ => log.warning(s"NARB! Unexpected message $m")
  }
}


// TODO: (.5 hour) Remarks in the homework description, things to think about:
//          - resiliency, what if shard workers die? Ans: supervisors to restart
//          - resiliency, data loss when shard workers die? Ans: persistence
//          - elasticity, what if needs grow over run and need more shards?
//              And: consistent hashing, esp. w/ the provided router for it.
