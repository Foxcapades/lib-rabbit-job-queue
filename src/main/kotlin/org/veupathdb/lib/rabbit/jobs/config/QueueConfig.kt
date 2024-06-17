package org.veupathdb.lib.rabbit.jobs.config

/**
 * RabbitMQ Queue Configuration
 */
class QueueConfig {

  /**
   * RabbitMQ connection configuration.
   */
  var connection: ConnectionConfig = ConnectionConfig()

  /**
   * RabbitMQ connection configuration.
   *
   * @param connectionConfig New connection configuration object.
   *
   * @return This configuration.
   */
  fun connection(connectionConfig: ConnectionConfig) = also { it.connection = connectionConfig }

  /**
   * Executes the given function on the current [connection] value.
   *
   * @param fn Function to execute on the current [connection] value.
   *
   * @return This configuration.
   */
  inline fun connection(fn: ConnectionConfig.() -> Unit) = also { it.connection.fn() }


  /**
   * Job Dispatch Queue Name
   */
  var jobQueueName = "jobs"

  /**
   * Configures the name of the job dispatch queue.
   *
   * @param name Job dispatch queue name.
   *
   * @return This configuration.
   */
  fun jobQueueName(name: String) = apply { jobQueueName = name }


  /**
   * Job Failure Notification Queue Name
   */
  var errorQueueName = "errors"

  /**
   * Configures the name of the job error notification queue.
   *
   * @param name Error notification queue name.
   *
   * @return This configuration.
   */
  fun errorQueueName(name: String) = apply { errorQueueName = name }


  /**
   * Job Success Notification Queue Name
   */
  var successQueueName = "successes"

  /**
   * Configures the name of the job success notification queue.
   *
   * @param name Success notification queue name.
   *
   * @return This configuration.
   */
  fun successQueueName(name: String) = apply { successQueueName = name }


  var executor: ExecutorConfig = ExecutorConfig()

  fun executor(executorConfig: ExecutorConfig) = also { it.executor = executorConfig }

  inline fun executor(fn: ExecutorConfig.() -> Unit) = also { it.executor.fn() }
}
