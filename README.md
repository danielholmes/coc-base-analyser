# COC Base Analyser

Analyses base layouts against sets of war base rules


## Dependencies

 - SBT
 - JDK 8+
 - Scala 2.11
 

## Tests

- `sbt test`
- `sbt "test-only org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParserSpec"`


## Simple ASCII print util

`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintVillage "I AM SPARTA!!1!"'`
`sbt 'run-main org.danielholmes.coc.baseanalyser.PrintAttackPlacements "I AM SPARTA!!1!"'`


## Running dev version of site

```
sbt
container:start
```
Available at http://localhost:8080/ . Run container:start each time something is changed