package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import org.veupathdb.lib.rabbit.jobs.fn.JobHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json

class QueueWorker : QueueWrapper {
  private val handlers = JobHandlers()

  constructor(config: QueueConfig): super(config)

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