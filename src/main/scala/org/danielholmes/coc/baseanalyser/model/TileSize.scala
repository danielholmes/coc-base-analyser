package org.danielholmes.coc.baseanalyser.model

// TODO: Look into anyval - http://docs.scala-lang.org/overviews/core/value-classes.html
case class TileSize(private val size: Int) {
  val toInt = size

  def toDouble = toInt.toDouble

  def +(amount: Int): TileSize = TileSize(size + amount)
  def +(amount: TileSize): TileSize = this + amount.toInt

  def *(amount: Int): TileSize = TileSize(size * amount)

  def -(amount: Int): TileSize = TileSize(size - amount)
  def -(amount: TileSize): TileSize = this - amount.toInt

  def <(amount: TileSize): Boolean = size < amount.toInt
  def <(amount: Int): Boolean = size < amount

  def >(amount: Int): Boolean = size < amount

  def %(amount: TileSize): Int = this % amount.toInt
  def %(amount: Int): Int = size % amount

  def /(amount: TileSize): Int = this / amount.toInt
  def /(amount: Int): Int = size / amount
}
