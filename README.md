# CryptoChat

## Introduction

CryptoChat is an instant message chat application allows two users to have a chat in an end-to-end encrypted environment.

In this project, I create a message chat application that allows two users to have a chat in an end-to-end encrypted environment.

Features implemented:
1. Client-Server chat application. A chat application with a simple user-interface. Two users can start the chat by connecting to the server.
Clients and Server communicate through a simple application protocol, the message between client and server will include a header and message body seperated by ‘.’; 
For example, if a Client wants to send a message to peer, it will send “msg.xxxx” to Server; if Client wants to send its public key, it will send “publicKey.xxx” to Server. Both ends can parse the message with the help of StringUtils library.
2. Diffie-Hellman key exchange. To encrypt/decrypt the messages, a shared secret key is needed for both users. To achieve this, I’ve implemented DH key exchange. Upon connection, two users will exchange their randomly generated public keys. Based on the peer's public key and the user’s private key, a shared secret can then be generated.  The shared secret will be a 2048-bit key.
3. SHA256. Because AES-128 will be used to encrypt/decrypt the message, the shared secret from DH key exchange needed to be hashed into a 128-bit key. I implemented SHA256 to first hash the shared secret into 256-bit and use the first half of the result as the 128-bit AES key.
4. AES encryption algorithm. This is the core part of the project. The application encrypts/decrypts user messages with AES-128 encryption algorithm. I implemented AES-128 from scratch. 

Details about how the encryption works
1. Alice and Bob generate a public and private key pair
2. Alice and Bob share their public keys with each other
3. Alice and Bob generate the shared secret, and hash the secret into a 128-bit AES key.
4. Both user encrypt and decrypt the message with this AES key.

Limitation and Further Improvement
1. The current encryption algorithm only works for English (i.e. language whose character takes exactly one byte).  In the future, I may further improve the algorithm so it can work for whatever languages.

## Usage

#### Compiling

To simplify things (so you don't need IDE to run the application), the project structure is kind of messy.

##### Server

```
CryptoChat$ rm ChatServer/*.class
CryptoChat$ javac -cp ".:./jars/commons-lang3-3.1.jar" ChatServer/*.java
```

**Client**

```
CryptoChat$ rm ChatClient/*.class
CryptoChat$ javac -cp ".:./jars/commons-lang3-3.1.jar" ChatClient/*.java
```

------

### Execution

Default IP address: 127.0.0.1

Deafult Port: 8080

##### Run Server

```
CryptoChat$ java -cp ".:./jars/commons-lang3-3.1.jar" ChatServer.ServerMain
```

##### Run Client

```
CryptoChat$ java -cp ".:./jars/commons-lang3-3.1.jar" ChatClient.ClientMain
```

*There is no restriciton on password. No duplicate login name is allowed (system can handle this).*

------

### Demo

<img height="150" src="https://github.com/ziruih999/CryptoChat/blob/main/img/login.jpg"/>
<img height="200" src="https://github.com/ziruih999/CryptoChat/blob/main/img/chat.jpg"/>
<img height="200" src="https://github.com/ziruih999/CryptoChat/blob/main/img/server.jpg"/>

### Reference

- https://signal.org/docs/specifications/x3dh/

- https://blog.csdn.net/qq_28205153/article/details/55798628
- https://qvault.io/2020/07/08/how-sha-2-works-step-by-step-sha-256/
- http://www.moserware.com/2009/09/stick-figure-guide-to-advanced.html
