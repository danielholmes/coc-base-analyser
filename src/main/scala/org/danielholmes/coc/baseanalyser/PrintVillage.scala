package org.danielholmes.coc.baseanalyser

object PrintVillage extends App with Services {
  if (args.length != 2) {
    throw new RuntimeException("Must provide clan code and userName arg")
  }

  val clanCode = args(0)
  val userName = args(1)

  val output = permittedClans.find(_.code == clanCode)
    .map(clan =>
      clanSeekerServiceAgent.getClanDetails(clan.id)
        .map(clanDetails =>
          clanDetails.players.find(_.avatar.userName.equalsIgnoreCase(userName))
            .map(_.avatar.currentHomeId)
            .map(userId =>
              clanSeekerServiceAgent.getPlayerVillage(userId)
                .map(_.village.raw)
                .map(villageJsonParser.parse)
                .map(
                  _.war
                    .map(stringDisplayer.buildColoured)
                    .getOrElse(s"User $userName has no war village")
                )
                .getOrElse("Error communicating with servers")
            )
            .getOrElse(s"Player $userName doesn't exist")
        )
        .getOrElse("Error communicating with servers")
    )
    .getOrElse(s"Clan code $clanCode not found")
  println(output)
}
