package ichigo.util;

import java.awt.Component;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

public class IOUtil {


    public static String load(String path, String encoding)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileInputStream input = new FileInputStream(path);
        InputStreamReader reader = new InputStreamReader(input, encoding);
        StringBuffer buf = new StringBuffer();
        try {
            int c;
            while ((c = reader.read()) >= 0) {
                buf.append((char) c);
            }
        } finally {
            reader.close();
        }
        return buf.toString();
    }

    public static byte[] loadBytes(String path) throws Exception {
        FileInputStream input = new FileInputStream(path);
        return readBytes(input);
    }
    public static byte[] loadBytes(File file) throws Exception {
        FileInputStream input = new FileInputStream(file);
        return readBytes(input);
    }

    public static String load(String path, String encoding, int maxLine, int maxSize)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileInputStream input = new FileInputStream(path);
        InputStreamReader reader = new InputStreamReader(input, encoding);
        StringBuffer buf = new StringBuffer();
        try {
            int line = 0;
            for (int i = 0; i < maxSize; i++) {
                int c = reader.read();
                if (c < 0) {
                    break;
                }
                buf.append((char) c);
                if ((char) c == '\n') {
                    line++;
                }
                if (line > maxLine) {
                    break;
                }
            }
        } finally {
            reader.close();
        }
        return buf.toString();
    }

    public static String loadURL(String path, String encoding)
            throws UnsupportedEncodingException, IOException {
        return loadURL(new URL(path), encoding);
    }

    public static String loadURL(URL url, String encoding)
            throws UnsupportedEncodingException, IOException {
        InputStream input = url.openStream();
        InputStreamReader reader = new InputStreamReader(input, encoding);
        StringBuffer buf = new StringBuffer();
        try {
            int c;
            while ((c = reader.read()) >= 0) {
                buf.append((char) c);
            }
        } finally {
            reader.close();
        }
        return buf.toString();
    }

    public static void save(String path, String encoding, String data)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        if ("MS932".equals(encoding)) {
            StringBuffer body = new StringBuffer();
            for (int i = 0; i < data.length(); i++) {
                char ch = data.charAt(i);
                if (ch == '\n') {
                    body.append("\r\n");
                } else if (ch == '\r') {
                } else if (ch == 0x301C) {
                    body.append((char) 0xFF5E);
                } else if (ch == 0x2016) {
                    body.append((char) 0x2225);
                } else if (ch == 0x2212) {
                    body.append((char) 0xFF0D);
                } else if (ch == 0x00A2) {
                    body.append((char) 0xFF0D);
                } else if (ch == 0x00A3) {
                    body.append((char) 0xFFE1);
                } else if (ch == 0x00AC) {
                    body.append((char) 0xFFE2);
                } else if (ch == 0xFF06) {
                    body.append((char) 0xFF06);
                } else {
                    body.append(ch);
                }
            }
            data = body.toString();
        } else {
            StringBuffer body = new StringBuffer();
            for (int i = 0; i < data.length(); i++) {
                char ch = data.charAt(i);
                if (ch == '\r') {
                } else {
                    body.append(ch);
                }
            }
            data = body.toString();
        }
        FileOutputStream output = new FileOutputStream(path);
        output.write(data.getBytes(encoding));
        output.close();
    }

    public static void save(String path, byte[] data)
            throws FileNotFoundException, IOException {
        FileOutputStream output = new FileOutputStream(path);
        output.write(data);
        output.close();
    }

    public static void copy(String inFile, String outFile, String encoding)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        save(outFile, encoding, load(inFile, encoding));
    }

    public static void copy(String inFile, String outFile)
            throws FileNotFoundException, IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inFile));
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            for (;;) {
                int b = in.read();
                if (b < 0) {
                    break;
                }
                out.write(b);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
				out.flush();
                out.close();
            }
        }
    }

    public static boolean delete(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static String[] getFileList(String path) {
        return (new File(path)).list();
    }

    public static byte[] readBytes(InputStream in, int length) throws IOException {
        byte buf[] = new byte[length];
        if (length == 0) {
            return buf;
        }
        int off = 0;
        int len = length;
        for (;;) {
            int stat = in.read(buf, off, len);
            if (stat == -1) {
                return null;
            }
            if (off + stat < length) {
                off += stat;
                len -= stat;
                continue;
            }
            break;
        }
        return buf;
    }

    public static byte[] readBytes(InputStream in) throws Exception {
        BufferedInputStream bIn = new BufferedInputStream(in);
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            int r;
            while ((r = bIn.read()) >= 0) {
                bOut.write((byte) r);
            }
            return bOut.toByteArray();
        } finally {
            in.close();
        }
    }
    public static byte[] readBytesLine(InputStream in) throws Exception {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		int r;
		while ((r = in.read()) >= 0) {
			System.out.println("" + r);
			bOut.write((byte) r);
			if((byte)r == (byte)'\n'){
				return bOut.toByteArray();
			}
		}
		return bOut.toByteArray();
    }
    public static String readString(InputStream in) {
      try {
          byte buf[] = readBytes(in);
          if (buf == null) {
              return null;
          }
          return new String(buf, "UTF-8");
        } catch(Exception e) {
          e.printStackTrace();
          return null;
        }
    }
    public static String readString(InputStream in, int length, String encoding) throws Exception {
        byte buf[] = readBytes(in, length);
        if (buf == null) {
            return null;
        }
        return new String(buf, encoding);
    }
    public static String readLine(InputStream in, String encoding) throws Exception {
        byte buf[] = readBytesLine(in);
        if (buf == null) {
            return null;
        }
        return new String(buf, encoding);
    }
    public static String readNextEmptyLine(InputStream in, String encoding) throws Exception {
		StringBuilder sb = new StringBuilder();
		for(;;){
			String line = readLine(in, encoding);
			System.out.println("===" + line);
			if(line == null){
				break;
			}
			if(line.trim().length() == 0){
				sb.append(line);
				break;
			}
			sb.append(line);
		}
		return sb.toString();
    }

    public static List<File> findFile(String path, String pattern) {
        Pattern p = null;
        if (pattern != null) {
            p = Pattern.compile(pattern);
        }
        List<File> result = new ArrayList<File>();
        findFileBody(new File(path), p, result);
        return result;
    }

    private static void findFileBody(File file, Pattern pattern, List<File> result) {
        if (file.isFile()) {
            if (pattern == null) {
                result.add(file);
            } else {
                Matcher m = pattern.matcher(file.getName());
                if (m.matches()) {
                    result.add(file);
                }
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                findFileBody(files[i], pattern, result);
            }
        }
    }

    public static String getBaseName(String path) {
        if (path == null) {
            return null;
        }
        int index = path.lastIndexOf(".");
        if (index > 0) {
            return path.substring(0, index);
        }
        return path;
    }

    public static String getExtension(String path) {
        if (path == null) {
            return null;
        }
        int index = path.lastIndexOf(".");
        String ext = "";
        if (index > 0) {
            ext = path.substring(index);
        }
        return ext.toLowerCase();
    }

    public static String getLine(InputStream in) throws IOException {
        return getLine(in, "UTF-8");
    }

    public static String getLine(InputStream in, String encoding) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int r;
        for (;;) {
            if ((r = in.read()) < 0) {
                String result = buffer.toString(encoding);
                if (result.length() == 0) {
                    return null;
                } else {
                    return result;
                }
            }
            if ((char) r == '\r') {
                buffer.write(r);
                continue;
            }
            if ((char) r == '\n') {
                buffer.write(r);
                break;
            }
            buffer.write(r);
        }
        return buffer.toString(encoding);
    }

    public static String get(InputStream in, char[] t, boolean f) throws IOException {
        return get(in, t, f, "UTF-8");
    }

    public static String get(InputStream in, char[] t, boolean f, String encoding) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int r;
        for (;;) {
            if ((r = in.read()) < 0) {
                String result = buffer.toString(encoding);
                if (result.length() == 0) {
                    return null;
                } else {
                    return result;
                }
            }
            for (char end : t) {
                if ((char) r == end) {
                    if (f) {
                        buffer.write(r);
                    }
                    return buffer.toString(encoding);
                }
            }
            buffer.write(r);
        }
    }

    public static byte[] readLineBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int r;
        for (;;) {
            if ((r = in.read()) < 0) {
                byte[] result = buffer.toByteArray();
                if (result.length == 0) {
                    return null;
                } else {
                    return result;
                }
            }
            if ((char) r == '\r') {
                buffer.write(r);
                continue;
            }
            if ((char) r == '\n') {
                buffer.write(r);
                break;
            }
            buffer.write(r);
        }
        return buffer.toByteArray();
    }

    public static int readLine(InputStream in, byte[] result) throws IOException {
        int r;
        int i = 0;
        for (; i < result.length; i++) {
            if ((r = in.read()) < 0) {
                if (i == 0) {
                    return -1;
                }
                return i;
            }
            if (r == (int) '\r') {
                result[i] = (byte) r;
                continue;
            }
            if (r == (int) '\n') {
                result[i] = (byte) r;
                i++;
                break;
            }
            result[i] = (byte) r;
        }
        return i;
    }

    public static String getNextEmptyLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int count = 0;
        int r;
        for (;;) {
            if ((r = in.read()) < 0) {
                return null;
            }
            if ((char) r == '\r') {
                buffer.write(r);
                continue;
            }
            if ((char) r == '\n') {
                if (count == 0) {
                    buffer.write(r);
                    break;
                }
                count = 0;
            } else {
                count++;
            }
            buffer.write(r);
        }
        return buffer.toString("UTF-8");
    }

    public static String getString(InputStream in, int length) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int r;
        for (int i = 0; i < length; i++) {
            if ((r = in.read()) < 0) {
                return null;
            }
            buffer.write(r);
        }
        return buffer.toString("UTF-8");
    }

	public static String getString(InputStream in, String encoding) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int r;
        while ((r = in.read()) >= 0) {
            buffer.write(r);
        }
        return buffer.toString(encoding);
    }

    public static class Filter extends javax.swing.filechooser.FileFilter {

        private List<String> extList = new ArrayList<String>();
        private String description;

        public Filter(String... ext) {
            this.description = ext[0];
            for (int i = 1; i < ext.length; i++) {
                extList.add(ext[i]);
            }
        }

        public boolean accept(File file) {
            if (file != null) {
                if (file.isDirectory()) {
                    return true;
                }
                String ext = IOUtil.getExtension(file.getName());
                if (ext == null) {
                } else {
                    for (String e: extList) {
                        if (e.equalsIgnoreCase(ext)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public String getDescription() {
            return description;
        }
    }

    private static JFileChooser createFileChooser(String path, String... ext) {
        JFileChooser file = new JFileChooser();
        if (path != null) {
            file.setSelectedFile(new File(path));
        }
        if (ext != null && ext.length > 0) {
            Filter filter = new Filter(ext);
            file.addChoosableFileFilter(filter);
            file.setFileFilter(filter);
        }
        return file;
    }

    public static File getOpenFile(Component component, String path, String... ext) {
        JFileChooser file = createFileChooser(path, ext);
        file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		file.setMultiSelectionEnabled(false);
        if (file.showOpenDialog(component) == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return file.getSelectedFile();
    }

	public static File[] getOpenFiles(Component component, String path, String... ext) {
        JFileChooser file = createFileChooser(path, ext);
        file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		file.setMultiSelectionEnabled(true);
        if (file.showOpenDialog(component) == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return file.getSelectedFiles();
    }

    public static File getSaveFile(Component component, String path, String... ext) {
        JFileChooser file = createFileChooser(path, ext);
        file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		file.setMultiSelectionEnabled(false);
        if (file.showSaveDialog(component) == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return file.getSelectedFile();
    }

    public static File getSaveDir(Component component, String path) {
        JFileChooser file = new JFileChooser();
        if (path != null) {
            file.setSelectedFile(new File(path));
		}
        file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		file.setMultiSelectionEnabled(true);
        if (file.showSaveDialog(component) == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return file.getSelectedFile();
    }
}
