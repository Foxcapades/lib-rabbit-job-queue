package org.veupathdb.lib.rabbit.jobs.pools.exec

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConsumerShutdownSignalCallback
import org.slf4j.LoggerFactory
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch
import org.veupathdb.lib.rabbit.jobs.pools.JobHandlers
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import java.util.concurrent.*
import kotlin.time.Duration

/**
 * Single 'worker' wrapping a RabbitMQ consumer client whose purpose is to parse
 * incoming job config messages and fire jobs onto an external thread pool for
 * each message.
 *
 * Each executor runs a single job at a time and awaits the result of that job
 * before acknowledging the job config message and moving on to the next
 * available message.
 *
 * Additionally, via the [next] and [prev] values, `Executor` instances act as
 * nodes in a linked list, allowing dead nodes to be pruned from the executor
 * pool efficiently.
 *
 * @author Elizabeth Paige Harper [foxcapades.io@gmail.com]
 * @since 2.0.0
 */
internal class Executor(
  /**
   * Identifier string for this executor.
   *
   * This value is used for logging.
   */
  val id: String,

  /**
   * Channel this executor should subscribe to.
   */
  private val channel: Channel,

  /**
   * Job execution handlers.
   */
  private val handlers: JobHandlers,

  /**
   * Max allowed job execution time.
   */
  private val jobTimeout: Duration,

  /**
   * Callback used to submit jobs to a worker pool.
   */
  private val submitJobFn: (Runnable) -> Future<*>
) {
  private val log = LoggerFactory.getLogger(javaClass)

  var prev: Executor? = null

  var next: Executor? = null

  fun init(queue: String, shutdown: ConsumerShutdownSignalCallback) {
    channel.basicConsume(
      queue,
      false,
      { _, msg ->
        if (!channel.isOpen) {
          log.error("consumer '{}' cannot execute job for message {}, channel is closed!", id, msg.envelope.deliveryTag)
          return@basicConsume
        }

        log.debug("consumer '{}' executing job for message {}", id, msg.envelope.deliveryTag)

        val fut = submitJobFn { handlers.execute(JobDispatch.fromJson(Json.from(msg.body))) }

        // Wait for {jobTimeout} at most before killing the job and
        // acknowledging the message
        try {
          fut.get(jobTimeout.inWholeSeconds, TimeUnit.SECONDS)
          log.debug("acknowledging job message {}", msg.envelope.deliveryTag)
          channel.basicAck(msg.envelope.deliveryTag, false)
        } catch (e: TimeoutException) {
          log.warn("consumer '{}' killing job for message {} for taking longer than {}", id, msg.envelope.deliveryTag, jobTimeout)
          fut.cancel(true)
          channel.basicAck(msg.envelope.deliveryTag, false)
        }
      },
      { },
      shutdown,
    )
  }

  fun stop() {
    log.debug("closing channel for consumer {}", id)
    try { channel.close() } catch (e: Throwable) { /* do nothing */ }
  }
}