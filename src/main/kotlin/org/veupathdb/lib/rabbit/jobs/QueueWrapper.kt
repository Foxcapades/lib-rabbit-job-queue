package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Base implementation of a queue worker or dispatcher.
 */
sealed class QueueWrapper {
  /**
   * RabbitMQ Connection Factory
   */
  private val factory = ConnectionFactory()

  protected val workers: ExecutorService

  /**
   * Open RabbitMQ Connection
   *
   * Connection will be refreshed if it has closed since last access.
   */
  protected var connection: Connection
    get() {
      if (!field.isOpen) {
        field = factory.newConnection()
      }

      return field
    }
    private set

  /**
   * Job Dispatch Queue Name
   */
  protected val dispatchQueueName: String

  /**
   * Job Error Queue Name
   */
  protected val errorQueueName: String

  /**
   * Job Success Queue Name
   */
  protected val successQueueName: String

  /**
   * Constructs a new QueueWrapper implementation instance.
   *
   * @param config Queue configuration used to configure this [QueueWrapper].
   */
  constructor(config: QueueConfig) {
    configure(config)
    connection = factory.newConnection()
    workers    = Executors.newFixedThreadPool(config.workers)

    dispatchQueueName = config.jobQueueName
    errorQueueName    = config.errorQueueName
    successQueueName  = config.successQueueName

    @Suppress("LeakingThis")
    initCallbacks()
  }

  /**
   * Constructs a new QueueWrapper implementation instance.
   *
   * @param action Action that configures the configuration object used to
   * configure this [QueueWrapper].
   */
  constructor(action: QueueConfig.() -> Unit) {
    val tmp = QueueConfig()
    tmp.action()
    configure(tmp)
    connection = factory.newConnection()
    workers    = Executors.newFixedThreadPool(tmp.workers)

    dispatchQueueName = tmp.jobQueueName
    errorQueueName    = tmp.errorQueueName
    successQueueName  = tmp.successQueueName

    @Suppress("LeakingThis")
    initCallbacks()
  }

  /**
   * Initializes the error notification queue if it is not already initialized.
   */
  protected open fun Channel.initErrorQueue() {
    queueDeclare(errorQueueName, true, false, false, emptyMap())
  }

  /**
   * Initializes the success notification queue if it is not already
   * initialized.
   */
  protected open fun Channel.initSuccessQueue() {
    queueDeclare(successQueueName, true, false, false, emptyMap())
  }

  /**
   * Initializes the job dispatch queue if it is not already initialized.
   */
  protected open fun Channel.initDispatchQueue() {
    queueDeclare(dispatchQueueName, true, false, false, emptyMap())
  }

  /**
   * Internal inline channel usage.
   */
  protected inline fun withChannel(action: Channel.() -> Unit) =
    with(connection.createChannel()) { action() }

  /**
   * Executes the given action against the job dispatch queue in a thread safe
   * lock.
   *
   * @param action Action to execute against the job dispatch queue.
   */
  protected inline fun withDispatchQueue(action: Channel.() -> Unit) =
    withChannel {
      initDispatchQueue()
      action()
    }

  /**
   * Executes the give action against the error notification queue in a thread
   * safe lock.
   *
   * @param action Action to execute against the error notification queue.
   */
  protected inline fun withErrorQueue(action: Channel.() -> Unit) =
    withChannel {
      initErrorQueue()
      action()
    }

  /**
   * Executes the given action against the success notification queue in a
   * thread safe lock.
   *
   * @param action Action to execute
   */
  protected inline fun withSuccessQueue(action: Channel.() -> Unit) =
    withChannel {
      initSuccessQueue()
      action()
    }

  /**
   * Publishes a message to the given [Channel].
   *
   * @param queue Name of the queue to publish the message to.
   * @param body  Body of the message to publish.
   */
  protected fun Channel.publish(queue: String, body: JsonSerializable) {
    basicPublish(
      "",
      queue,
      null,
      body.toJson().toString().toByteArray(StandardCharsets.UTF_8)
    )
  }

  /**
   * Initialize queue callbacks.
   */
  protected abstract fun initCallbacks()

  /**
   * Configures the RabbitMQ [ConnectionFactory] based on the settings in the
   * given [QueueConfig].
   *
   * @param config Caller initialized RabbitMQ configuration properties.
   */
  private fun configure(config: QueueConfig) {
    factory.host              = config.hostname
    factory.username          = config.username
    factory.password          = config.password
    factory.port              = config.hostPort
    factory.connectionTimeout = config.timeout
  }
}
