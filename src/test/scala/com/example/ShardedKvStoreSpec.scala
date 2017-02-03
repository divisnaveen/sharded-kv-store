package com.example

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.example.ShardWorker.Value
import com.example.ShardedKvStore._
import org.scalatest._
import scala.concurrent.duration._

// NOTE In testing lecture go over WordSpecLike vs FunSuiteLike
class ShardedKvStoreSpec(_system: ActorSystem) extends TestKit(_system)
                                                with WordSpecLike
                                                with MustMatchers
                                                with BeforeAndAfterAll
                                                with ImplicitSender {

  def this() = this(ActorSystem("SharedKvStoreSpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A KvStore actor" must {
    val kvStoreActor = system.actorOf(ShardedKvStore.props)

    kvStoreActor ! Initialize(3)

    "not return values for a missing key" in {
      kvStoreActor ! Get("fooKey")
      expectMsg(Value("fooKey", None))
    }
    "return a value previously put into the store" in {
      kvStoreActor ! Put("foo", "bar")
      kvStoreActor ! Get("foo")
      expectMsg(Value("foo", Some("bar")))
    }
    "correctly remove a mapping present in the store" in {
      kvStoreActor ! Delete("foo")
      kvStoreActor ! Get("foo")
      expectMsg(Value("foo", None))
    }
    "gracefully handle attempts to delete a key absent from the store" in {
      kvStoreActor ! Delete("baz")
      expectNoMsg
    }
    "return the total size of all shards" in {
      kvStoreActor ! Put("wa", "za")
      kvStoreActor ! Put("na", "ba")
      kvStoreActor ! GetSize
      expectMsg(Size(2))
    }
  }
}
