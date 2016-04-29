package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense.{ArcherTower, Cannon}
import org.danielholmes.coc.baseanalyser.model.heroes.BarbarianKingAltar
import org.danielholmes.coc.baseanalyser.model.trash.Barrack
import org.scalatest._

class HogRiderSpec extends FlatSpec with Matchers {
  val origin = TileCoordinate(0, 0)

  "HogRider" should "return defense target" in {
    val at = ArcherTower(1, Tile(1, 1))
    HogRider.findTargets(origin, Village(Set(at))) should contain (at)
  }

  it should "return non-defense target if none available" in {
    val barrack = Barrack(1, Tile(1, 1))
    HogRider.findTargets(origin, Village(Set(barrack))) should contain (barrack)
  }

  it should "return empty if only wall available" in {
    HogRider.findTargets(origin, Village(Set(Wall(1, Tile.MapOrigin)))) should be (empty)
  }

  it should "return closest defense target if available" in {
    val at = ArcherTower(1, Tile(10, 10))
    val closeBarrack = Barrack(1, Tile(2, 2))
    HogRider.findTargets(origin, Village(Set(at, closeBarrack))) should contain (at)
  }

  it should "not include heroes as target" in {
    val bk = BarbarianKingAltar(1, Tile(10, 10))
    val closeBarrack = Barrack(1, Tile(2, 2))
    HogRider.findTargets(origin, Village(Set(bk, closeBarrack))) should contain (closeBarrack)
  }

  it should "return none for empty village" in {
    HogRider.findTargets(origin, Village.empty) shouldBe empty
  }

  it should "return all equidistant defense targets" in {
    val at = ArcherTower(1, Tile(0, 10))
    val cannon = Cannon(1, Tile(10, 0))
    val closeBarrack = Barrack(1, Tile(2, 2))
    HogRider.findTargets(origin, Village(Set(at, cannon, closeBarrack))) should be (Set(at, cannon))
  }
}
