package jipc.types;

public record mq_attr(long flags, long maxmsg, long msgsize, long curmsg) {
}
