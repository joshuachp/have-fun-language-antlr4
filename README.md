# have-fun-language

Interpreter written with the ANTLR parser.

The goal of the project is to implement a new imperative language named HaveFun
(named after the function declaration).

The specifics are in the `doc/` folder.

## Run

The build system used is gradle do to run it:

```bash
gradle run
```

In the `src/main/resource` folder there is a `program.txt` file were you can
write the program that will be executed.

## Test

There are different test programs, to run all of them:

```bash
gradle test
```

The test are divided in `well-typed` for which we check if the program produces
the correct output and `bad-typed` which will make the program panic.
