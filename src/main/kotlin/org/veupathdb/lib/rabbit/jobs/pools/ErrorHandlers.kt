package org.veupathdb.lib.rabbit.jobs.pools

import org.veupathdb.lib.rabbit.jobs.fn.ErrorHandler
import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification

internal class ErrorHandlers : HandlerPool<ErrorHandler, ErrorNotification> {

  private val handlers = ArrayList<ErrorHandler>(2)

  override fun register(handler: ErrorHandler) {
    handlers.add(handler)
  }

  override fun deregister(handler: ErrorHandler) {
    handlers.remove(handler)
  }

  override fun execute(msg: ErrorNotification) {
    handlers.forEach { it.handle(msg) }
  }
}