package org.veupathdb.lib.rabbit.jobs.config

import kotlin.time.Duration

/**
 * RabbitMQ Queue Configuration
 */
class QueueConfig {

  // region Connection

  /**
   * RabbitMQ connection configuration.
   */
  var connectionConfig: ConnectionConfig = ConnectionConfig()

  /**
   * RabbitMQ connection configuration.
   *
   * @param connectionConfig New connection configuration object.
   *
   * @return This configuration.
   */
  fun connectionConfig(connectionConfig: ConnectionConfig) = also { it.connectionConfig = connectionConfig }

  /**
   * Executes the given function on the current [connectionConfig] value.
   *
   * @param fn Function to execute on the current [connectionConfig] value.
   *
   * @return This configuration.
   */
  inline fun connectionConfig(fn: ConnectionConfig.() -> Unit) = also { it.connectionConfig.fn() }


  /**
   * See [ConnectionConfig.hostname].
   */
  inline var hostname
    get() = connectionConfig.hostname
    set(value) { connectionConfig.hostname = value }

  /**
   * See [ConnectionConfig.hostname].
   *
   * @param host RabbitMQ hostname.
   *
   * @return This configuration.
   */
  fun hostname(host: String) = apply { connectionConfig.hostname = host }


  /**
   * See [ConnectionConfig.username].
   */
  inline var username
    get() = connectionConfig.username
    set(value) { connectionConfig.username = value }

  /**
   * See [ConnectionConfig.username].
   *
   * @param user RabbitMQ username.
   *
   * @return This configuration.
   */
  fun username(user: String) = apply { connectionConfig.username = user }


  /**
   * See [ConnectionConfig.password].
   */
  inline var password
    get() = connectionConfig.password
    set(value) { connectionConfig.password = value }

  /**
   * See [ConnectionConfig.password].
   *
   * @param pass RabbitMQ password.
   *
   * @return This configuration.
   */
  fun password(pass: String) = apply { connectionConfig.password = pass }


  /**
   * See [ConnectionConfig.hostPort].
   */
  inline var hostPort
    get() = connectionConfig.hostPort
    set(value) { connectionConfig.hostPort = value }

  /**
   * See [ConnectionConfig.hostPort].
   *
   * See [ConnectionConfig.hostPort].
   *
   * @return This configuration.
   */
  fun hostPort(port: Int) = apply { connectionConfig.hostPort = port }


  /**
   * See [ConnectionConfig.timeout].
   */
  inline var timeout
    get() = connectionConfig.timeout
    set(value) { connectionConfig.timeout = value }

  /**
   * See [ConnectionConfig.timeout].
   *
   * @param time Connection timeout.
   *
   * @return This configuration.
   */
  fun timeout(time: Duration) = apply { connectionConfig.timeout = time }

  // endregion Connection

  // region Queue Names

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

  // endregion Queue Names

  // region Executor

  var executorConfig: ExecutorConfig = ExecutorConfig()

  fun executorConfig(executorConfig: ExecutorConfig) = also { it.executorConfig = executorConfig }

  inline fun executorConfig(fn: ExecutorConfig.() -> Unit) = also { it.executorConfig.fn() }


  /**
   * See [ExecutorConfig.workers].
   */
  inline var workers
    get() = executorConfig.workers
    set(value) { executorConfig.workers = value }

  /**
   * See [ExecutorConfig.workers].
   *
   * @param value Number of workers
   *
   * @return This configuration.
   */
  fun workers(value: Int) = apply { executorConfig.workers = value }


  /**
   * See [ExecutorConfig.maxJobExecutionTime].
   */
  inline var maxJobExecutionTime
    get() = executorConfig.maxJobExecutionTime
    set(value) { executorConfig.maxJobExecutionTime = value }

  /**
   * See [ExecutorConfig.maxJobExecutionTime].
   *
   * @param duration Max duration a job will be permitted to run for.
   *
   * @return This configuration.
   */
  fun maxJobExecutionTime(duration: Duration) = apply { executorConfig.maxJobExecutionTime = duration }


  /**
   * See [ExecutorConfig.failurePolicy].
   */
  inline var executorFailurePolicy
    get() = executorConfig.failurePolicy
    set(value) { executorConfig.failurePolicy = value }

  /**
   * See [ExecutorConfig.failurePolicy].
   *
   * @param policy Failure policy to use.
   *
   * @return This configuration.
   */
  fun executorFailurePolicy(policy: ExecutorFailurePolicy) = apply { executorConfig.failurePolicy = policy }

  // endregion Executor
}
