@file:JvmName("Main")

import com.fasterxml.jackson.databind.node.TextNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.QueueDispatcher
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch

fun main() {
  println("Sleeping for 10 seconds...")
  Thread.sleep(10_000)

  val conFac = QueueDispatcher {}

  conFac.onSuccess {
    println("Client Success: $it")
  }

  conFac.onError {
    print("Client Error: $it")
  }

  conFac.dispatch(JobDispatch(
    HashID("01020304050607080102030405060708"),
    "something",
    TextNode("foo")
  ))
}