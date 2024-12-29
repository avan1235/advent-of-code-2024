# 🎄🎁🎅 2024 Advent of Code in Kotlin 🎅🎁🎄

## Project goals

The project goal is to deliver some pretty, readable and concise solutions to Advent of Code 2024 problems all written
in Kotlin language. It should show the other developer how some constructions from the language can be used and how to
solve some kind of tricky problems that appear during the Advent of Code.

## Problems source

You can find all problems at the [page of Advent of Code 2024](https://adventofcode.com/2024). The description of each
problem contains some sample test data, but I also included my input data files from the contest in
the [input](./runner/input) directory of the project to make my project working with some sample, real
world data.

## Solution template

This repository makes use of the [Advent of Code in Kotlin](https://github.com/avan1235/advent-of-code-kotlin)
library.
It contains all days' solutions in [solutions](./solutions/src/commonMain/kotlin) project.

To run the days' solutions one can use `./gradlew :runner:jvmRun`. To run tests for them one can use `./gradlew :runner:jvmTest`.

To build JVM version of UI solver one can use `./gradlew :solver:run`. To build the browser version one can use `./gradlew :solver:wasmJsBrowserRun`.
