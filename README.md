# COC Base Analyser

Analyses base layouts against sets of war base rules


## Dependencies

 - SBT
 - JDK 8+
 - Scala 2.11
 

## Tests

- `sbt test`
- `sbt "test-only org.danielholmes.coc.baseanalyser.baseparser.VillageJsonParserSpec"`


## Running simple print util

```
sbt 'run "I AM SPARTA!!1!"'
```


## Running dev version of site

```
sbt
container:start
```
Available at http://localhost:8080/ . Run container:start each time something is changed