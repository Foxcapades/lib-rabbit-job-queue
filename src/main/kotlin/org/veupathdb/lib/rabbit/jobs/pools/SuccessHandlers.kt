package org.veupathdb.lib.rabbit.jobs.pools

import org.veupathdb.lib.rabbit.jobs.fn.SuccessHandler
import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification

internal class SuccessHandlers : HandlerPool<SuccessHandler, SuccessNotification> {
  private val handlers = ArrayList<SuccessHandler>(1)

  override fun register(handler: SuccessHandler) {
    handlers.add(handler)
  }

  override fun deregister(handler: SuccessHandler) {
    handlers.remove(handler)
  }

  override fun execute(msg: SuccessNotification) {
    handlers.forEach { it.handle(msg) }
  }
}