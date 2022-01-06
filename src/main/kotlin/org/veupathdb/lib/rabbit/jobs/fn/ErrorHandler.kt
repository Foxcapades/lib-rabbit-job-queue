package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification

@FunctionalInterface
fun interface ErrorHandler {
  fun handle(msg: ErrorNotification)
}