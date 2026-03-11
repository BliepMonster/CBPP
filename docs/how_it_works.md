# How this compiler works

Making a compiler like this is a lot simpler than you may think. 

The compiler is divided into seven segments, each of which has a separate function in gradually lowering the level of the language.

## Scanner

If you know anything about compilers, you know what this is.

The scanner reads the input file and divides it into *tokens*. Tokens are objects that store:
* The type of token (PLUS, NUMBER, WHILE, IDENTIFIER, etc)
* The source line 
* The string that corresponds with the token.