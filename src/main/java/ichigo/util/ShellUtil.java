package ichigo.util;

import java.net.*;
import java.io.*;
import java.util.*;

public class ShellUtil {

	private String[] command;
	private String grep;
	private List<String> result;

	public ShellUtil(String[] c) {
		this.command = c;
		this.result = new ArrayList<String>();
	}
	public static BufferedReader getBufferedReader(InputStream in) {
		BufferedInputStream bin = new BufferedInputStream(in);
		InputStreamReader reader = new InputStreamReader(bin);
		BufferedReader bReader = new BufferedReader(reader);
		return bReader;
	}
	public void run() {
		try {
			// System.out.println(this.command);
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(this.command);
			BufferedInputStream bin = new BufferedInputStream(proc.getInputStream());
			InputStreamReader reader = new InputStreamReader(bin);
			proc.waitFor();

			/*
			new Thread(() -> {
					// 処理
			}).start();
			*/

			BufferedReader bReader = new BufferedReader(reader);
			String text;
			while((text = bReader.readLine()) != null) {
				result.add(text);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public int printResult(String grep) {
		int printCount = 0;
		for (String line: result) {
			if (grep != null) {
				if (line.matches(grep)) {
					System.out.println(line);
					printCount++;
				}
			} else {
				System.out.println(line);
				printCount++;
			}
		}
		return printCount;
	}

	public List<String> getResult() {
		return result;
	}
}
