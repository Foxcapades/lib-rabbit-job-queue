@file:JvmName("Main")

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.QueueWorker
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification

fun main() {
  println("Sleeping for 10 seconds...")
  Thread.sleep(10_000)

  val conFac = QueueWorker {}

  conFac.onJob {
    print("Server: ")
    println(it)
  }

  conFac.sendSuccess(SuccessNotification(HashID("0102030405060708090A0B0C0D0E0F10")))
  conFac.sendError(ErrorNotification(HashID("0102030405060708090A0B0C0D0E0F10"), 123, "butts"))
}