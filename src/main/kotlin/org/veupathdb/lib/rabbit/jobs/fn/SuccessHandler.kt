package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification

@FunctionalInterface
fun interface SuccessHandler {
  fun handle(msg: SuccessNotification)
}