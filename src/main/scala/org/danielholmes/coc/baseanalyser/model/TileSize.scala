package org.danielholmes.coc.baseanalyser.model

case class TileSize(private val size: Int) {
  val toInt = size

  def toDouble = toInt.toDouble

  def +(amount: Int): TileSize = TileSize(size + amount)
  def +(amount: TileSize): TileSize = this + amount.toInt

  def *(amount: Int): TileSize = TileSize(size * amount)

  def -(amount: Int): TileSize = TileSize(size - amount)
  def -(amount: TileSize): TileSize = this - amount.toInt

  def <(amount: Int): Boolean = size < amount

  def >(amount: Int): Boolean = size < amount
}
