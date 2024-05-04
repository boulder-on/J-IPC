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

Usage:

``` Java
// Autoclose will auto unlock as well
try (var sem1 = IPC.openSemaphore("/testsem2");) {

    if (sem1.lock(false))
    {
        //do privileged work
        
        sem1.unlock();            
    }
    assertTrue(sem1.unlock());
}

try (Arena a = Arena.ofConfined();
     SharedMemory shm = IPC.openMemory("my_test_mem", 2048, a);)
{
    shm.asByteBuffer().putInt(3000).putDouble(1000);
}

try (var queue = IPC.openQueue("/testqueue", 10, 1024);)
{
    byte[] msg = new byte[1024];
    msg[0] = 1; msg[1] = 2;
    IPC.writeQueue(queue, msg, msg.length, 1);
}

```