package org.veupathdb.lib.rabbit.jobs.config

internal class AnyFailureEnforcer(policies: Array<out ExecutorFailurePolicy>) : ExecutorFailureEnforcer {
  private val enforcers = policies.map(ExecutorFailurePolicy::newEnforcer)

  override fun markFailure() = enforcers.forEach(ExecutorFailureEnforcer::markFailure)
  override fun shouldHalt() = enforcers.any(ExecutorFailureEnforcer::shouldHalt)
  override fun reason() = enforcers.first(ExecutorFailureEnforcer::shouldHalt).reason()
}