package org.danielholmes.coc.baseanalyser.model.troops

import org.danielholmes.coc.baseanalyser.model.Block
import org.danielholmes.coc.baseanalyser.model.defense.{AirDefense, WizardTower}

case class WizardTowerHoundTargeting(tower: WizardTower, airDefense: AirDefense, houndTarget: Block)
