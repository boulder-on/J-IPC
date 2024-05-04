package jipc.types;
import jipc.IPC;
import jpassport.GenericPointer;

import java.lang.foreign.MemorySegment;

public class mqd_t  extends GenericPointer implements AutoCloseable {
    public mqd_t(MemorySegment addr) {
        super(addr);
    }

    @Override
    public void close() throws Exception {
        IPC.closeQueue(this);
    }
}
