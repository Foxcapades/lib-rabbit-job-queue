package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.DeliverCallback
import org.veupathdb.lib.rabbit.jobs.fn.ErrorHandler
import org.veupathdb.lib.rabbit.jobs.fn.SuccessHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.ErrorHandlers
import org.veupathdb.lib.rabbit.jobs.pools.SuccessHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json

class QueueDispatcher : QueueWrapper {
  private val errorHandlers = ErrorHandlers()

  private val successHandlers = SuccessHandlers()

  constructor(config: QueueConfig): super(config)

  constructor(action: QueueConfig.() -> Unit): super(action)

  fun onSuccess(fn: SuccessHandler) {
    successHandlers.register(fn)
  }

  fun onError(fn: ErrorHandler) {
    errorHandlers.register(fn)
  }

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