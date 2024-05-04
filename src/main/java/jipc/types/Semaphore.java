package jipc.types;

import jipc.IPC;

public class Semaphore implements AutoCloseable {

    public sem_t semaphore;
    private boolean held = false;

    public Semaphore(sem_t s)
    {
        semaphore = s;
    }

    @Override
    public void close() throws Exception {
        if (held)
            unlock();
        IPC.closeSemaphore(this);
    }

    public sem_t semaphore()
    {
        return semaphore;
    }

    public boolean lock(boolean blocking)
    {
        var ret = IPC.waitSemaphore(this, blocking);
        if (ret)
            held = true;
        return ret;
    }

    public boolean unlock()
    {
        return signal();
    }

    public boolean signal()
    {
        held = false;
        return IPC.signalSemaphore(this);
    }
}