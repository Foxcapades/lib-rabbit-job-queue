package org.veupathdb.lib.rabbit.jobs

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.veupathdb.lib.rabbit.jobs.config.QueueConfig
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable
import java.nio.charset.StandardCharsets

/**
 * Base implementation of a queue worker or dispatcher.
 */
sealed class QueueWrapper {
  /**
   * RabbitMQ Connection Factory
   */
  private val factory = ConnectionFactory()

  protected val config: QueueConfig

  /**
   * Open RabbitMQ Connection
   */
  protected val connection: Connection

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
    this.config = config
    configure(config)

    connection = factory.newConnection()

    dispatchQueueName = config.jobQueueName
    errorQueueName    = config.errorQueueName
    successQueueName  = config.successQueueName
  }

  /**
   * Constructs a new QueueWrapper implementation instance.
   *
   * @param action Action that configures the configuration object used to
   * configure this [QueueWrapper].
   */
  constructor(action: QueueConfig.() -> Unit) {
    config = QueueConfig()
    config.action()
    configure(config)
    connection = factory.newConnection()

    dispatchQueueName = config.jobQueueName
    errorQueueName    = config.errorQueueName
    successQueueName  = config.successQueueName
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
    queueDeclare(
      /* queue = */      dispatchQueueName,
      /* durable = */    true,
      /* exclusive = */  false,
      /* autoDelete = */ false,
      /* arguments = */  mapOf(
        "x-consumer-timeout" to (config.executor.maxJobExecutionTime * 1.5).inWholeMilliseconds
      ),
    )
  }

  protected fun dispatchQueue(): Channel = connection.createChannel().also { it.initDispatchQueue() }

  /**
   * Executes the given action against the job dispatch queue in a thread safe
   * lock.
   *
   * @param action Action to execute against the job dispatch queue.
   */
  protected inline fun withDispatchQueue(action: Channel.() -> Unit) = dispatchQueue().let(action)

  protected fun errorQueue(): Channel = connection.createChannel().also { it.initErrorQueue() }

  /**
   * Executes the give action against the error notification queue in a thread
   * safe lock.
   *
   * @param action Action to execute against the error notification queue.
   */
  protected inline fun withErrorQueue(action: Channel.() -> Unit) = errorQueue().let(action)

  protected fun successQueue(): Channel = connection.createChannel().also { it.initSuccessQueue() }

  /**
   * Executes the given action against the success notification queue in a
   * thread safe lock.
   *
   * @param action Action to execute
   */
  protected inline fun withSuccessQueue(action: Channel.() -> Unit) = successQueue().let(action)

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
   * Configures the RabbitMQ [ConnectionFactory] based on the settings in the
   * given [QueueConfig].
   *
   * @param config Caller initialized RabbitMQ configuration properties.
   */
  private fun configure(config: QueueConfig) {
    factory.host              = config.connection.hostname
    factory.username          = config.connection.username
    factory.password          = config.connection.password
    factory.port              = config.connection.hostPort
    factory.connectionTimeout = config.connection.timeout.inWholeMilliseconds.toInt()
  }
}
