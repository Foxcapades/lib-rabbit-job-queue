package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.SuccessNotification

/**
 * Callback to be executed on incoming job success notification.
 */
@FunctionalInterface
fun interface SuccessHandler {
  /**
   * Handles the job success notification.
   *
   * @param msg Job success notification.
   */
  fun handle(msg: SuccessNotification)
}