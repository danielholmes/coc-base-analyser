package org.danielholmes.coc.baseanalyser.baseparser

import org.danielholmes.coc.baseanalyser.model._
import org.danielholmes.coc.baseanalyser.model.defense._
import org.danielholmes.coc.baseanalyser.model.heroes.{ArcherQueenAltar, BarbarianKingAltar, GrandWarden}
import org.danielholmes.coc.baseanalyser.model.special.{ClanCastle, TownHall}
import org.danielholmes.coc.baseanalyser.model.traps._
import org.danielholmes.coc.baseanalyser.model.trash._
import org.scalactic.anyvals.{PosInt, PosZInt}

class HardCodedElementFactory extends ElementFactory {
  private def levelAndCoordinateConstructor(constructor: (PosInt, Tile) => Element): (RawElement => Element) = {
    raw => constructor(elementLevel(raw.lvl), elementTile(raw))
  }

  private def elementLevel(rawLevel: Int): PosInt = PosInt.from(Math.max(1, rawLevel + 1)).get

  private def elementTile(raw: RawElement) = Tile(PosZInt.from(raw.x).get, PosZInt.from(raw.y).get)

  private val elementConstructorByCode: Map[Int, RawElement => Element] = Map(
    // Buildings
    1000001 -> levelAndCoordinateConstructor(TownHall),
    1000000 -> levelAndCoordinateConstructor(ArmyCamp),
    1000002 -> levelAndCoordinateConstructor(ElixirCollector),
    1000003 -> levelAndCoordinateConstructor(ElixirStorage),
    1000004 -> levelAndCoordinateConstructor(GoldMine),
    1000005 -> levelAndCoordinateConstructor(GoldStorage),
    1000006 -> levelAndCoordinateConstructor(Barrack),
    1000007 -> levelAndCoordinateConstructor(Laboratory),
    1000008 -> levelAndCoordinateConstructor(Cannon),
    1000009 -> levelAndCoordinateConstructor(ArcherTower),
    1000010 -> levelAndCoordinateConstructor(Wall),
    1000011 -> levelAndCoordinateConstructor(WizardTower),
    1000012 -> levelAndCoordinateConstructor(AirDefense),
    1000013 -> levelAndCoordinateConstructor(Mortar),
    1000014 -> levelAndCoordinateConstructor(ClanCastle),
    1000015 -> ((raw: RawElement) => BuilderHut(elementTile(raw))),
    //1000016 CommunicationsMast,
    //1000017 -> GoblinTownHull,
    //1000018 -> GoblinHut,
    1000019 -> levelAndCoordinateConstructor(HiddenTesla),
    1000020 -> levelAndCoordinateConstructor(SpellFactory),
    1000021 -> ((raw: RawElement) => XBow.both(elementLevel(raw.lvl), elementTile(raw))),
    1000022 -> levelAndCoordinateConstructor(BarbarianKingAltar),
    1000023 -> levelAndCoordinateConstructor(DarkElixirCollector),
    1000024 -> levelAndCoordinateConstructor(DarkElixirStorage),
    1000025 -> levelAndCoordinateConstructor(ArcherQueenAltar),
    1000026 -> levelAndCoordinateConstructor(DarkBarrack),
    1000027 -> levelAndCoordinateConstructor(InfernoTower),
    1000028 -> ((raw: RawElement) => AirSweeper(elementLevel(raw.lvl), elementTile(raw), raw.aimAngle.get)),
    1000029 -> levelAndCoordinateConstructor(DarkSpellFactory),
    1000030 -> levelAndCoordinateConstructor(GrandWarden),
    1000031 -> levelAndCoordinateConstructor(EagleArtillery),

    // Traps
    12000000 -> levelAndCoordinateConstructor(Bomb),
    12000001 -> levelAndCoordinateConstructor(SpringTrap),
    12000002 -> levelAndCoordinateConstructor(GiantBomb),
    12000003 -> levelAndCoordinateConstructor(HalloweenBomb),
    //12000004 -> levelAndCoordinateConstructor(????),
    12000005 -> levelAndCoordinateConstructor(AirBomb),
    12000006 -> levelAndCoordinateConstructor(SeekingAirMine),
    12000007 -> levelAndCoordinateConstructor(SantaTrap),
    12000008 -> levelAndCoordinateConstructor(SkeletonTrap)
  )

  def build(raw: RawElement): Option[Element] = {
    Some(raw)
      .filter(shouldInclude)
      .map(
        value => elementConstructorByCode.get(value.data)
          .orElse(throw new RuntimeException("No building with code " + raw.data))
          .get(raw)
      )
  }

  private def shouldInclude(raw: RawElement): Boolean = {
    Set("1800", "800").forall(!raw.data.toString.startsWith(_))
  }
}

/*
Decorations
18000000 Barbarian Statue
18000001 Torch
18000002 Goblin Pole
18000003 White Flag
18000004 Skull Flag
18000005 Flower box 1
18000006 Flower box 2
18000007 Windmeter
18000008 Down Arrow Flag
18000009 Up Arrow Flag
18000010 Skull Altar
18000011 USA Flag
18000012 Canada Flag
18000013 Italia Flag
18000014 Germany Flag
18000015 Finland Flag
18000016 Spain Flag
18000017 France Flag
18000018 GBR Flag
18000019 Brazil Flag
18000020 China Flag
18000021 Norway Flag
18000022 Thailand Flag
18000023 Thailand Flag
18000024 India Flag
18000025 Australia Flag
18000026 South Korea Flag
18000027 Japan Flag
18000028 Turkey Flag
18000029 Indonesia Flag
18000030 Netherlands Flag
18000031 Philippines Flag
18000032 Singapore Flag
18000033 PEKKA Statue
18000034 Russia Flag
18000035 Russia Flag
18000036 Greece Flag


obstacles ID:
Code:
8000000 Pine Tree
8000001 Large Stone
8000002 Small Stone 1
8000003 Small Stone 2
8000004 Square Bush
8000005 Square Tree
8000006 Tree Trunk 1
8000007 Tree Trunk 2
8000008 Mushrooms
8000009 TombStone
8000010 Fallen Tree
8000011 Small Stone 3
8000012 Small Stone 4
8000013 Square Tree 2
8000014 Stone Pillar 1
8000015 Large Stone
8000016 Sharp Stone 1
8000017 Sharp Stone 2
8000018 Sharp Stone 3
8000019 Sharp Stone 4
8000020 Sharp Stone 5
8000021 Xmas tree
8000022 Hero TombStone
8000023 DarkTombStone
8000024 Passable Stone 1
8000025 Passable Stone 2
8000026 Campfire
8000027 Campfire
8000028 Xmas tree2013
8000029 Xmas TombStone
8000030 Bonus Gembox
8000031 Halloween2014
8000032 Xmas tree2014
8000033 Xmas TombStone2014
8000034 Npc Plant 1
8000035 Npc Plant 2
8000036 Halloween2015
*/
