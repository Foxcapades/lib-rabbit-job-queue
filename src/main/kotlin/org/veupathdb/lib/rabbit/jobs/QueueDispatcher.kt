package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import org.veupathdb.lib.rabbit.jobs.fn.ErrorHandler
import org.veupathdb.lib.rabbit.jobs.fn.SuccessHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.ErrorHandlers
import org.veupathdb.lib.rabbit.jobs.pools.SuccessHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json

/**
 * Job dispatcher.
 */
class QueueDispatcher : QueueWrapper {
  private val errorHandlers = ErrorHandlers()

  private val successHandlers = SuccessHandlers()

  constructor(config: QueueConfig): super(config)

  constructor(action: QueueConfig.() -> Unit): super(action)

  /**
   * Registers a callback to be executed on job success notification.
   *
   * @param fn Success callback.
   */
  fun onSuccess(fn: SuccessHandler) {
    successHandlers.register(fn)
  }

  /**
   * Registers a callback to be executed on job failure notification.
   *
   * @param fn Error callback.
   */
  fun onError(fn: ErrorHandler) {
    errorHandlers.register(fn)
  }

  /**
   * Dispatches a new job on the job queue.
   *
   * @param job Job definition.
   */
  fun dispatch(job: JobDispatch) {
    withDispatchQueue { publish(dispatchQueueName, job) }
  }

  override fun initCallbacks() {
    withErrorQueue {
      basicConsume(
        errorQueueName,
        false,
        DeliverCallback { _, msg ->
          workers.execute {
            errorHandlers.execute(ErrorNotification.fromJson(Json.from(msg.body)))
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
          workers.execute {
            successHandlers.execute(SuccessNotification.fromJson(Json.from(msg.body)))
          }
        },
        CancelCallback { }
      )
    }
  }
}