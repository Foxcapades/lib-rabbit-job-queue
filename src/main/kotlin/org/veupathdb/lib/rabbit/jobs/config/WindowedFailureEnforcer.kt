package org.veupathdb.lib.rabbit.jobs.config

import org.veupathdb.lib.rabbit.jobs.utils.ScrollingCounter
import kotlin.time.Duration

internal class WindowedFailureEnforcer(
  private val maxFailures: UInt,
  private val within: Duration,
) : ExecutorFailureEnforcer {
  private val counter = ScrollingCounter(within)

  override fun markFailure() = counter.inc()
  override fun shouldHalt() = counter > maxFailures
  override fun reason() = "process exceeded $maxFailures within $within"
}