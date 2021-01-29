# Cloud Database
Course project of ECE419, distributed system, at the University of Toronto. A distributed system for a cloud database. The Project is divide into 4 milestones.

## Getting Started

### Client & Server
For running test of the project
```
ant test
```
 In the echoClient folder, compile and build with:
 ```
 ant build-jar
 ```
 Follow by the command bellow to run:
 ```
java -jar ms1-server.jar <port> <cache_size> <cache_strategy>
java -jar ms1-client.jar
 ```
Your prompt should change to:
```
ms1-client>
```
and that's all set. 

You can play with it now.

### Jefferson
- Main (wheel)
- Server accept multiple client (multithreading)
- Logging (specify dir)
- Cache (None, LFU, LRU, FIFO)
  - Strategy class
  - Cache size: 4096
  - LRU remove use Java API

### Troy
- Dump evicted key to persistence storage
- implement read/write IO to storage file

### Junxuan
- Single threaded server client implementation
- socket communication library- message protocol design
- Server controller, implementation of server side communication tunnel
- parse client/server messagess

