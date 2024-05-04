package jipc.types;

import jipc.IPC;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;

public record SharedMemory(int segmentid, MemorySegment mem) implements AutoCloseable {
    @Override
    public void close() throws Exception {
        IPC.closeMemory(this);
    }

    public ByteBuffer asByteBuffer()
    {
        return mem.asByteBuffer();
    }

//    public void waitfor(byte b)
//    {
//        int c = 0;
//        while (mem.get(ValueLayout.JAVA_BYTE, 0) != b)
//        {
//            if (c++ > 10000)
//                Thread.yield();
//        }
//    }
//
//    public void signalWith(byte b)
//    {
//        mem.set(ValueLayout.JAVA_BYTE, 0, b);
//    }


}
