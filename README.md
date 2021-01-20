# Cloud Database
Course project of ECE419, distributed system, at the University of Toronto. A distributed system for a cloud database. The Project is divide into 4 milestones.

## Getting Started
### Sever
In the echoSever folder, compile and build with:
 ```
 ant
 ```
 Follow by the command bellow to run:
 ```
 java -jar echoServer.jar <port number>
 ```
Port numbers between 1024 and 65536 should be used (pick a random port to avoid conflicting with other services.) If you cannot start the server because “Port is already bound!” use a different port number. Avoid hard-coding port numbers for this reason.

### Client

 In the echoClient folder, compile and build with:
 ```
 ant
 ```
 Follow by the command bellow to run:
 ```
java -jar echoClient.jar
 ```
Your prompt should change to:
```
EchoClient>
```
and that's all set. 

You can play with it now.

## Milestone 1 Client and Persistent Storage Server
Folder: M1
