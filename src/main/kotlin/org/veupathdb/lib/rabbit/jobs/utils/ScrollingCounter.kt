package org.veupathdb.lib.rabbit.jobs.utils

import java.util.LinkedList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScrollingCounter(windowDuration: Duration = 5.seconds) {
  private val entries = LinkedList<Long>()
  private val maxDelta = windowDuration.inWholeMilliseconds

  fun inc() { entries.offer(System.currentTimeMillis()) }

  operator fun compareTo(value: Int) = compareTo(value.toUInt())
  operator fun compareTo(value: UInt) = count().compareTo(value)

  fun count(): UInt {
    val cutoff = System.currentTimeMillis() - maxDelta

    while (entries.isNotEmpty() && entries.first < cutoff)
      entries.pop()

    return entries.size.toUInt()
  }

  override fun equals(other: Any?) =
    when (other) {
      is Int -> count() == other
      is UInt -> count() == other
      is ScrollingCounter -> other === this || count() == other.count()
      else -> false
    }

  override fun hashCode() = entries.hashCode() * 31 + maxDelta.hashCode()
}