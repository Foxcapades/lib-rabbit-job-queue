@file:JvmName("Main")

import org.veupathdb.lib.rabbit.jobs.JobQueueExecutor
import org.veupathdb.lib.rabbit.jobs.config.ExecutorFailurePolicy
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import kotlin.time.Duration.Companion.minutes

fun main() {
  println("Sleeping for 10 seconds...")
  Thread.sleep(10_000)

  val conFac = JobQueueExecutor {
    executor {
      failurePolicy = ExecutorFailurePolicy.maxTotalFailures(1)
      workers = 1
      maxJobExecutionTime = 35.minutes
      jobTimeoutCallback = { println(it.body) }
    }
  }

  conFac.onJob {
    println("Server said: $it")

    Thread.sleep(40.minutes.inWholeMilliseconds)

    conFac.sendSuccess(SuccessNotification(it.jobID))
  }
}