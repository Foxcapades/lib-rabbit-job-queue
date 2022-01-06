package org.veupathdb.lib.rabbit.jobs.pools

internal sealed interface HandlerPool<H, T> {
  fun register(handler: H)

  fun deregister(handler: H)

  fun execute(msg: T)
}