package cn.shumingl.tcpka.object;

public interface ObjectWrapper {
	boolean isBusy();

	void setBusy(boolean busy);

	boolean isAlive();

	void activate();

	void destroy();

	String getName();

	void setName(String name);
}
