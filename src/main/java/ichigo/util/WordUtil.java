package ichigo.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtil {

	public static final int DEFAULT    = 0;
	public static final int OPERATOR   = 1;
	public static final int CONTROL    = 2;
	public static final int RETURN     = 3;
	public static final int SPACE      = 4;
	public static final int QUOTATION  = 5;
	public static final int AFTER_0X80 = 6;
	public static final int END        = 7;

	private int preIndex;
	private int index;
	private int type;
	private String data;

	public WordUtil() {
	}

	public WordUtil(String data) {
		setData(data);
	}

	public void setData(String data) {
		this.data = data;
		this.index = 0;
		this.type  = -1;
		this.preIndex = 0;
	}
	public String getData() {
		return data;
	}

	public int getStartIndex() {
		return preIndex;
	}

	public void setIndex(int i) {
		index = i;
	}

	public int getIndex() {
		return index;
	}

	public int getLineNo(int c) {
		int result = 0;
		for (int i = 0; i < c; i++) {
			if (data.charAt(i) == '\n') {
				result++;
			}
		}
		return result;
	}

	public int getLineStartIndex() {
		return getLineStartIndex(index);
	}
	public int getLineStartIndex(int c) {
		for (int i = c - 1; i >=0; i--) {
			if (data.charAt(i) == '\n') {
				return i + 1;
			}
		}
		return 0;
	}
	public int getLineEndIndex() {
		return getLineEndIndex(index);
	}
	public int getLineEndIndex(int c) {
		for (int i = c; i < data.length(); i++) {
			if (data.charAt(i) == '\n') {
				return i + 1;
			}
		}
		return 0;
	}

	public boolean hasNext() {
		if (data == null) {
			return false;
		}
		return index < data.length();
	}

	public String next() {
		if (index >= data.length()) {
			type = END;
			return "";
		}
		preIndex = index;
		char ch = data.charAt(index);
		if (isControl(ch)) {
			index++;
			type = CONTROL;
			return String.valueOf(ch);
		}
		StringBuffer buf = new StringBuffer();
		if (isQuotation(ch)) {
			buf.append(ch);
			for (index++; index < data.length(); index++) {
				char c = data.charAt(index);
				if ('\\' == c && index < data.length() - 1) {
					buf.append(c);
					index++;
					buf.append(data.charAt(index));
					continue;
				}
				buf.append(c);
				if (c == ch) {
					index++;
					break;
				}
			}
			type = QUOTATION;
			return buf.toString();
		}
		type = getType(ch);
		buf.append(ch);
		for (index++; index < data.length(); index++) {
			if (ch == '\n') {
				break;
			}
			ch = data.charAt(index);
			if (type != getType(ch)) {
				break;
			}
			buf.append(ch);
		}
		return buf.toString();
	}

	public int nextInt(String end) {
		return Integer.parseInt(next(end));
	}

	public String next(String end) {
		int dstIndex = data.indexOf(end, index);
		if (dstIndex < 0) {
			return null;
		}
		String result = data.substring(index, dstIndex);
		index = dstIndex + end.length();
		return result;
	}
	public String nextRegex(String r){
		Pattern p = Pattern.compile(r, Pattern.DOTALL);
		Matcher m =p.matcher(data);
		if(m.find(index)){
			String result= this.data.substring(m.start(), m.end());
			this.index = m.end() + 1;
			return result;
		}
		return null;
	}

	public String next(int len) {
		String result = data.substring(index, index + len);
		index = index + len;
		return result;
	}

	public String nextOrEnd(String end) {
		int dstIndex = data.indexOf(end, index);
		if (dstIndex < 0) {
			String result = data.substring(index);
			index = index + result.length();
			return result;
		}
		String result = data.substring(index, dstIndex);
		index = dstIndex + end.length();
		return result;
	}

	public String end() {
		String result = data.substring(index);
		index = data.length();
		return result;
	}

	public char charAt(int i) {
		return data.charAt(index + i);
	}

	public String nextLine() {
		if (data.length() <= index) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for (;index < data.length(); index++) {
			char ch = data.charAt(index);
			if ('\r' == ch) {
				continue;
			}
			if ('\n' == ch) {
				index++;
				break;
			}
			buf.append(ch);
		}
		return buf.toString();
	}


	public String nextSpace() {
		StringBuffer result = new StringBuffer();
		while (hasNext()) {
			String word = next();
			if (getType() == SPACE || getType() == RETURN) {
				break;
			}
			result.append(word);
		}
		return result.toString();
	}

	public String nextChar(String charList) {
		StringBuffer result = new StringBuffer();
		for (; index < data.length(); index++) {
			char c = data.charAt(index);
			if (charList.indexOf(c) >= 0) {
				break;
			}
			result.append(c);
		}
		return result.toString();
	}

	public int getType() {
		if (hasNext()) {
			return this.type;
		} else {
			return END;
		}
	}

	public static boolean isOperator(char c) {
		return (   (c == '<')
				|| (c == '>')
				|| (c == ':')
				|| (c == '&')
				|| (c == '|')
				|| (c == '!')
				|| (c == '*')
				|| (c == '/')
				|| (c == '%')
				|| (c == '+')
				|| (c == '-')
				|| (c == '=')
				|| (c == '#')
				|| (c == '@'));
	}

	public static boolean isControl(char c) {
		return (   (c == ';')
				|| (c == '.')
				|| (c == ',')
				|| (c == '[')
				|| (c == ']')
				|| (c == '{')
				|| (c == '}')
				|| (c == '(')
				|| (c == ')'));
	}

	public static boolean isReturn(char c) {
		return ((c == '\n') || (c == '\r'));
	}

	public boolean isSpace() {
		return getType() == SPACE;
	}

	public static boolean isSpace(char c) {
		return ((c == ' ') || (c == '\t'));
	}

	public static boolean isQuotation(char c) {
		return ((c == '"') || (c == '\''));
	}

	public static boolean isOthor(char c) {
		return !(isOperator(c) || isControl(c) || isReturn(c) || isSpace(c) || isQuotation(c));
	}

	private int getType(char c) {
		if (isOperator(c)) {
			return OPERATOR;
		}
		if (isControl(c)) {
			return CONTROL;
		}
		if (isReturn(c)) {
			return RETURN;
		}
		if (isSpace(c)) {
			return SPACE;
		}
		if (isQuotation(c)) {
			return QUOTATION;
		}
		if (c >= (char)0x80) {
			return AFTER_0X80;
		}
		return DEFAULT;
	}

	public static final String escape(Object str) {
		if (str == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		escape(buf, (String)str);
		return buf.toString();
	}

	public static final StringBuffer escape(StringBuffer buf, String in) {
		for (int i = 0; i < in.length(); i++) {
			char chr = in.charAt(i);
			if (chr == '\n') {
				buf.append("\\n");
			} else if (chr == '\r') {
				buf.append("\\r");
			} else if (chr == '\t') {
				buf.append("\\t");
			} else if (chr == '\'') {
				buf.append("\\\'");
			} else if (chr == '\"') {
				buf.append("\\\"");
			} else if (chr == '\\') {
				buf.append("\\\\");
			} else {
				buf.append(chr);
			}
		}
		return buf;
	}

	public static String unescape(String in) {
		if (in == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < in.length() - 1; i++) {
			char c = in.charAt(i);
			if ('\\' == c) {
				i++;
				c = in.charAt(i);
				if (c == 'n') {
					buf.append('\n');
				} else if (c == 'r') {
					buf.append('\r');
				} else if (c == 't') {
					buf.append('\t');
				} else {
					buf.append(c);
				}
				continue;
			}
			buf.append(c);
		}
		return buf.toString();
	}

	public List<String> nextLineWords() {
		List<String> result = new ArrayList<String>();

		while(hasNext()) {
			String w = nextSpace();
			result.add(w);
			if (getType() == END || getType() == RETURN) {
				return result;
			}
		}
		return null;
	}

}
