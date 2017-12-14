#Building
Run `gradle build` from the source directory.

#Running the Solver
After you build, either run the solver as a Java program under `build`, as `edu.cmu.cs.cdm.sat.Main`
or find binary executable bundle in `.tar` and `.zip` format under `build/distributions`. After you
uncompress the archive, you can find the executable under `bin` called SatSolver.

#Usage
    `./SatSolver -input [path] (-output [path]) (-stepbystep) (-solver [simple | dpll | cdcl])`
 Specifically, all options other than input are optional. If you omit the output file, then all output is printed
 to the command line. If you omit the solver flag, `dpll` is used.
 
#Input Format

Provide input as CNF formulas. Pick any variable names and use `!` to signal negation. Write each clause on
its own line and use any whitespace character as delimiter between literals. Do not put whitespace between `!` and
the variable name.

See examples in the source directory as `test_cdcl` and `test_dpll`.

#Interactive Mode

Provide the command line flag `-stepbystep` to use interactive mode. The solver will then wait
for human input before proceeding at each step. Type `p` to the prompt to print out the current
assignment. `Enter` to step the solver.

#Output
The solver does not necessarily explore the entire state space, if it returns a solution with some variables
assigned as `none`, the CNF formula is satisfied regardless of what its value is.

#Using Different Solvers

Three algorithms are implemented. `simple` will take you to the most naive backtracking solution, `dpll` implements
the classical [dpll algorithm](https://en.wikipedia.org/wiki/DPLL_algorithm), and `cdcl` implements the more
modern [conflict driven clause learning](https://en.wikipedia.org/wiki/Conflict-Driven_Clause_Learning) algorithm used
by larger scale SAT solvers.