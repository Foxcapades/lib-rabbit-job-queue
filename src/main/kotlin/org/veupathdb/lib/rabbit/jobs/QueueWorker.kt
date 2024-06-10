package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConsumerShutdownSignalCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.ShutdownSignalException
import org.slf4j.LoggerFactory
import org.veupathdb.lib.rabbit.jobs.fn.JobHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Job executor end of the job queue.
 */
class QueueWorker : QueueWrapper {

  private val Log = LoggerFactory.getLogger(javaClass)

  private val handlers = JobHandlers()
  private val taskTimeout: Long

  /**
   * Instantiates a new QueueWorker based on the given configuration.
   *
   * @param config Configuration for the RabbitMQ connections.
   */
  constructor(config: QueueConfig) : super(config) {
    this.taskTimeout = config.taskTimeoutSeconds
  }

  /**
   * Instantiates a new QueueWorker using the given action to configure the
   * RabbitMQ connections.
   *
   * @param action Action used to configure the RabbitMQ connections.
   */
  constructor(action: QueueConfig.() -> Unit): super(action) {
    val tmp = QueueConfig()
    tmp.action()
    this.taskTimeout = tmp.taskTimeoutSeconds
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

  /**
   * Initializes the job queue callback.
   */
  override fun initCallbacks() {
    withDispatchQueue {
      basicConsume(
        dispatchQueueName,
        false,
        { _, msg ->

          Log.debug("handling job message with delivery tag {}", msg.envelope.deliveryTag)

          val task: Callable<Any> = Callable {
            try {
              handlers.execute(JobDispatch.fromJson(Json.from(msg.body)))
            } finally {
              Log.debug("acknowledging job message {}", msg.envelope.deliveryTag)
              basicAck(msg.envelope.deliveryTag, false)
              defaultConsumer
            }
          }

          workers.invokeAll(listOf(task), taskTimeout, TimeUnit.SECONDS)
        },
        CancelCallback {
          Log.warn("Cancelled with reason {}.", it)
        },
        ConsumerShutdownSignalCallback { consumerTag: String, shutdownSignalException: ShutdownSignalException ->
          Log.warn("RabbitMQ consumer {} failed with Exception", consumerTag, shutdownSignalException)
        }
      )
    }
  }
}