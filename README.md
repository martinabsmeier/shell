# Shell
Shell is a small Java library enabling simple creation of interactive command-line user interfaces.

It uses metadata and Java Reflection to determine which class methods should be exposed to end user and to provide info 
for user. Therefore, all information related to specific command is kept in only one place: in annotations in method's 
header. User don't have to organise command loop, write complicated parsers/converters for primitive types, though he 
can implement custom converters when needed. 

## Project History
The original project was developed by Anton Grigoryev and the code can be found at [Google code](https://code.google.com/p/cliche/)

## Quick start