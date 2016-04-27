package org.danielholmes.coc.baseanalyser.util

import scala.collection.mutable

case class Memo2[A,B,C](f: (A, B) => C) extends ((A, B) => C) {
  private val cache = mutable.Map.empty[(A, B), C]
  def apply(a: A, b: B): C = {
    cache.getOrElseUpdate((a, b), f(a, b))
  }
}
