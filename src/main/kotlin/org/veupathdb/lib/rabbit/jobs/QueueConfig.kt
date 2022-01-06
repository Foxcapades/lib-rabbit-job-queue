package org.veupathdb.lib.rabbit.jobs

import java.util.*

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
    private set

  fun hostname(host: String): QueueConfig {
    hostname = host
    return this
  }

  fun username(user: String): QueueConfig {
    username = user
    return this
  }

  fun password(pass: String): QueueConfig {
    password = pass
    return this
  }

  fun hostPort(port: Int): QueueConfig {
    hostPort = port
    return this
  }

  fun timeout(time: Int): QueueConfig {
    timeout = time
    return this
  }

  fun jobQueueName(name: String): QueueConfig {
    jobQueueName = name
    return this
  }

  fun errorQueueName(name: String): QueueConfig {
    errorQueueName = name
    return this
  }

  fun successQueueName(name: String): QueueConfig {
    successQueueName = name
    return this
  }
}