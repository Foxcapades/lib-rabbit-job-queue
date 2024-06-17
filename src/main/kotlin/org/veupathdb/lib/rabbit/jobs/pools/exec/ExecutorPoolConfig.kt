package org.veupathdb.lib.rabbit.jobs.pools.exec

import org.veupathdb.lib.rabbit.jobs.config.ExecutorFailureEnforcer
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import java.util.concurrent.ThreadFactory
import kotlin.time.Duration

/**
 * Configuration values for a [JobQueueExecutorPool] instance.
 *
 * @author Elizabeth Paige Harper [foxcapades.io@gmail.com]
 * @since 2.0.0
 */
internal data class ExecutorPoolConfig(
  /**
   * Function used to get new RabbitMQ `Channel` instances to bind newly created
   * [Executor]s to.
   */
  val channelProvider: ChannelProvider,

  /**
   * Name of the queue [Executor]s in the target pool should subscribe to.
   */
  val queueName: String,

  /**
   * Job execution handlers.
   */
  val handlers: JobHandlers,

  /**
   * Number of [Executor]s that should be kept in the target pool.
   */
  val poolSize: Int,

  /**
   * Max allowed job execution time.
   *
   * Jobs will be killed if they exceed this time limit.
   */
  val maxJobTime: Duration,

  /**
   * Thread factory to use in the [JobQueueExecutorPool]'s internal thread pool.
   *
   * If this value is set to null, a default thread factory will be used.
   */
  val threadFactory: ThreadFactory?,

  /**
   * Executor failure checker.
   *
   * Used to determine when the target executor pool has reached a 'failed'
   * state.
   */
  val failureChecker: ExecutorFailureEnforcer,

  /**
   * Shutdown callback.  Used to trigger a connection close on critical worker
   * pool failure.
   */
  val shutdownCB: () -> Unit,
)