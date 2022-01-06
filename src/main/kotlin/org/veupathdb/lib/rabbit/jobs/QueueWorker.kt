package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import org.veupathdb.lib.rabbit.jobs.fn.JobHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json

/**
 * Job executor end of the job queue.
 */
class QueueWorker : QueueWrapper {
  private val handlers = JobHandlers()

  /**
   * Instantiates a new QueueWorker based on the given configuration.
   *
   * @param config Configuration for the RabbitMQ connections.
   */
  constructor(config: QueueConfig): super(config)

  /**
   * Instantiates a new QueueWorker using the given action to configure the
   * RabbitMQ connections.
   *
   * @param action Action used to configure the RabbitMQ connections.
   */
  constructor(action: QueueConfig.() -> Unit): super(action)

  /**
   * Registers a callback to be executed when a new job is submitted to the
   * queue.
   *
   * @param fn Job request callback.
   */
  fun onJob(fn: JobHandler) {
    handlers.register(fn)
  }

  /**
   * Sends an error notification to the job dispatcher to alert it that a
   * submitted job has failed.
   *
   * @param err Error notification to send.
   */
  fun sendError(err: ErrorNotification) {
    withErrorQueue { publish(errorQueueName, err) }
  }

  /**
   * Sends a success notification to the job dispatcher to alert it that a
   * submitted job has succeeded.
   *
   * @param msg Success notification to send.
   */
  fun sendSuccess(msg: SuccessNotification) {
    withSuccessQueue { publish(successQueueName, msg) }
  }

  /**
   * Initializes the job queue callback.
   */
  override fun initCallbacks() {
    withDispatchQueue {
      basicConsume(
        dispatchQueueName,
        true,
        DeliverCallback { _, msg ->
          workers.execute {
            handlers.execute(JobDispatch.fromJson(Json.from(msg.body)))
          }
        },
        CancelCallback { }
      )
    }
  }
}