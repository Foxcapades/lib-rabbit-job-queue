@file:JvmName("Main")

import com.fasterxml.jackson.databind.node.TextNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.JobQueueDispatcher
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch

fun main() {
  println("Sleeping for 10 seconds...")
  Thread.sleep(10_000)

  val conFac = JobQueueDispatcher {}

  conFac.onSuccess {
    println("Success from client: $it")
  }

  conFac.onError {
    print("Error from client: $it")
  }

  for (i in 1 .. 15) {
    conFac.dispatch(JobDispatch(
      HashID("01020304050607080102030405060701"),
      TextNode("foo $i"),
      "something",
    ))
  }
}