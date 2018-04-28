package cn.shumingl.tcpka.logger;

import org.joda.time.DateTime;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制台日志类，用于没有日志框架情况下的日志输出
 * @author shumingl
 */
public class LogConsole {

	private static final PrintStream stdout = System.out;
	private static final PrintStream errout = System.err;
	private static final String LONG_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String LOGGER_TEMPLATE = "%1$s %2$-5s %3$s - %4$s";
	private static final LogLevel defaultLevel = LogLevel.INFO;
	private LogLevel currentLevel = defaultLevel;
	private String logger;
	private static final ConcurrentHashMap<String, LogConsole> classes = new ConcurrentHashMap<>();

	private LogConsole(String logger) {
		this.logger = logger;
	}

	public static LogConsole getLogger(Class<?> clazz) {
		String loggerClass = clazz.getCanonicalName();
		if (classes.containsKey(loggerClass)) { // 有则取
			return classes.get(loggerClass);
		} else { // 没找到则新增
			classes.put(loggerClass, new LogConsole(loggerClass));
			return classes.get(loggerClass);
		}
	}

	private LogLevel getCurrentLevel() {
		return currentLevel;
	}

	private void setCurrentLevel(LogLevel currentLevel) {
		this.currentLevel = currentLevel;
	}

	/**
	 * 设置日志级别
	 * @param logger
	 * @param level
	 */
	public static void setLevel(Class<?> clazz, LogLevel level) {
		String logger = clazz.getCanonicalName();
		if (classes.containsKey(logger)) { // 有则设置
			classes.get(logger).setCurrentLevel(level);
			return;
		} else { // 查找前缀
			for (String name : classes.keySet()) {
				if (name.startsWith(logger)) {
					classes.get(name).setCurrentLevel(level);
					return;
				}
			}
		}
	}

	private String NOW() {
		return DateTime.now().toString(LONG_TIME_PATTERN);
	}

	public void trace(String message) {
		log(LogLevel.TRACE, message);
	}

	public void trace(String message, Object... parameters) {
		log(LogLevel.TRACE, message, parameters);
	}

	public void trace(String message, Throwable throwable) {
		log(LogLevel.TRACE, message, throwable);
	}

	public void trace(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.TRACE, message, throwable, parameters);
	}

	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	public void debug(String message, Object... parameters) {
		log(LogLevel.DEBUG, message, parameters);
	}

	public void debug(String message, Throwable throwable) {
		log(LogLevel.DEBUG, message, throwable);
	}

	public void debug(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.DEBUG, message, throwable, parameters);
	}

	public void info(String message) {
		log(LogLevel.INFO, message);
	}

	public void info(String message, Object... parameters) {
		log(LogLevel.INFO, message, parameters);
	}

	public void info(String message, Throwable throwable) {
		log(LogLevel.INFO, message, throwable);
	}

	public void info(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.INFO, message, throwable, parameters);
	}

	public void warn(String message) {
		log(LogLevel.WARN, message);
	}

	public void warn(String message, Object... parameters) {
		log(LogLevel.WARN, message, parameters);
	}

	public void warn(String message, Throwable throwable) {
		log(LogLevel.WARN, message, throwable);
	}

	public void warn(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.WARN, message, throwable, parameters);
	}

	public void error(String message) {
		log(LogLevel.ERROR, message);
	}

	public void error(String message, Object... parameters) {
		log(LogLevel.ERROR, message, parameters);
	}

	public void error(String message, Throwable throwable) {
		log(LogLevel.ERROR, message, throwable);
	}

	public void error(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.ERROR, message, throwable, parameters);
	}

	public void fatal(String message) {
		log(LogLevel.FATAL, message);
	}

	public void fatal(String message, Object... parameters) {
		log(LogLevel.FATAL, message, parameters);
	}

	public void fatal(String message, Throwable throwable) {
		log(LogLevel.FATAL, message, throwable);
	}

	public void fatal(String message, Throwable throwable, Object... parameters) {
		log(LogLevel.FATAL, message, throwable, parameters);
	}

	/**
	 * 打印日志
	 * @param level 日志级别
	 * @param message 日志内容
	 */
	public void log(LogLevel level, String message) {
		int currentLevelNumber = getCurrentLevel().LevelNumber();
		if (level.LevelNumber() < currentLevelNumber)
			return;
		stdout.println(String.format(LOGGER_TEMPLATE, NOW(), level, logger, message));
	}

	/**
	 * 打印日志
	 * @param level 日志级别
	 * @param message 带参数的内容,参考String.format
	 * @param parameters 参数
	 */
	public void log(LogLevel level, String message, Object... parameters) {
		int currentLevelNumber = getCurrentLevel().LevelNumber();
		if (level.LevelNumber() < currentLevelNumber)
			return;
		String msg = String.format(message, parameters);
		stdout.println(String.format(LOGGER_TEMPLATE, NOW(), level, logger, msg));
	}

	/**
	 * 打印日志
	 * @param level 日志级别
	 * @param message 日志内容
	 * @param throwable 异常对象
	 */
	public void log(LogLevel level, String message, Throwable throwable) {
		int currentLevelNumber = getCurrentLevel().LevelNumber();
		if (level.LevelNumber() < currentLevelNumber)
			return;
		errout.println(String.format(LOGGER_TEMPLATE, NOW(), level, logger, message));
		throwable.printStackTrace(errout);
	}

	/**
	 * 打印日志
	 * @param level
	 * @param message 带参数的内容,参考String.format
	 * @param throwable 一场对象
	 * @param parameters 参数
	 */
	public void log(LogLevel level, String message, Throwable throwable, Object... parameters) {
		int currentLevelNumber = getCurrentLevel().LevelNumber();
		if (level.LevelNumber() < currentLevelNumber)
			return;
		String msg = String.format(message, parameters);
		errout.println(String.format(LOGGER_TEMPLATE, NOW(), level, logger, msg));
		throwable.printStackTrace(errout);
	}
}
