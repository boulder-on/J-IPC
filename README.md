# J-IPC

This is a library that gives access to Linux native IPC mechanisms in Java. 
This library will only work on Linux. 

This library relies on JPassport to wrap the Java Foreign Function and Memory API. 
In some testing I did against C code, this implementation is
very competative with pure C code.

**The version of JPassport that this relies on requires Java 22!**

The primitives you have access to are:

* Semaphores
* Shared Memory
* Shared Queues