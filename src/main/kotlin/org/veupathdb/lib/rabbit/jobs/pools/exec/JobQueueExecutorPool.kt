package org.veupathdb.lib.rabbit.jobs.pools.exec

import com.rabbitmq.client.Consumer
import com.rabbitmq.client.ConsumerShutdownSignalCallback
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Job Queue Executor Pool
 *
 * Contains a pool of job execution workers subscribed to a RabbitMQ queue
 * defined by the given configuration params.
 *
 * Each executor in the pool operates with its own [Consumer] instance on its
 * own channel provided by the given [ChannelProvider].
 *
 * If an executor's consumer dies, the executor will be replaced.
 *
 * ```kotlin
 * // Create a new executor pool
 * val executor = JobQueueExecutorPool(config)
 *
 * // Start the executor pool's workers
 * executor.start()
 *
 * // Gracefully stop the executor pool
 * executor.stop()
 *
 * // Immediately stop the executor pool (interrupting any running jobs)
 * executor.stopNow()
 * ```
 *
 * @author Elizabeth Paige Harper [foxcapades.io@gmail.com]
 * @since 2.0.0
 */
internal class JobQueueExecutorPool(private val config: ExecutorPoolConfig) {
  private val logger = LoggerFactory.getLogger(javaClass)

  private val closing = AtomicBoolean(false)
  private val name = "${config.queueName}-consumer-pool"
  private val threads = Executors.newFixedThreadPool(config.poolSize)

  private var head: Executor? = null
  private var tail: Executor? = null
  private var counter = 0u

  init {
    if (config.poolSize !in 1..64)
      throw IllegalArgumentException("invalid size value ${config.poolSize}, must be in the range [1, 64]")
  }

  /**
   * Starts the executor pool's workers.
   *
   * Until this method is called, the worker pool will not receive or react to
   * any job queue messages.
   */
  fun start() {
    for (i in 0 ..< config.poolSize)
      add(createNew().also { it.init(config.queueName, onConsumerShutdown(it)) })
  }

  /**
   * Attempts to gracefully shut down the executor pool.
   *
   * If [block] is `true`, this method will block the current thread until the
   * [killAfter] duration has been reached, at which point the executor pool
   * will request a force-shutdown of the underlying thread pool, aborting any
   * jobs still in progress at that point.  If [blockAfterKill] is also `true`
   * at this point, the method will continue to block after the force-shutdown
   * request until the last job has ended.
   *
   * If [block] is `false`, this method will not block.  It will request the
   * underlying thread pool shut down, then return immediately.  In this case
   * the values of [killAfter] and [blockAfterKill] are ignored.
   *
   * @param block Whether this method should block the current thread until the
   * underlying thread pool has gracefully shut down.
   *
   * @param killAfter Max duration to wait for a graceful shutdown before
   * attempting to abort remaining in-progress job executions.
   *
   * @param blockAfterKill Whether this method should continue to block after
   * the [killAfter] duration has passed.
   */
  fun stop(
    block: Boolean = false,
    killAfter: Duration = 10_000.days,
    blockAfterKill: Boolean = true,
  ): StopCode {
    stopExecutors()
    threads.shutdown()

    if (!block)
      return if (threads.isTerminated) StopCode.Graceful else StopCode.Unknown

    if (threads.awaitTermination(killAfter.inWholeMilliseconds, TimeUnit.MILLISECONDS))
      return StopCode.Graceful

    threads.shutdownNow()
    silently(config.shutdownCB)
    if (blockAfterKill)
      threads.awaitTermination(243256, TimeUnit.DAYS)

    return StopCode.Forced
  }

  @JvmInline value class StopCode private constructor(private val value: Int) {
    companion object {
      val Unknown = StopCode(0)
      val Graceful = StopCode(1)
      val Forced = StopCode(2)
    }
  }

  /**
   * Shuts down the executor pool by force, aborting any currently running jobs.
   *
   * This method may optionally block until the shutdown has completed.
   *
   * @param block Whether this method should block until the shutdown has
   * completed.
   */
  fun stopNow(block: Boolean = true): StopCode {
    stopExecutors()
    threads.shutdownNow()
    silently(config.shutdownCB)

    if (block)
      threads.awaitTermination(243256, TimeUnit.DAYS)

    return StopCode.Forced
  }

  private fun stopExecutors() {
    synchronized(this) {
      if (closing.get())
        return

      closing.set(true)

      var next = head
      head = null
      tail = null

      while (next != null) {
        val tn = next.next

        next.prev = null
        next.next = null
        next.stop()

        next = tn
      }
    }
  }

  private fun createNew(): Executor {
    counter++
    val name = "$name-$counter"

    logger.debug("creating new consumer: {}", name)

    return Executor(
      id = name,
      channel = config.channelProvider(),
      handlers = config.handlers,
      jobTimeout = config.maxJobTime,
      submitJobFn = threads::submit
    )
  }

  private fun onConsumerShutdown(ex: Executor): ConsumerShutdownSignalCallback {
    return ConsumerShutdownSignalCallback { _, sig ->
      if (sig.isInitiatedByApplication) {
        logger.debug("received consumer {} shutdown signal", ex.id)
      } else {
        logger.warn("caught unexpected shutdown on consumer {}", ex.id)
        config.failureChecker.markFailure()
        if (config.failureChecker.shouldHalt()) {
          val reason = config.failureChecker.reason()
          logger.error("shutting down job queue executor: {}", reason)

          stopNow()
          return@ConsumerShutdownSignalCallback
        }
      }

      remove(ex)
      ex.stop()

      if (!closing.get())
        add(createNew().also { it.init(config.queueName, onConsumerShutdown(it)) })
    }
  }

  private fun add(ex: Executor) {
    if (head == null) {
      head = ex
      tail = ex
    } else {
      tail!!.next = ex
      tail = ex
    }
  }

  private fun remove(ex: Executor) {
    if (head === ex) {
      head = ex.next
      head?.prev = null
    } else if (tail === ex) {
      tail = ex.prev
      tail?.next = null
    } else {
      ex.next?.prev = ex.prev
      ex.prev?.next = ex.next
    }
  }

  private fun silently(fn: () -> Unit) {
    try {
      fn()
    } catch (e: Throwable) {
      // do nothing
    }
  }
}