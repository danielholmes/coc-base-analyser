# COC Base Analyser

Analyses base layouts against sets of war base rules


## Dependencies

 - SBT
 - JDK 8+
 - Scala 2.11
 

## Tests

- `sbt test`
- `sbt "test-only org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParserSpec"`


## Simple ASCII print utils

`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintVillage "I AM SPARTA!!1!"'`
`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintAttackPlacements "I AM SPARTA!!1!"'`


## Running dev version of site

```
sbt
container:start
```
Available at http://localhost:8080/ . Run container:start each time something is changed


## Production deployment

Currently has an Elastic Beanstalk app set up. Package the war file:
 
`sbt package` 

then deploy it manually through the AWS console:

`target/scala-2.11/coc-base-analyser_2.11-1.0.war`


## General TODO
 - 3d render
 - trap access (if leadership go for it)
 - mobile device/small size compatibility (including dynamic canvas size)
 - make it more apparent that need to open accordian to see problems (maybe open first failed)
 - green/red tick for accordian headers
 - overall summary, e.g. "you have passed 1/5 rules, see below"
 - pass, warning, fail levels (e.g. for minion anchors)
 - saved result with a url to be able to send someone (save serialised result in db which can be recalled and lasts one 
   day, ensure to inlude version number so can expire early if need)


## TH8 TODO Rules
 - minimum # compartments - defend against gowipe
 - minion anchors (warning only, no hard fail, once that functionality is built)
 - minion+loon hitting mortars and cannons without air coverage
 - BK not swappable
 - minimum 3 DGB possible spots (including diagonal)
 - sufficient empty space within walls to make attacker guess
 - warnings for upgrading, e.g. build new traps asap if not maxed
 - wb t junction warning
 

## TH8 TODO Rules once traps available
 - spring trap locations (resting on defenses)
 - skele traps not lurable
 - loon pathing? e.g. must be >= 3 defenses to go through to path to air defs
 - skele traps + air traps not within dgb positions (gives info for cleanup if first hit was with air)
 - 3 viable DGB spots (more difficult)


## TH9 Rules
 - most of TH8 rules
 - EQ cant connect >2 GB/DGB positions + AQ
 - Jump doesnt connect too many AQ, GB
 - Air defs not walkable/reachable from outside walls


## Expansion ideas
 - Hog pathing analysis - start paths from each tile and be able to select/see individual paths from defense to defense
   to show DGB issues
 - war day analysis - analyse all bases for above criteria, also if have CCs filled