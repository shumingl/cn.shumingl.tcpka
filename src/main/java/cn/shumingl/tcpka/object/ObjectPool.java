package cn.shumingl.tcpka.object;

import cn.shumingl.tcpka.logger.LogConsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectPool<T extends ObjectWrapper> implements Iterable<T> {

	private static final LogConsole logger = LogConsole.getLogger(ObjectPool.class);
	private static final AtomicInteger poolidx = new AtomicInteger(1);
	private final AtomicInteger objectidx = new AtomicInteger(0);

	private List<T> objectList;
	private String prefix;
	private long timeout = -1;
	private int size = 0;

	public ObjectPool(int size) {
		init(size, -1);
	}

	public ObjectPool(int size, long timeout) {
		init(size, timeout);
	}

	/**
	 * 初始化
	 * 
	 * @param size 对象数量
	 * @param timeout 获取对象的超时时间
	 */
	private void init(int size, long timeout) {
		this.size = size;
		this.timeout = timeout;
		objectList = new ArrayList<>(this.size);
		prefix = String.format("ObjectPool/%03d.", poolidx.getAndIncrement());
	}

	public int size() {
		return size;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void put(T object) {
		object.setName(String.format(prefix + "%03d", objectidx.getAndIncrement()));
		// logger.info("ObjectPool.put -> [%s]", object.getName());
		objectList.add(object);
	}

	/**
	 * 获取Object
	 * @return
	 */
	public T get() {
		T result = null;
		long start = System.currentTimeMillis();
		while (true) {
			for (T object : objectList) {
				synchronized (object) {// 临界区加锁
					if (!object.isBusy()) {// 寻找空闲Object
						result = object;
						result.setBusy(true);
						break;
					}
				}
			}
			if (result != null) {// 找到空闲Object，没找到会继续等，直到超时
				synchronized (result) {// 临界区加锁
					if (!result.isAlive())// 激活Object
						result.activate();
				}
				// logger.info("ObjectPool.get -> [%s]", result.getName());
				return result;
			}
			// 超时则返回null
			if (timeout > 0 && System.currentTimeMillis() - start > timeout)
				throw new RuntimeException("Timeout when get object.");
		}
	}

	public void shutdown() {
		if (objectList == null)
			return;
		for (T object : objectList) {
			if (object.isBusy())
				logger.info("OBJECT[%s] is busy.", object.getName());
			while (object.isBusy()) { // 如果对象正在使用中则等待
			}
			object.destroy();// 销毁对象
		}
		objectList.clear();
	}

	public void shutdownNow() {
		if (objectList == null)
			return;
		for (T object : objectList) {
			while (object.isAlive()) {// 销毁对象
				object.destroy();
			}
		}
		objectList.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return objectList.iterator();
	}
}
