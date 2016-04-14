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

`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintVillage "I AM SPARTA!!1!"'`
`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintAttackPlacements "I AM SPARTA!!1!"'`


## Running dev version of site (automatically reloads on changes)

`sbt ~tomcat:start`

Available at http://localhost:8080/ . Run container:start each time something is changed


## Production deployment

Currently has an Elastic Beanstalk app set up. Package the war file:
 
`sbt package` 

then deploy it manually through the AWS console:

`target/scala-2.11/coc-base-analyser_2.11-0.1.war`


## General TODO
 - ui overhaul: select clan -> load players, select player, click go
 - Better spritesheet/rendering
   - http://clashofclansbuilder.com/build?latest=true (individual buildings - in proportion)
    - http://clashofclansbuilder.com/assets/builder/objects/158_13_toolbar.png
    - http://clashofclansbuilder.com/assets/builder/objects/158_1_toolbar.png
   - http://coc2.clashofclans-tools.com/coc-buildings-trans.png (not consistent scale)
     - http://www.clashofclans-tools.com/Layout-Builder
     - http://coc2.clashofclans-tools.com/coc-buildings-1-trans.png
     
 
 - switch to new, dedicated credentials of own
 - 3d render
 - trap access (if leadership go for it)
 - select rule set types for each TH - farm + arranged
 - pass, warning, fail levels (e.g. for minion anchors)
 - saved result with a url to be able to send someone (save serialised result in db which can be recalled and lasts one 
   day, ensure to inlude version number so can expire early if need)
 - dynamic sizing of canvas still a bit funky - find proper js solution


## TH8 TODO Rules
 - air defs should be a minimum distance apart
 - loon pathing
   - must be >= 3 defenses to go through to path to air defs
   - OR must be > certain distance
   - should also consider air trap placement
   - should also consider air sweeper placement
 - minimum 3 DGB possible spots (including diagonal)
 - sufficient empty space within walls to make attacker guess
 - minion anchors (warning only, no hard fail, once that functionality is built)
 - wb t junction warning
 

## TH8 TODO Rules once traps available
 - spring trap locations (resting on defenses)
 - skele traps not lurable
 - skele traps + air traps not within dgb positions (gives info for cleanup if first hit was with air)
 - 3 viable DGB spots (more difficult)


## TH9 Rules
 - Air defs not walkable/reachable from outside walls
 - EQ cant connect >2 GB/DGB positions + AQ
 - Jump doesnt connect too many AQ, GB
 - black bombs within range of queen or air def - to get hounds or suicide drags. red bombs out of range of air defs


## TH10 Rules
 - cant get 2 infernos with one freeze


## Expansion ideas
 - Hog pathing analysis - start paths from each tile and be able to select/see individual paths from defense to defense
   to show DGB issues
 - war day analysis - analyse all bases for above criteria, also if have CCs filled
 - multiple goals/rulesets:
   - farming (protected loot, give away easy shield - one star, but no value for more than)
   - war - depending on clan and level, this might be to prevent 1 star, prevent 2 star, or just prevent 3 star
   - trophy?
 - provide weaknesses for attack types. e.g. drags doesnt consider DGB locations, hogs dont consider air def high hp.