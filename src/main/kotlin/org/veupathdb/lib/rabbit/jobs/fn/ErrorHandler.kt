package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.ErrorNotification

/**
 * Callback to be executed on incoming job failure notification.
 */
@FunctionalInterface
fun interface ErrorHandler {

  /**
   * Handles the job failure notification.
   *
   * @param msg Job failure notification.
   */
  fun handle(msg: ErrorNotification)
}