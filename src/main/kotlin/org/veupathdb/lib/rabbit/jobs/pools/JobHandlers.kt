package org.veupathdb.lib.rabbit.jobs.pools

import org.veupathdb.lib.rabbit.jobs.fn.JobHandler
import org.veupathdb.lib.rabbit.jobs.model.JobDispatch

internal class JobHandlers : HandlerPool<JobHandler, JobDispatch> {
  private val handlers = ArrayList<JobHandler>(2)

  override fun register(handler: JobHandler) {
    handlers.add(handler)
  }

  override fun deregister(handler: JobHandler) {
    handlers.remove(handler)
  }

  override fun execute(msg: JobDispatch) {
    handlers.forEach { it.handle(msg) }
  }
}