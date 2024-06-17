package org.veupathdb.lib.rabbit.jobs.model

internal data class PoolState(
  var totalFailures: UInt,
  var windowedFailures: UInt,
) : ExecutorPoolState