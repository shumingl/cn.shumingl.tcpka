package cn.shumingl.tcpka.object;

public interface IResource<T> {
    T take();
    void release();
    void destroy();
    boolean isUsed();
    boolean isAvailable();
}
