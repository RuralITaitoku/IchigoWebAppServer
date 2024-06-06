package ichigo.util;

import java.net.*;
import java.util.*;

public class Command {
	String cmd = null;
	List<String> cmdList;
	Map<String, String> param = null;
	Map<String, String> prop = null;
	List<String> propList = null;
	boolean withProperty = true;
	byte[] data = null;

	public Command() {
	}
	public Command(String c) {
		this.init(c);
	}
	public Command(List<String> lines) {
		this.init(lines);
	}

	public List<String> getCmdList() {
		return this.cmdList;
	}

	public void setWithProperty(boolean b) {
		this.withProperty = b;
	}
	public boolean withProperty() {
		return this.withProperty;
	}
	public void init(String c) {
		List<String> lines = new ArrayList<String>();
		Scanner scanner = new Scanner(c);
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() == 0) {
				break;
			}
			lines.add(line);
		}
		scanner.close();
		this.init(lines);
	}

	public void init(List<String> lines) {
		this.cmdList = new ArrayList<String>();
		if (lines != null) {
			if (lines.size() > 0) {
				this.cmd = lines.get(0);
				Scanner scanner = new Scanner(this.cmd);
				while(scanner.hasNext()) {
					this.cmdList.add(scanner.next());
				}
				scanner.close();
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < lines.size(); i++) {
				String l = lines.get(i);
				sb.append(l);
				int si = l.indexOf(":");
				if (si > 0) {
					String n = l.substring(0, si);
					String v = l.substring(si + 1);
					putProp(n, v);
				}
			}
			this.setData(sb.toString());
		}
	}
	public String getRequestPath() {
		String path = getArgs(1);
		LogUtil.log("パス：" + path);
		int i = path.indexOf('?');
		if (i > 0) {
			return path.substring(0, i);
		}
		return path;
	}
	private void initParam() {
		if (this.param == null) {
			this.param = new HashMap<String, String>();
		}
		String paramData = null;
		if ("GET".equals(getArgs(0))) {
			LogUtil.log("GETの場合");
			String path = getArgs(1);
			LogUtil.log("パス：" + path);
			int i = path.indexOf('?');
			if (i > 0) {
				paramData = path.substring(i + 1);
			}
		} else {
			try {
				paramData = new String(data, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LogUtil.log("パラメータデータ：" + paramData);
		WordUtil word = new WordUtil(paramData);
		while (word.hasNext()) {
			String key = word.next("=");
			String value = word.nextOrEnd(",");
			LogUtil.log("key=" + key);
			LogUtil.log("value=" + value);
			try {
				param.put(key, URLDecoder.decode(value, "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public String getParam(String key) {
		initParam();
		return param.get(key);
	}




	public void setData(String d) {
		try {
			this.data = d.getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setData(byte[] d) {
		this.data = d;
	}
	public byte[] getData() {
		return this.data;
	}
	public int getDataLength() {
		if (this.data == null) {
			return 0;
		}
		return this.data.length;
	}
	public void putProp(String key, String value) {
		if (this.propList == null) {
			this.propList = new ArrayList<String>();
		}
		if (this.prop == null) {
			this.prop = new HashMap<String, String>();
		}
		if (this.prop.put(key, value) == null) {
			this.propList.add(key);
		}
	}
	public String getProp(String key) {
		if (this.prop == null) {
			return null;
		}
		return this.prop.get(key);
	}
	public int getInt(String key, int d) {
		String p = getProp(key);
		try {
			return Integer.parseInt(p);
		}	catch (Exception e) {
			return d;
		}
	}

	public String getArgs(int i) {
		if (0 <= i && i < cmdList.size()) {
			return cmdList.get(i);
		}
		return null;
	}
	public String toString() {
		return getCommandString();
	}
	public String getCmd() {
		return this.cmd;
	}
	public byte[] getCommand() {
		try {
			String c = getCommandString();
			return c.getBytes("UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String getCommandString() {
		StringBuilder sb = new StringBuilder();
		// sb.append(this.cmd);
		// sb.append("\r\n");
		for (int i = 0; i < cmdList.size(); i++) {
			if (i == 0) {
				sb.append(cmdList.get(i));
			} else {
				sb.append(" ");
				sb.append(cmdList.get(i));
			}
		}
		sb.append("\r\n");
		if (propList != null) {
			for (String pn : propList) {
				String v = prop.get(pn);
				if (v != null){
					sb.append(pn);
					sb.append(": ");
					sb.append(v);
					sb.append("\r\n");
				}
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}
}
