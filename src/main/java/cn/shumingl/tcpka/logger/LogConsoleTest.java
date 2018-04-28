package cn.shumingl.tcpka.logger;

public class LogConsoleTest {
	public static void main(String[] args) {
		LogConsole logger = LogConsole.getLogger(LogConsoleTest.class);
		LogConsole.setLevel(LogConsoleTest.class, LogLevel.ERROR);
		logger.trace("message");
		logger.debug("message");
		logger.info("message");
		logger.error("message");
		logger.fatal("message");
		System.out.println("-------------------------------------------");
		LogConsole.setLevel(LogConsoleTest.class, LogLevel.INFO);
		logger.trace("message");
		logger.debug("message");
		logger.info("message");
		logger.error("message");
		logger.fatal("message");
	}
}
