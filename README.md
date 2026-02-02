# CBPP
CBPP is a statically typed language built for fun. It compiles to Brainfuck.

## Features

* Rust-inspired syntax
* (most) numeric & boolean operators
* Shorthand functions

## Not supported

### Due to limitations of brainfuck

* Gotos
* Pointers
* (Native) multi-byte integers
* Recursion

### Due to limitations of compiler

* Arrays (fixed size)
* Enums (unions)

## Examples

```
@ define vector struct
struct Vector2b {
  x: byte;
  y: byte;
}
@ define 2-arg constructor (overloaded from default)
mfn _Vector2b(x: byte, y: byte) -> Vector2b {
  var v: Vector2b = _Vector2b(); @ default constructor
  v.x = x;
  v.y = y;
  return v;
}
@ use native input function to generate vector
var vec: Vector2b = _Vector2b($input(), $input());
```
This function prints nothing and creates a vector object based on the ascii values of the user input.


More examples can be found in the "examples" folder.

## Compiler stages

The compiler is made of 7 stages:
* Scanning
* Preprocessing (macros)
* Parsing
* Compilation (IR 1)
* Lowering
* Compilation (IR 2)
* Compilation (Brainfuck)

## Syntax

### Variables

Variables are defined using the var keyword, followed by the name and the type. It must always be followed by an assignment.
`var t: byte = 1;`

There are 2 native types, byte and bool. All others must be defined using structs.

### Structs

Structs largely follow Rust syntax:
```
struct A {
  x: byte;
  y: byte;
}
```
### Functions

Functions are always inlined. Recursion is not possible.
Define functions using the "mfn" keyword. Functions largely follow Rust syntax, except implicit returns are not a thing. Instead, shorthand functions can be created using a colon.
`mfn f(x: byte) -> byte: x**2;`
### Comments

Line comments are created using the @ symbol.

### Preprocessor

The 'preprocessor' runs after lexing and is responsible for resolving macros and imports.
There are 7 commands:
* define (token) (string): creates a macro that replaces the token with the content of the string if it is found in the code
* undef (token): removes the macro
* ifdef (token) {(body)}: compiles the body ONLY if the macro exists
* ifndef (token) {(body)}: compiles the body ONLY if the macro does not exist
* native (string): inserts the string as IR2 (similar to assembly). Can be used for moving the pointer to a specific register
* native native (string): inserts the string as Brainfuck in the output
* import (string): inserts the file at address (string) in this file and scans it.

## How to use

Compile main.CBPP.java using java 25 (or higher) and run it. The command line arguments when running it should be:
1. The file that contains your code
2. The output file
All existing content in the output file is overridden.

## IMPORTANT

Some statements or expressions may function weirdly when compared to other languages:
* And/or operators do not short-circuit
* !!! Return statements do not exit the function !!!
```
mfn f() -> byte {
    return 1;
    return 2;
}
```
This function will return 2, not 1!

### Bugs

* Can't call member (dot) expressions directly on function results

## Changelog

(note: I have no idea how to make changelogs or version numbers)
v0.1.0:
* added core language
v0.1.1:
* added +=, etc assignment operators