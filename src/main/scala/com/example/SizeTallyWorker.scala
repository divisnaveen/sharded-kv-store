package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.ShardedKvStore.GetSize
import com.example.SizeTallyWorker.GatherSizes

/**
  * <p>Actor to which the sharded KV store can delegate the work of querying
  * all of the shard workers for their sizes, collect the results, sum them,
  * and return them to whoever asked.  Since collecting sizes from multiple
  * actors, some of which may take longer than others to respond, we don't
  * want to be tying up and blocking the behavior of the main gatekeeper
  * actor to the store.  Thus we delegate.</p>
  *
  * <p>As the course goes on, we'll see patterns involving Futures for
  * maintaining asynchronous, non blocking behavior, but without introducing
  * too much contortion in our otherwise sequential looking code.</p>
  */

object SizeTallyWorker {
  val props = Props[SizeTallyWorker]

  ////////////////////////////////////////////////////////////////////////////
  // Messages provided by the SizeTallyWorker
  ////////////////////////////////////////////////////////////////////////////
  case class GatherSizes(shardWorkers: scala.collection.immutable.Set[ActorRef])
}


class SizeTallyWorker extends Actor with ActorLogging {
  import ShardWorker.ShardSize
  import ShardedKvStore.Size

  // NOTE: Once again a var ref to an immutable collection
  var shardWorkers: scala.collection.immutable.Set[ActorRef] = Set.empty

  // Interested party refers to the actor who wants to receive the final tally
  var interestedParty: ActorRef = null

  /**
    * The initial receive behavior of the actor must, in response to a
    * GatherSizes message, perform the following tasks:
    * <ul>
    *   <li>Remember the set of shard workers from whom it must gather sizes.
    *   </li>
    *   <li>Remember the actor that wants to hear the final tally.
    *   </li>
    *   <li>For each shard worker, tell the shard worker to report the size of
    *   its key-value map.
    *   </li>
    *   <li>Change to the waitForTallies behavior once the above work is done.
    *   </li>
    * </ul>
    * @return
    */
  // TODO: Implement receive method according to above comment's specification.
  override def receive: Receive = {

    case GatherSizes(workers) =>
  }

  /**
    * The waitforTallies behavior uses the "asynchronous tail recursion"
    * pattern to stash away the state that's uniquely associated with it.
    *
    * In response to a ShardSize message this method must:
    * <ul>
    *   <li>Reduce the number of shards it's waiting to hear from by one.</li>
    *   <li>Update the size so far to reflect the returned shard size.</li>
    *   <li>If the number of outstanding shards has fallen to zero,
    *   notify the interested party with a Size message bearing the accumulated
    *   sizes and stop the tally worker actor.</li>
    *   <li>If the number of outstanding shards has not fallen to zero,
    *   we become into the waitForTallies beahvior with the numShards and
    *   sizeAccum parameters updated to reflect the ShardSize message we
    *   received.</li>
    * </ul>
    *
    * <p><b>NOTE:</b> As specified above, the tally worker actor would not be
    * production suitable. In particular, what if one of the shard workers has
    * failed sometime after we were given the list of workers to accumulate
    * but before it reported a size. In such a case, the count of outstanding
    * shard workers we're waiting on wouldn't decrement enough and we'd never
    * reach the state at which we inform the interested party and shut
    * ourselves down.</p>
    *
    * <p>
    * To solve that problem we'd want to do two things:
    * <ol>
    *   <li>Restarting failed shard workers automatically would help. Recall
    *   that the ActorRef remains valid between resurrections of the Actor,
    *   something we'll understand further in week 3 when we talk about
    *   the actor lifecycle.</li>
    *   <li>Implement some kind of timeout behavior where if we haven't heard
    *   from all of the shard workers after the accursed and nefarious
    *   and mythical "reasonable timeout" we are willing to shut ourselves down.
    *   We might return an incomplete count to the interested party, perhaps
    *   with a notification that it's incomplete, or maybe an outright error.
    *   Later in the course we'll see that with distributed data structures
    *   one often prefers incomplete, or eventually consisted answers to
    *   perfect truth both because things can fail, and because seeking
    *   absolute exact correctness can be more expensive than it is
    *   valuable.</li>
    * </ol>
    * </p>
    *
    * @param numShards The number of shard workers from which we're waiting for
    *                  tallies.
    * @param sizeAccum An accumulator parameter that will be used to keep a
    *                  running sum of the returned tallies.
    * @return
    *
    *
    */
  // TODO: Implement the waitForTallies behavior as above
  def waitForTallies(numShards: Int, sizeAccum: Int): Receive = {

    case ShardSize(n) =>
  }

}
