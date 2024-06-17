package org.veupathdb.lib.rabbit.jobs.config

/**
 * Executor Failure State Shutdown Enforcer
 *
 * Tracks failures and determines when an executor should be considered as
 * "failed" and shut down.
 *
 * @author Elizabeth Paige Harper [foxcapades.io@gmail.com]
 * @since 2.0.0
 */
interface ExecutorFailureEnforcer {
  /**
   * Called when a channel or consumer is killed due to an unhandled exception.
   */
  fun markFailure()

  /**
   * Called to test whether the containing executor should be halted according
   * to the rules defined in this enforcer.
   *
   * @return `true` if the executor should be halted, otherwise `false`.
   */
  fun shouldHalt(): Boolean

  /**
   * Returns the failure reason for this enforcer.
   *
   * This method will only be called after [shouldHalt] has returned `true`.
   *
   * @return A message describing the reason the executor is being halted.
   */
  fun reason(): String
}