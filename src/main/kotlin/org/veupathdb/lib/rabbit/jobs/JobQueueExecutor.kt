package org.veupathdb.lib.rabbit.jobs

import org.slf4j.LoggerFactory
import org.veupathdb.lib.rabbit.jobs.config.QueueConfig
import org.veupathdb.lib.rabbit.jobs.fn.JobHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.exec.ExecutorPoolConfig
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import org.veupathdb.lib.rabbit.jobs.pools.exec.JobQueueExecutorPool

/**
 * Job execution end of the job queue.
 */
class JobQueueExecutor : QueueWrapper {

  private val Log = LoggerFactory.getLogger(javaClass)

  private val handlers = JobHandlers()

  private val executorPool: JobQueueExecutorPool

  /**
   * Instantiates a new QueueWorker based on the given configuration.
   *
   * @param config Configuration for the RabbitMQ connections.
   */
  constructor(config: QueueConfig): super(config) {
    executorPool = JobQueueExecutorPool(ExecutorPoolConfig(
      channelProvider = ::dispatchQueue,
      queueName = config.jobQueueName,
      handlers = handlers,
      poolSize = config.workers,
      maxJobTime = config.maxJobExecutionTime,
      threadFactory = null,
      failureChecker = config.executorConfig.getOrCreateFailureEnforcer(),
      shutdownCB = ::abort,
    ))

    executorPool.start()
  }

  /**
   * Instantiates a new QueueWorker using the given action to configure the
   * RabbitMQ connections.
   *
   * @param action Action used to configure the RabbitMQ connections.
   */
  constructor(action: QueueConfig.() -> Unit): super(action) {
    executorPool = JobQueueExecutorPool(ExecutorPoolConfig(
      channelProvider = ::dispatchQueue,
      queueName = config.jobQueueName,
      handlers = handlers,
      poolSize = config.workers,
      maxJobTime = config.maxJobExecutionTime,
      threadFactory = null,
      failureChecker = config.executorConfig.getOrCreateFailureEnforcer(),
      shutdownCB = ::abort,
    ))

    executorPool.start()
  }

  /**
   * Registers a callback to be executed when a new job is submitted to the
   * queue.
   *
   * @param fn Job request callback.
   */
  fun onJob(fn: JobHandler) {
    Log.debug("registering job handler {}", fn)
    handlers.register(fn)
  }

  /**
   * Sends an error notification to the job dispatcher to alert it that a
   * submitted job has failed.
   *
   * @param err Error notification to send.
   */
  fun sendError(err: ErrorNotification) {
    Log.debug("sending error notification {}", err)
    withErrorQueue { publish(errorQueueName, err) }
  }

  /**
   * Sends a success notification to the job dispatcher to alert it that a
   * submitted job has succeeded.
   *
   * @param msg Success notification to send.
   */
  fun sendSuccess(msg: SuccessNotification) {
    Log.debug("sending success notification {}", msg)
    withSuccessQueue { publish(successQueueName, msg) }
  }

  fun shutdown(blocking: Boolean = true) {
    executorPool.stop(blocking)
  }

  private fun abort() {
    Log.info("closing connection to RabbitMQ")
    connection.abort()
  }
}