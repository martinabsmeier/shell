## Shell
Shell is a small Java library enabling really simple creation of interactive command-line user interfaces.

It uses metadata and Java Reflection to determine which class methods should be exposed to end user and to provide info for user. Therefore all information related to specific command is kept in only one place: in annotations in method's header. User don't have to organize command loop, write complicated parsers/converters for primitive types, though he can implement custom converters when needed.

### Project History
The original project was developed by Anton Grigoryev and the code can be found at https://code.google.com/p/cliche/

### Command to scan project
```shell
mvn sonar:sonar \
-Dsonar.projectKey=shell \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=e31443f2e71a5e0a5221a23639d7ca5d3b64fa02
```