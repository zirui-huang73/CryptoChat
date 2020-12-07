# CryptoChat

## Introduction

CryptoChat is an instant message chat application allows two users to have a chat in an end-to-end encrypted environment.

## Usage

#### Compiling

To simplify things (so you don't need IDE to run the application), the project structure is kind of messy.

##### Server

```
$CryptoChat rm ChatServer/*.class
$CryptoChat javac -cp ".:./jars/commons-lang3-3.1.jar" ChatServer/*.java
```

**Client**

```
$CryptoChat rm ChatClient/*.class
$CryptoChat javac -cp ".:./jars/commons-lang3-3.1.jar" ChatClient/*.java
```

------

### Execution

Default IP address: 127.0.0.1

Deafult Port: 8080

##### Run Server

```
$CryptoChat java -cp ".:./jars/commons-lang3-3.1.jar" ChatServer.ServerMain
```

##### Run Client

```
$CryptoChat java -cp ".:./jars/commons-lang3-3.1.jar" ChatClient.ClientMain
```

*There is no restriciton on password. No duplicate login name is allowed (system can handle this).*

------

### Reference

- https://signal.org/docs/specifications/x3dh/

- https://blog.csdn.net/qq_28205153/article/details/55798628
- https://qvault.io/2020/07/08/how-sha-2-works-step-by-step-sha-256/
- http://www.moserware.com/2009/09/stick-figure-guide-to-advanced.html