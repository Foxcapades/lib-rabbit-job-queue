package org.veupathdb.lib.rabbit.jobs.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ExecutorConfig {
  /**
   * Configures the number of job execution worker threads.
   */
  var workers: Int = 5
    set(value) {
      if (value < 1)
        throw IllegalArgumentException("cannot set worker count to a value less than 1")

      field = value
    }

  /**
   * Max allowed execution time for a single job.
   *
   * If a job exceeds this time it will be forceably terminated.
   */
  var maxJobExecutionTime: Duration = 15.minutes

  /**
   * Failure policy defining the circumstances in which the target
   * [JobQueueExecutor][org.veupathdb.lib.rabbit.jobs.JobQueueExecutor] should
   * shut down without attempting recovery.
   */
  var failurePolicy: ExecutorFailurePolicy? = null

  internal fun getOrCreateFailureEnforcer() =
    failurePolicy?.newEnforcer() ?: WindowedFailureEnforcer((workers * 2).toUInt(), 2.seconds)
}