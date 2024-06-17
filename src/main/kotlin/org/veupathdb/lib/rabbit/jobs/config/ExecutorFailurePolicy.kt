package org.veupathdb.lib.rabbit.jobs.config

import kotlin.time.Duration

enum class ExecutorFailureResponse {
  GracefulStop,
  ImmediateStop,
}

/**
 * Defines a policy that itself defines the state or point at which an executor
 * should be considered "failed" and shut down.
 *
 * This type is effectively a factory for [ExecutorFailureEnforcer] instances
 * which will individually be used to enforce the rules of the policy.
 *
 * @author Elizabeth Paige Harper [foxcapades.io@gmail.com]
 * @since 2.0.0
 */
fun interface ExecutorFailurePolicy {
  /**
   * Creates a new [ExecutorFailureEnforcer] instance.
   */
  fun newEnforcer(): ExecutorFailureEnforcer

  companion object {
    /**
     * Defines a new failure policy that considers an executor as failed when
     * channels or consumers are unexpectedly killed more than [max] times
     * within the time window defined by [within].
     *
     * This may be used to catch instances where consumers are spinning up and
     * failing immediately, possibly indicating a persistent issue that will not
     * be resolved automatically.
     *
     * @param max Max permissible number of times consumers may be unexpectedly
     * killed within the defined time window.
     *
     * @param within Defines the time window within which at most [max]
     * unexpected consumer deaths may occur.
     *
     * @return A new [ExecutorFailurePolicy] instance.
     */
    fun maxFailuresWithin(max: Int, within: Duration) = ExecutorFailurePolicy { WindowedFailureEnforcer(max.toUInt(), within) }

    /**
     * Defines a new failure policy that considers an executor as failed when
     * channels or consumers are unexpectedly killed more than [max] times total
     * within the lifespan of the executor.
     *
     * @param max Max permissible number of times consumers may be unexpectedly
     * killed.
     *
     * @return A new [ExecutorFailurePolicy] instance.
     */
    fun maxTotalFailures(max: Int) = ExecutorFailurePolicy { MaxFailureEnforcer(max.toUInt()) }

    /**
     * Defines a new failure policy that wraps other policies and considers an
     * executor as failed when any of the sub policies consider the executor as
     * failed.
     *
     * @param others Policies to apply.
     *
     * @return A new [ExecutorFailurePolicy] instance.
     */
    fun ofAny(vararg others: ExecutorFailurePolicy) = ExecutorFailurePolicy { AnyFailureEnforcer(others) }
  }
}

