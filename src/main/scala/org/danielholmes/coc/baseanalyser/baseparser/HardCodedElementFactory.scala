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

  private def noLevelConstructor(constructor: (Tile) => Element): (RawElement => Element) = {
    raw => constructor(elementTile(raw))
  }

  private def elementLevel(rawLevel: Int): PosInt = PosInt.from(Math.max(1, rawLevel + 1)).get

  private def elementTile(raw: RawElement) = Tile(PosZInt.from(raw.x).get, PosZInt.from(raw.y).get)

  private val decorationConstructor = (element: RawElement) => Decoration(elementTile(element))

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
    1000015 -> noLevelConstructor(BuilderHut),
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
    1000028 -> ((raw: RawElement) => AirSweeper(elementLevel(raw.lvl), elementTile(raw), Angle.degrees(raw.aimAngle.get))),
    1000029 -> levelAndCoordinateConstructor(DarkSpellFactory),
    1000030 -> levelAndCoordinateConstructor(GrandWarden),
    1000031 -> levelAndCoordinateConstructor(EagleArtillery),

    // Traps
    12000000 -> levelAndCoordinateConstructor(Bomb),
    12000001 -> noLevelConstructor(SpringTrap),
    12000002 -> levelAndCoordinateConstructor(GiantBomb),
    12000003 -> levelAndCoordinateConstructor(HalloweenBomb),
    //12000004 -> levelAndCoordinateConstructor(????),
    12000005 -> levelAndCoordinateConstructor(AirBomb),
    12000006 -> levelAndCoordinateConstructor(SeekingAirMine),
    12000007 -> levelAndCoordinateConstructor(SantaTrap),
    12000008 -> levelAndCoordinateConstructor(SkeletonTrap),

    // Decorations/Obstacles
    18000000 -> decorationConstructor, // Barbarian Statue
    18000001 -> decorationConstructor, // Torch
    18000002 -> decorationConstructor, // Goblin Pole
    18000003 -> decorationConstructor, // White Flag
    18000004 -> decorationConstructor, // Skull Flag
    18000005 -> decorationConstructor, // Flower box 1
    18000006 -> decorationConstructor, // Flower box 2
    18000007 -> decorationConstructor, // Windmeter
    18000008 -> decorationConstructor, // Down Arrow Flag
    18000009 -> decorationConstructor, // Up Arrow Flag
    18000010 -> decorationConstructor, // Skull Altar
    18000011 -> decorationConstructor, // USA Flag
    18000012 -> decorationConstructor, // Canada Flag
    18000013 -> decorationConstructor, // Italia Flag
    18000014 -> decorationConstructor, // Germany Flag
    18000015 -> decorationConstructor, // Finland Flag
    18000016 -> decorationConstructor, // Spain Flag
    18000017 -> decorationConstructor, // France Flag
    18000018 -> decorationConstructor, // GBR Flag
    18000019 -> decorationConstructor, // Brazil Flag
    18000020 -> decorationConstructor, // China Flag
    18000021 -> decorationConstructor, // Norway Flag
    18000022 -> decorationConstructor, // Thailand Flag
    18000023 -> decorationConstructor, // Thailand Flag
    18000024 -> decorationConstructor, // India Flag
    18000025 -> decorationConstructor, // Australia Flag
    18000026 -> decorationConstructor, // South Korea Flag
    18000027 -> decorationConstructor, // Japan Flag
    18000028 -> decorationConstructor, // Turkey Flag
    18000029 -> decorationConstructor, // Indonesia Flag
    18000030 -> decorationConstructor, // Netherlands Flag
    18000031 -> decorationConstructor, // Philippines Flag
    18000032 -> decorationConstructor, // Singapore Flag
    18000033 -> decorationConstructor, // PEKKA Statue
    18000034 -> decorationConstructor, // Russia Flag
    18000035 -> decorationConstructor, // Russia Flag
    18000036 -> decorationConstructor, // Greece Flag

    8000000 -> decorationConstructor, // Pine Tree
    8000001 -> decorationConstructor, // Large Stone
    8000002 -> decorationConstructor, // Small Stone 1
    8000003 -> decorationConstructor, // Small Stone 2
    8000004 -> decorationConstructor, // Square Bush
    8000005 -> decorationConstructor, // Square Tree
    8000006 -> decorationConstructor, // Tree Trunk 1
    8000007 -> decorationConstructor, // Tree Trunk 2
    8000008 -> decorationConstructor, // Mushrooms
    8000009 -> decorationConstructor, // TombStone
    8000010 -> decorationConstructor, // Fallen Tree
    8000011 -> decorationConstructor, // Small Stone 3
    8000012 -> decorationConstructor, // Small Stone 4
    8000013 -> decorationConstructor, // Square Tree 2
    8000014 -> decorationConstructor, // Stone Pillar 1
    8000015 -> decorationConstructor, // Large Stone
    8000016 -> decorationConstructor, // Sharp Stone 1
    8000017 -> decorationConstructor, // Sharp Stone 2
    8000018 -> decorationConstructor, // Sharp Stone 3
    8000019 -> decorationConstructor, // Sharp Stone 4
    8000020 -> decorationConstructor, // Sharp Stone 5
    8000021 -> decorationConstructor, // Xmas tree
    8000022 -> decorationConstructor, // Hero TombStone
    8000023 -> decorationConstructor, // DarkTombStone
    8000024 -> decorationConstructor, // Passable Stone 1
    8000025 -> decorationConstructor, // Passable Stone 2
    8000026 -> decorationConstructor, // Campfire
    8000027 -> decorationConstructor, // Campfire
    8000028 -> decorationConstructor, // Xmas tree2013
    8000029 -> decorationConstructor, // Xmas TombStone
    8000030 -> decorationConstructor, // Bonus Gembox
    8000031 -> decorationConstructor, // Halloween2014
    8000032 -> decorationConstructor, // Xmas tree2014
    8000033 -> decorationConstructor, // Xmas TombStone2014
    8000034 -> decorationConstructor, // Npc Plant 1
    8000035 -> decorationConstructor, // Npc Plant 2
    8000036 -> decorationConstructor // Halloween2015
  )

  def build(raw: RawElement): Option[Element] = {
    Some(raw)
      .map(
        value => elementConstructorByCode.get(value.data)
          .orElse(throw new RuntimeException("No building with code " + raw.data))
          .get(raw)
      )
  }
}
