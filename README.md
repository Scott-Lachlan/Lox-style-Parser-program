# Lox Style Parser Program
**Author:** Lachlan Scott 
 
---
 
## Getting Started
 
### How to Compile
 
1. Open the terminal (Visual Studio Code is recommended).
2. Navigate to the **parent directory** of the `river_lox` project folder.
3. Run the following command:
```bash
javac river_lox/*.java
```
 
### How to Run Programs
 
1. Compile the project first (see above).
2. Navigate to the **parent directory** of the `river_lox` project folder.
3. Run the following command:
```bash
java river_lox.Lox river_lox/program/<filename>.lox
```
 
> Try the command again using a different program from the `program` folder.
 
---
 
## Language Reference
 
| Question | Answer |
|---|---|
| What literal represents a river that gets 10L/s of flow on the first day after 1mm of rainfall? | A list literal: `[1, 10]` → `[rain mm, flow L/s]` |
| What symbol is used to show two rivers combine? | `join` — e.g. `join main(river1, river2)` |
| Is the above symbol unary, binary, or a literal? | It is a statement, but can also be referred to as a binary operation as it combines two inputs into one. |
| What is the working folder to compile the parser? | `river_lox` |
| What command(s) compile the parser? | `javac river_lox/*.java` (run from the parent directory) |
| How long does it take all water to work through a river system after 1 day of rain? | Water gained - flow, repeated per day until 0 |
| Does the language include statements or is it an expression language? | Yes, it has statements |
| Which chapters of the book were used as the starting point? | Chapters 3–8 |
 
---
 
## Grammar
 
```
program       → (declaration)* EOF
 
declaration   → varDecl | rivDecl | joinDecl | outDecl | listDecl | statement
 
varDecl       → "var" IDENTIFIER ("=" expression)? ";"
rivrDecl      → "riv" IDENTIFIER ("=" expression)? ";"
joinDecl      → "join" IDENTIFIER "(" IDENTIFIER ("," IDENTIFIER)* ")" ";"
outDecl       → "out" IDENTIFIER ";"
listDecl      → "list" IDENTIFIER "=" list ";"
 
statement     → printStmt | exprStmt | rainStmt
printStmt     → "print" expression ";"
exprStmt      → expression ";"
rainStmt      → "RAIN" "=" list ";"
 
list          → "[" (listElmt ("," listElmt)*)? "]"
listElmt      → list | expression
 
expression    → assignment
assignment    → (IDENTIFIER ("[" expression "]")? "=")? equality
equality      → comparison (("!=" | "==") comparison)*
comparison    → term (("<" | ">" | ">=" | "<=") term)*
term          → factor (("+" | "-") factor)*
factor        → unary (("*" | "/" | "->") unary)*
unary         → ("!" | "-") unary | primary
primary       → NUMBER | STRING | "true" | "false" | "nil" | "rain"
              | "(" expression ")"
              | IDENTIFIER ("[" expression "]")*
```
 
---
 
## Example Programs
 
### 1. Single Root River and Output
 
```
var area = [10]; riv r1 = rain; out r1;
```
 
Declares a list variable `area = [10]`, creates a river `r1` initialised to `rain`, then prints `r1` using `out`.
 
---
 
### 2. Join Two Feeders into a River
 
```
riv feeder1 = rain; riv feeder2 = rain; join mainRiver(feeder1, feeder2); out mainRiver;
```
 
Declares two feeder rivers each sourced from rain, then defines `mainRiver` as the combination of the two feeders and prints it.
 
---
 
### 3. List Declaration, Assignment and Rain Assignment
 
```
depths = [1, 2, [3, 4]]; var x = 5; RAIN = [10, 20, 30]; out x;
```
 
Sets up structured data (lists), updates the rainfall sequence, and demonstrates a normal variable output.
 
> The example programs in correct formatting can also be found in the `program` folder within the `river_lox` folder.
