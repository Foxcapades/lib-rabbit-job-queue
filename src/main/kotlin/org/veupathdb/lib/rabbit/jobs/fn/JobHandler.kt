package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.JobDispatch

@FunctionalInterface
fun interface JobHandler {
  fun handle(msg: JobDispatch)
}