package jipc;

import jipc.types.mq_attr;
import jipc.types.mqd_t;
import jipc.types.sem_t;
import jpassport.GenericPointer;
import jpassport.Passport;
import jpassport.annotations.RefArg;

import java.lang.foreign.MemorySegment;

public interface ipc_native  extends Passport {
    int IPC_CREAT = 01000;
    int IPC_RMID = 0;
    int O_CREAT=64;
    int SEM_FAILED=0;
    int O_RDWR=2;
    int O_NONBLOCK=04000;
    short POLLIN = 0x001;

    /**
     * <a href="https://man7.org/linux/man-pages/man3/perror.3.html">...</a>
     * @param prePend - The name of the method will be prepended to the error string in stderr
     */
    void perror(String prePend);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/ftok.3.html">...</a>
     * @param pathname The name of an existing and accessible file
     * @param proj_id An integer of your choice.
     * @return -1 on error. Otherwise, a token that can be used with System V IPCs
     */
    int ftok(String pathname, int proj_id);

    /**
     *<a href="https://man7.org/linux/man-pages/man2/shmget.2.html">...</a>
     * @param key From ftok
     * @param size The size of the memory segment to open
     * @param shmflg Creation flags
     * @return The segment ID for the memory
     */
    int shmget(int key, int size, int shmflg);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/shmat.3p.html">...</a>
     * @param shmid The segment ID from shmget
     * @param shmaddr RTFM - null works
     * @param shmflg RTFM - 0 works
     * @return The shared memory segment
     */
    MemorySegment shmat(int shmid, GenericPointer shmaddr, int shmflg);

    /**
     *<a href="https://man7.org/linux/man-pages/man3/shmdt.3p.html">...</a>
     * @param shmaddr The memory segment to close
     * @return 0 for success
     */
    int shmdt(MemorySegment shmaddr);

    /**
     * <a href="https://man7.org/linux/man-pages/man2/shmctl.2.html">...</a>
     * @param shmid The memory to close
     * @param cmd RTFM
     * @param buf RTFM - null
     * @return 0 on success, -1 on error.
     */
    int shmctl(int shmid, int cmd, MemorySegment buf);

    /**
     *<a href="https://man7.org/linux/man-pages/man3/sem_open.3.html">...</a>
     * @param name Name of the semaphore to open
     * @param oflag Open flags (RTFM)
     * @param mode Mode flags (RTFM)
     * @param value The opened semaphore
     * @return The ID of the semaphore.
     */
    sem_t sem_open(String name, int oflag, int mode, int value);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/sem_close.3.html">...</a>
     * @param sem The semaphore to close
     * @return 0 on success
     */
    int sem_close(sem_t sem);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/sem_post.3.html">...</a>
     * @param sem the semaphore to increment
     * @return 0 on success
     */
    int sem_post(sem_t sem);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/sem_wait.3.html">...</a>
     * @param sem The semaphore to wait on.
     * @return 0 on success
     */
    int sem_wait(sem_t sem);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/sem_trywait.3p.html">...</a>
     * @param sem The semaphore to wait on
     * @return 0 on success
     */
    int sem_trywait(sem_t sem);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/sem_getvalue.3.html">...</a>
     * @param sem The semahore to query
     * @param sval Returns the value of the semaphor
     * @return 0 on success
     */
    int sem_getvalue(sem_t sem, @RefArg int[] sval);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/mq_open.3.html">...</a>
     * @param name The name of the queue to open
     * @param oflag Open flags (RTFM)
     * @param mode Mode flags (RTFM)
     * @param atts Creation attributes (RTFM)
     * @return The opened queue
     */
    mqd_t mq_open(String name, int oflag, int mode, mq_attr atts);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/mq_send.3.html">...</a>
     * @param mq The queue to send on
     * @param buf The buffer to pass on the queue
     * @param msglen The length of the buffer to pass
     * @param msg_priority The priority
     * @return 0 on success
     */
    int mq_send(mqd_t mq, byte[] buf, int msglen, int msg_priority);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/mq_receive.3.html">...</a>
     * @param mq The queue to receive on
     * @param buf The buffer to receive into
     * @param msglen The length of the buffer
     * @param priority The priority used (null is fine)
     * @return 0 on success
     */
    int mq_receive(mqd_t mq, @RefArg(read_back_only=true) byte[] buf, int msglen, @RefArg(read_back_only = true) int[] priority);

    /**
     * <a href="https://man7.org/linux/man-pages/man3/mq_close.3.html">...</a>
     * @param mq The queue to close
     * @return 0 on success
     */
    int mq_close(mqd_t mq);


//    int poll(pollfd[] fds, int nfds, int timeoutMS);

}
