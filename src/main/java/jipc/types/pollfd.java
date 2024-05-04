package jipc.types;

public record pollfd(int   fd,         /* file descriptor */
                     short events,     /* requested events */
                     short revents) {
}
