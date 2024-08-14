package org.veupathdb.lib.rabbit.jobs.config

import com.rabbitmq.client.ConnectionFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ConnectionConfig {
  /**
   * RabbitMQ Connection Hostname
   */
  var hostname: String = "rabbit"
    set(value) {
      if (hostname.isBlank())
        throw IllegalArgumentException("RabbitMQ hostname cannot be blank")
      field = value
    }

  /**
   * RabbitMQ Authentication Username
   */
  var username: String = "guest"

  /**
   * RabbitMQ Authentication Password
   */
  var password: String = "guest"

  /**
   * RabbitMQ Connection Port
   */
  var hostPort: Int = 5672
    set(value) {
      if (value !in 1 .. 65535)
        throw IllegalArgumentException("invalid port number $value")
      field = value
    }

  /**
   * RabbitMQ Connection Timeout
   */
  var timeout: Duration = 5.seconds

  /**
   * Connection Factory
   */
  var connectionFactory: ConnectionFactory = ConnectionFactory()
}
