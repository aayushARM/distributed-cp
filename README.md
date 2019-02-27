# Distributed Consumer-Producer Implementation With TCP/Sockets and RMI

This project implements the Distributed Consumer-Producer design wherein all the three types of entities: Producers, Consumers and the Server governing communication between the two can run on different machines.
Two types of implementations are developed:
* First uses TCP/Sockets, Streams and Multithreading with Synchronization for all types of back and forth communication.
* The second uses Java's Remote Method Invocation API. The Storage class used by Server extends a UnicastRemoteObject, and consists of Remotely-invocable Synchronized methods which are called used by Producers and Consumers on different machines.
