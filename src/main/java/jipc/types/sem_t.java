package jipc.types;

import jpassport.GenericPointer;

import java.lang.foreign.MemorySegment;

public class sem_t extends GenericPointer {
    public sem_t(MemorySegment addr) {
        super(addr);
    }
}
