package jipc_test;

import jipc.IPC;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.*;

public class IPCTest {

    @BeforeAll
    public static void startup() throws Throwable {
        System.setProperty("jpassport.build.home", "out/testing");
        IPC.init();
    }

    @Test
    public void lockTest() throws Exception {

        try (var sem1 = IPC.openSemaphore("/testsem2");
            var sem2 = IPC.openSemaphore("/testsem2")) {

            assertEquals(1, IPC.getValueSemaphore(sem1));
            assertTrue(sem1.lock(false));
            assertFalse(sem2.lock(false));
            assertTrue(sem1.unlock());
            assertEquals(1, IPC.getValueSemaphore(sem1));
        }
    }

    @Test
    public void queueTest() throws Exception {
        try (var queue = IPC.openQueue("/testqueue2", 10, 1024);
                var queue2 = IPC.openQueue("/testqueue2", 10, 1024))
        {
            byte[] msg = new byte[1024];
            msg[0] = 1; msg[1] = 2;
            IPC.writeQueue(queue, msg, msg.length, 1);

            byte[] msgBack = new byte[msg.length];
            IPC.readQueue(queue2, msgBack);
            assertArrayEquals(msg, msgBack);
        }
    }

    @Test
    public void sharedMemTest() throws Exception
    {
        try (Arena a = Arena.ofConfined();
             var shm = IPC.openMemory("my_test_mem", 2048, a);
             var shm2 = IPC.openMemory("my_test_mem", 2048, a);
             )
        {
            shm.asByteBuffer().putInt(3000).putDouble(1000);

            var bb = shm2.asByteBuffer();
            assertEquals(3000, bb.getInt());
            assertEquals(1000, bb.getDouble());
        }
    }

}
