package org.fincl.miss.server.logging.logger;

import java.util.Stack;

public class LogRepository {

	private static ThreadLocal<Stack<Log>> logRepo;

	static {
		logRepo = new ThreadLocal<Stack<Log>>();
	}

	public static void pushLog(Log log) {

		Stack<Log> logs = logRepo.get();
		if (logs == null) {
			logs = new Stack<Log>();
			logRepo.set(logs);
		}
		logs.push(log);
	}

	public static Log popLog() {

		Stack<Log> logs = logRepo.get();
		try {
			if (logs == null || logs.empty()) {
				return null;
			} else {
				return logs.pop();
			}
		} finally {
			if (logs.empty()) {
				logRepo.remove();
			}
		}
	}

	public static Log getLog() {

		Stack<Log> logs = logRepo.get();
		if (logs == null || logs.empty()) {
			return null;
		} else {
			return logs.peek();
		}
	}
}
