package cn.shumingl.tcpka.object;

public interface IResource<T> {
    T take();
    void destroy();
    boolean isAvailable();
}
