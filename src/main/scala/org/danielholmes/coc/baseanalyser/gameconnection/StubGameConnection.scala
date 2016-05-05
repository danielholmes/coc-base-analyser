package org.danielholmes.coc.baseanalyser.gameconnection

import org.danielholmes.coc.baseanalyser.gameconnection.ClanSeekerProtocol._
import org.danielholmes.coc.baseanalyser.web.PermittedClan

class StubGameConnection(private val permittedClans: Set[PermittedClan]) extends GameConnection {
  def getClanDetails(id: Long): Option[ClanDetails] = {
    permittedClans.find(_.id == id)
      .filter(_.code != "uncool")
      .map(clan =>
        ClanDetails(
          clan.name,
          Set(
            PlayerSummary(AvatarSummary("Dakota", id + 1L, id)),
            PlayerSummary(AvatarSummary("kottonmouth", id + 2L, id)),
            PlayerSummary(AvatarSummary("Valaar", id + 3L, id)),
            PlayerSummary(AvatarSummary("Mesoscalevortex", id + 4L, id)),
            PlayerSummary(AvatarSummary("Kajla", id + 5L, id)),
            PlayerSummary(AvatarSummary("a Noob", id + 6L, id)),
            PlayerSummary(AvatarSummary("Ricochet", id + 7L, id)),
            PlayerSummary(AvatarSummary("Lazy Ninja", id + 8L, id)),
            PlayerSummary(AvatarSummary("san", id + 9L, id)),
            PlayerSummary(AvatarSummary("Robbie", id + 10L, id)),
            PlayerSummary(AvatarSummary("Kendrall", id + 11L, id)),
            PlayerSummary(AvatarSummary("SpikeDragon", id + 12L, id)),
            PlayerSummary(AvatarSummary("Jamie", id + 13L, id)),
            PlayerSummary(AvatarSummary("joshua", id + 14L, id)),
            PlayerSummary(AvatarSummary("Kiara Kong", id + 15L, id)),
            PlayerSummary(AvatarSummary("ice ice baby", id + 16L, id)),
            PlayerSummary(AvatarSummary("sp@nd@n14", id + 17L, id)),
            PlayerSummary(AvatarSummary("Diaz", id + 18L, id)),

            PlayerSummary(AvatarSummary("I AM SPARTA!!1!", id + 100L, id)),
            PlayerSummary(AvatarSummary("rektscrub", id + 102L, id)),
            PlayerSummary(AvatarSummary("Darth Noobus", id + 103L, id)),
            PlayerSummary(AvatarSummary("greg", id + 104L, id)),
            PlayerSummary(AvatarSummary("Max", id + 105L, id)),
            PlayerSummary(AvatarSummary("ppete", id + 106L, id)),
            PlayerSummary(AvatarSummary("Vicious", id + 107L, id)),
            PlayerSummary(AvatarSummary("Riggs", id + 108L, id)),

            PlayerSummary(AvatarSummary("Some Mini", id + 1000L, id))
          )
        )
      )
  }

  private def villageJson(name: String): String = {
    io.Source.fromInputStream(getClass.getResourceAsStream("/examples/" + name)).mkString
  }

  def getPlayerVillage(id: Long): Option[PlayerVillage] = {
    permittedClans.map(_.id)
      .map(getClanDetails(_))
      .filter(_.isDefined)
      .map(_.get)
      .flatMap(_.players)
      .find(_.avatar.currentHomeId == id)
      .map(_.avatar)
      .map(
        avatarSummary =>
          if (id < avatarSummary.clanId + 100L) {
            PlayerVillage(
              avatarSummary,
              RawVillage(villageJson("th8-sample-1.json"))
            )
          } else if (id < avatarSummary.clanId + 1000L) {
            PlayerVillage(
              avatarSummary,
              RawVillage(villageJson("th9-sample-1.json"))
            )
          } else {
            PlayerVillage(
              avatarSummary,
              RawVillage(villageJson("th5-sample-1.json"))
            )
          }
      )
  }
}
