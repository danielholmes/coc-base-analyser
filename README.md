# Clash of Clans Base Analyser

Analyses base layouts against sets of war base rules. It runs this analysis in bulk and provides tabulated results for
the current war bases for each member in a clan. Additional to this you can click through to an individual's base to
see a more in depth analysis.

Analysis is only allowed for pre-configured clans in the app which are assigned an alias for convenience.


## Dependencies

 - SBT
 - JDK 8+
 - Scala 2.11
 
To find available SBT dependency updates run `sbt dependencyUpdates`
 

## Tests

 - All: `sbt test`
 - Individual: `sbt "test-only org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParserSpec"`
 - Individual continuous: `sbt ~"test-only org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParserSpec"`


## Command Line Utils

`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintVillage alpha "I AM SPARTA!!1!"'`
`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintAttackPlacements alpha "I AM SPARTA!!1!"'`
`sbt 'run-main org.danielholmes.coc.baseanalyser.ProfileAnalysis alpha "I AM SPARTA!!1!"'`


## Running dev version of site (automatically reloads on changes)

`sbt ~tomcat:start`

Available at [http://localhost:8080](http://localhost:8080)

Accessing a clan e.g. [http://localhost:8080/clans/alpha](http://localhost:8080/clans/alpha)


## Production Build

`sbt package` 

then deploy the war as required:

`target/scala-2.11/coc-base-analyser_2.11-0.1.war`


## Game Connection

The app requires a connection to the Supercell servers to query the village json and other clan members. Note that this 
isn't referring to the official [Clash of Clans API](https://developer.clashofclans.com/), but a direct connection to
the game servers how the game does. Once upon a time this project used a product called "Clan Seeker" which has since
been discontinued. It's trivial however to write an agent for the app if there's another such service out there:

 1. Write a new game connection agent that implements the 
   [GameConnection Trait](src/main/scala/org/danielholmes/coc/baseanalyser/gameconnection/GameConnection.scala)
 2. Wire in the game connection in the
   [Services Trait](src/main/scala/org/danielholmes/coc/baseanalyser/Services.scala)

At the moment there's a hardcoded stub/testing GameConnection with dummy data


## Rules

The project is very much in a WIP state. Town Hall 8 is pretty fleshed out but TH9 and above don't have many rules.
Also these were modelled off of attack and base building meta from around February 2016. Some newer buildings are also 
not present such as the Bomb Tower.

Rules are pretty quick to build due to the building blocks and infrastructure of a modelled base being in place.


## Screenshots

![Clan Homepage](docs/images/home-1.png?raw=true "Clan Home")
![Base Analysis 1](docs/images/base-1.png?raw=true "Base Analysis 1")
![Base Analysis 2](docs/images/base-2.png?raw=true "Base Analysis 2")
![Base Analysis 3](docs/images/base-3.png?raw=true "Base Analysis 3")
![Base Analysis 4](docs/images/base-4.png?raw=true "Base Analysis 4")
![Bulk Analysis](docs/images/bulk-1.png?raw=true "Bulk Analysis")