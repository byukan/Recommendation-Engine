package com.tara.common;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandRunner {

	public static String triggerCommand(String command) {

		return executeCommand(command);

	}

	private static String executeCommand(String command) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Process process;
		try {
			process = Runtime.getRuntime().exec(command);

			ExecutorService executorService = Executors.newFixedThreadPool(2);

			executorService.execute(new SyncPipe(process.getInputStream(),
					outputStream));
			executorService.execute(new SyncPipe(process.getErrorStream(),
					System.out));

			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

			process.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		// p.waitFor();
		return new String(outputStream.toByteArray());

	}
}
