@file:JvmName("Main")

import org.veupathdb.lib.rabbit.jobs.JobQueueExecutor
import org.veupathdb.lib.rabbit.jobs.config.ExecutorFailurePolicy
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import kotlin.time.Duration.Companion.seconds

fun main() {
  println("Sleeping for 10 seconds...")
  Thread.sleep(10_000)

  val conFac = JobQueueExecutor {
    executorFailurePolicy = ExecutorFailurePolicy.maxTotalFailures(1)
    workers = 1
    maxJobExecutionTime = 15.seconds
  }

  conFac.onJob {
    println("Server said: $it")

    Thread.sleep(20.seconds.inWholeMilliseconds)

    conFac.sendSuccess(SuccessNotification(it.jobID))
  }
}