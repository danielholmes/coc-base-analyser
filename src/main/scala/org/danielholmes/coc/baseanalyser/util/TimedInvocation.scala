package org.danielholmes.coc.baseanalyser.util

import java.time.Duration

object TimedInvocation {
  def run[T](op: () => T): (T, Duration) = {
    val start = System.currentTimeMillis
    val result = op.apply()
    val duration = Duration.ofMillis(System.currentTimeMillis - start)
    (result, duration)
  }
}
