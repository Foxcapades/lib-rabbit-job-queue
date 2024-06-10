package org.veupathdb.lib.rabbit.jobs

import java.time.Duration

/**
 * RabbitMQ Queue Configuration
 */
class QueueConfig {
  /**
   * RabbitMQ Connection Hostname
   */
  var hostname = "rabbit"

  /**
   * RabbitMQ Authentication Username
   */
  var username = "guest"

  /**
   * RabbitMQ Authentication Password
   */
  var password = "guest"

  /**
   * RabbitMQ Connection Port
   */
  var hostPort = 5672

  /**
   * RabbitMQ Connection Timeout
   */
  var timeout  = 5_000

  /**
   * Job Dispatch Queue Name
   */
  var jobQueueName = "jobs"

  /**
   * Job Failure Notification Queue Name
   */
  var errorQueueName = "errors"

  /**
   * Job Success Notification Queue Name
   */
  var successQueueName = "successes"

  /**
   * Callback Worker Count
   *
   * Number of worker threads used to handle incoming messages.
   */
  var workers = 5

  /**
   * Maximum amount of time a task can take before timing out.
   */
  var taskTimeoutSeconds = Duration.ofMinutes(20).toSeconds()

  /**
   * Configures the RabbitMQ hostname.
   *
   * @param host RabbitMQ hostname.
   *
   * @return This configuration.
   */
  fun hostname(host: String): QueueConfig {
    hostname = host
    return this
  }

  /**
   * Configures the RabbitMQ authentication username.
   *
   * @param user RabbitMQ username.
   *
   * @return This configuration.
   */
  fun username(user: String): QueueConfig {
    username = user
    return this
  }

  /**
   * Configures the RabbitMQ authentication password.
   *
   * @param pass RabbitMQ password.
   *
   * @return This configuration.
   */
  fun password(pass: String): QueueConfig {
    password = pass
    return this
  }

  /**
   * Configures the RabbitMQ host port.
   *
   * @param port RabbitMQ port.
   *
   * @return This configuration.
   */
  fun hostPort(port: Int): QueueConfig {
    hostPort = port
    return this
  }

  /**
   * Configures the RabbitMQ connection timeout.
   *
   * @param time Connection timeout.
   *
   * @return This configuration.
   */
  fun timeout(time: Int): QueueConfig {
    timeout = time
    return this
  }

  /**
   * Configures the name of the job dispatch queue.
   *
   * @param name Job dispatch queue name.
   *
   * @return This configuration.
   */
  fun jobQueueName(name: String): QueueConfig {
    jobQueueName = name
    return this
  }

  /**
   * Configures the name of the job error notification queue.
   *
   * @param name Error notification queue name.
   *
   * @return This configuration.
   */
  fun errorQueueName(name: String): QueueConfig {
    errorQueueName = name
    return this
  }

  /**
   * Configures the name of the job success notification queue.
   *
   * @param name Success notification queue name.
   *
   * @return This configuration.
   */
  fun successQueueName(name: String): QueueConfig {
    successQueueName = name
    return this
  }
}