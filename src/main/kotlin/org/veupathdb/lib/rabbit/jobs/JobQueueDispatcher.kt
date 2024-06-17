package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import org.slf4j.LoggerFactory
import org.veupathdb.lib.rabbit.jobs.config.QueueConfig
import org.veupathdb.lib.rabbit.jobs.fn.ErrorHandler
import org.veupathdb.lib.rabbit.jobs.fn.SuccessHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.ErrorHandlers
import org.veupathdb.lib.rabbit.jobs.pools.SuccessHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Job dispatcher.
 */
class JobQueueDispatcher : QueueWrapper {

  private val Log = LoggerFactory.getLogger(javaClass)

  private val errorHandlers = ErrorHandlers()

  private val successHandlers = SuccessHandlers()

  private val workers: ExecutorService

  constructor(config: QueueConfig): super(config) {
    workers = Executors.newFixedThreadPool(config.workers)
    initCallbacks()
  }

  constructor(action: QueueConfig.() -> Unit): super(action) {
    workers = Executors.newFixedThreadPool(config.workers)
    initCallbacks()
  }

  /**
   * Registers a callback to be executed on job success notification.
   *
   * @param fn Success callback.
   */
  fun onSuccess(fn: SuccessHandler) {
    Log.debug("registering success handler {}", fn)
    successHandlers.register(fn)
  }

  /**
   * Registers a callback to be executed on job failure notification.
   *
   * @param fn Error callback.
   */
  fun onError(fn: ErrorHandler) {
    Log.debug("registering error handler {}", fn)
    errorHandlers.register(fn)
  }

  /**
   * Dispatches a new job on the job queue.
   *
   * @param job Job definition.
   */
  fun dispatch(job: JobDispatch) {
    Log.debug("dispatching job {}", job)
    withDispatchQueue { publish(dispatchQueueName, job) }
  }

  private fun initCallbacks() {
    withErrorQueue {
      basicConsume(
        errorQueueName,
        false,
        DeliverCallback { _, msg ->
          Log.debug("handling error message {}", msg.envelope.deliveryTag)
          workers.execute {
            try {
              errorHandlers.execute(ErrorNotification.fromJson(Json.from(msg.body)))
            } finally {
              Log.debug("acknowledging error message {}", msg.envelope.deliveryTag)
              basicAck(msg.envelope.deliveryTag, false)
            }
          }
        },
        CancelCallback { }
      )
    }

    withSuccessQueue {
      basicConsume(
        successQueueName,
        false,
        DeliverCallback { _, msg ->
          Log.debug("handling success message {}", msg.envelope.deliveryTag)
          workers.execute {
            try {
              successHandlers.execute(SuccessNotification.fromJson(Json.from(msg.body)))
            } finally {
              Log.debug("acknowledging success message {}", msg.envelope.deliveryTag)
              basicAck(msg.envelope.deliveryTag, false)
            }
          }
        },
        CancelCallback { }
      )
    }
  }
}