package org.veupathdb.lib.rabbit.jobs.fn

import org.veupathdb.lib.rabbit.jobs.model.JobDispatch

/**
 * Callback to be executed on incoming job request.
 */
@FunctionalInterface
fun interface JobHandler {

  /**
   * Handles the job request.
   *
   * @param msg Job request.
   */
  fun handle(msg: JobDispatch)
}