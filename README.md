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


## Deployment

Currently has an Elastic Beanstalk app set up. Package the war file:
 
`sbt package` 

then deploy it manually through the AWS console:

`target/scala-2.11/coc-base-analyser_2.11-1.0.war`