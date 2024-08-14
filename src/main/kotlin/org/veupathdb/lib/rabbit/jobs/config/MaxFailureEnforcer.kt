package org.veupathdb.lib.rabbit.jobs.config

internal class MaxFailureEnforcer(private val maxFailures: UInt) : ExecutorFailureEnforcer {
  private var failureCount = 0u

  override fun markFailure() { failureCount++ }
  override fun shouldHalt() = failureCount > maxFailures
  override fun reason() = "process exceeded $maxFailures total failures"
}