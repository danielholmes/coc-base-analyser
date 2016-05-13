# COC Base Analyser

Analyses base layouts against sets of war base rules


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

Available at (http://localhost:8080)


## Production deployment

Currently has an Elastic Beanstalk app set up. Package the war file:
 
`sbt package` 

then deploy it manually through the AWS console:

`target/scala-2.11/coc-base-analyser_2.11-0.1.war`


## General TODO
 - own connection
  - purchase from alex
  - set up on own small EB app
 - analysis performance, currently too slow
   - MapCoordinate trait with underlying FloatMapCoordinate, TileCoordinate, Tile - encourage integer math where possible and prevent widening
   - Views of tile sets (e.g. TileBlock returned from matrix
   - Redo ranges - should include underlying cached set of tiles + tilecoordinates contained
 
 - gzip css, etc
 
 - double check sweeper angles being rendered same as in game once clan seeker up
 - Some TH9 rules
  - Queen Charge into wall breakable compartment shouldnt get to 2 air defs
  - It should require either a jump spell or 2 wall breaker groups in order to access the queen.
 
 - on equidistant hog lure, mark it as such or ignore it all together (maybe a pink line showing equidistant, non luring alternative path
 
 - TH10s without infernos should go under TH 9.5 rules?
 
 - separate hole in the base rule - just to highlight really bad issues (due to ignoring some others)
 
 - expand possible trap locations for channel bases. e.g. see spandan and vicious 2.0 an sparta home base
 
 - Begin on DGB:
  - class PossibleDoubleGiantBomb(anchors: (Either[Defense, PossibleTrapLocation], Either[Defense, PossibleTrapLocation]), gbs: (PossibleTrapLocation, PossibleTrapLocation))
 
 - BK Trigger rule further tweaks. should show red for all non-compartment tiles floodfilled from triggered
 
 - clarify AQ range - see iphoto screenshot of greg raid. possibly shown on ppetes war base
 
 - trap access (if leadership go for it) - new, dedicated credentials of own
 
 - include obstacles? affect some rules such as empty space analysis within base
 
 - TH11 rendering - new levels and warden + eagle
 - maintenance mode for site - different runRoute if connection to game unavailable
 
 - sbt deploy task
 - 3d render
  - split map display 2d apart. New inner stage container - drawLine(tile1, tile2) which transforms to 2d or 3d view
 - separate rule groups for farm vs arranged
 - pass, warning, fail levels (e.g. for minion anchors)
 - dynamic sizing of canvas still a bit funky - find proper js solution
 - integration tests
 - password protection (in the wrong hands opposition would see our trap locations)


## TH8 TODO Rules
 - air defs should be a minimum distance apart
 - SAMs not next to each other - one kills a dragon
 - loon pathing
   - must be >= 3 defenses to go through to path to air defs
   - OR must be > certain distance
   - should also consider air trap placement
   - should also consider air sweeper placement
 - minimum 3 DGB possible spots (including diagonal)
 - minion anchors (warning only, no hard fail, once that functionality is built)
 - wb t junction warning
 

## TH8 TODO Rules once traps available
 - spring trap locations (resting on defenses)
 - skele traps should be on ground and not triggerable during cc lure
 - skele traps + air traps not within dgb positions (gives info for cleanup if first hit was with air)
 - 3 viable DGB spots (more difficult)
 - farm wars - teslas in diff compartment for gowipe
 - trash buildings in front of all outer ring defenses


## TH9 Rules
 - EQ cant connect >2 GB/DGB positions + AQ
 - Jump doesnt connect too many AQ, GB
 - queen needs to be protected from “suicide dragons”
   Specifically, an air sweeper pointed to protect the queen, or (more commonly) a black mine between the queen and the likely dragon entry point
 - black bombs within range of queen or air def - to get hounds or suicide drags. red bombs out of range of air defs
 - The defenses around your DGB should be more than 4 tiles from an exterior wall
   Ensures the defenses aren’t eliminated using a queen walk


## TH10 Rules
 - cant get 2 infernos with one freeze


## Expansion ideas
 - Hog pathing analysis - start paths from each tile and be able to select/see individual paths from defense to defense
   to show DGB issues
 - multiple goals/rulesets:
   - farming (protected loot, give away easy shield - one star, but no value for more than)
   - war - depending on clan and level, this might be to prevent 1 star, prevent 2 star, or just prevent 3 star
   - trophy?
 - provide weaknesses for attack types. e.g. drags doesnt consider DGB locations, hogs dont consider air def high hp.
 - queen walk pathing from drop point
