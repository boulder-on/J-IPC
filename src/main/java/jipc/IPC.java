package jipc;

import jipc.types.Semaphore;
import jipc.types.SharedMemory;
import jipc.types.mqd_t;
import jipc.types.pollfd;
import jipc.types.mq_attr;
import jpassport.GenericPointer;
import jpassport.PassportFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static jipc.ipc_native.*;

public class IPC {
    private static boolean inited = false;
    static ipc_native mem = null;

    public static void main(String[] args) throws Throwable
    {
        init();
    }

    public static synchronized void init() throws Throwable {
        if (inited)
            return;

        mem = PassportFactory.link(null, ipc_native.class);
        inited = true;
    }

    /**
     *
     * @param name The name of the shared memory file - must be a valid file name
     * @param size The number of bytes in shared memory
     * @param arena The arena that controls the lifetime of the memory. When the arena is closed the memory is de-allocated
     * @return The shared memory you can write into.
     * @throws Exception Any error opening the memory
     */
    public static SharedMemory openMemory(String name, int size, Arena arena) throws Exception {

        Path root_dir = Path.of("/tmp/shm");
        if (!Files.exists(root_dir))
            Files.createDirectories(root_dir);
        Path fullPath = root_dir.resolve(name);

        if (!Files.exists(fullPath))
            Files.write(fullPath, new byte[1], StandardOpenOption.CREATE);

        int segment_key = mem.ftok(fullPath.toFile().getAbsolutePath(), 'X');
        var segment_id = mem.shmget(segment_key, size, IPC_CREAT | 0666 );

        if (segment_id == -1) {
            mem.perror("shmget (" + segment_id +"):");
            throw new Exception("Could not get segment");
        }

        var shared_memory = mem.shmat(segment_id, GenericPointer.NULL(), 0);

        if (shared_memory.address() < 0) {
            mem.perror("shmat:");
            throw new Exception("Could not attach segment");
        }

        return new SharedMemory(segment_id, shared_memory.reinterpret(size, arena, null));
    }

    /**
     * Close a shared memory segment
     * @param memory The memory to close.
     */
    public static void closeMemory(SharedMemory memory) {
        if (mem.shmdt(memory.mem()) != 0)
            mem.perror("shmdt");
        if (mem.shmctl(memory.segmentid(), IPC_RMID, MemorySegment.NULL) != 0)
            mem.perror("shmctl");
    }

    /**
     * Open a named semaphore
     * @param name The name of the semaphore
     * @return The opened semaphore
     * @throws SemaphoreException On any error opening.
     */
    public static Semaphore openSemaphore(String name) throws SemaphoreException {
        return openSemaphore(name, 1);
    }

    /**
     * Opens s semaphore with a given initial count.
     * @param name The name of the semaphore
     * @param initialCount The count to start at
     * @return The open semaphore
     * @throws SemaphoreException Any error opening
     */
    public static Semaphore openSemaphore(String name, int initialCount) throws SemaphoreException {
        if (!name.startsWith("/") || name.substring(1).contains("/"))
            throw new SemaphoreException("Semaphore name must start with / and contain no other / (" + name + ")");
        if (name.length() > 252)
            throw new SemaphoreException("Semaphore name cannot be longer than 252 characters");

        var s = mem.sem_open(name, O_CREAT, 0777, initialCount);
        if (s.getPtr().address() == SEM_FAILED) {
            mem.perror("sem_open:");
            throw new SemaphoreException("Failed to create semaphore.");
        }

        return new Semaphore(s);
    }

    /**
     * Close a semaphore
     * @param s The semaphore to close.
     * @return True if it worked.
     */
    public static boolean closeSemaphore(Semaphore s) {
        if (mem.sem_close(s.semaphore()) != 0)
        {
            mem.perror("sem_close:");
            return false;
        }
        return true;
    }

    public static boolean waitSemaphore(Semaphore s, boolean blocking) {
        if (blocking && mem.sem_wait(s.semaphore()) == 0)
            return true;
        if (!blocking && mem.sem_trywait(s.semaphore()) == 0)
            return true;

        mem.perror("sem_wait:");
        return false;
    }

    public static boolean signalSemaphore(Semaphore s) {
        if(mem.sem_post(s.semaphore()) == 0)
            return true;

        mem.perror("sem_post:");
        return false;
    }

    public static int getValueSemaphore(Semaphore s)
    {
        int[] val = new int[1];
        if (mem.sem_getvalue(s.semaphore(), val) == 0)
        {
            return val[0];

        }

        mem.perror("sem_getvalue:");
        return -1;
    }

    public static mqd_t openQueue(String name, int maxMsg, int msgSizebytes)
    {
        if (!name.startsWith("/") || name.substring(1).contains("/"))
            throw new IllegalArgumentException("Queue name must start with / and contain no other / (" + name + ")");
        if (name.length() > 252)
            throw new IllegalArgumentException("Queue name cannot be longer than 252 characters");

        mq_attr attrs = new mq_attr(O_CREAT | O_RDWR | O_NONBLOCK,
                maxMsg, msgSizebytes, 0);

        var ret = mem.mq_open(name, O_CREAT | O_RDWR | O_NONBLOCK, 0666, attrs);
        if (ret.getPtr().address() == -1)
        {
            mem.perror("mq_open:");
            throw new IllegalArgumentException("Error creating queue");
        }
        return ret;
    }

    public static void closeQueue(mqd_t queue)
    {
        mem.mq_close(queue);
    }

    public static int readQueue(mqd_t queue, byte[] msg)
    {
        return mem.mq_receive(queue, msg, msg.length, null);
    }

    public static boolean writeQueue(mqd_t queue, byte[] msg, int len, int priority)
    {
        if (mem.mq_send(queue, msg, msg.length, priority) == 0)
            return true;

        mem.perror("mq_send");
        return false;
    }

//    public static boolean poll(int fd, int timeoutMS)
//    {
//        pollfd[] pollThese = new pollfd[1];
//        pollThese[0] = new pollfd(fd, POLLIN, (short)0);
//
//        return mem.poll(pollThese, 1, timeoutMS) > 0;
//    }

}
