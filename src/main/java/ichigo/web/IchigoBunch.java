package ichigo.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ichigo.util.LogUtil;
import ichigo.util.WordUtil;

public class IchigoBunch implements Iterable<IchigoBunch> {
	public static String dbName = "test.db";
	Map<String, IchigoBunch> map = null;
	List<IchigoBunch> list = null;


	String value = null;
	enum Type {
		VAL,
		STR,
		INT,
		TEMP
	}
	Type type;


	public IchigoBunch() {
	}
	public IchigoBunch(String v) {
		this.type = Type.STR;
		this.value = v;
	}
	public IchigoBunch(String v, Type t) {
		this.type = t;
		this.value = v;
	}

	public void setType(Type t) {
		this.type = t;
	}
	public boolean isInt() {
		if (this.type == Type.INT) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isStr() {
		if (this.type == Type.STR) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isValue() {
		if (this.value != null) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isMap() {
		if (this.map != null) {
			return true;
		}
		return false;
	}
	public boolean isList() {
		if (this.list != null) {
			return true;
		}
		return false;
	}
	

	public static void setDbFilename(String name) {
		IchigoBunch.dbName = name;
	}

	public Connection getConnection() throws SQLException {
        Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			LogUtil.log(e);
		}
		// データベースのPATHを指定。相対パスでも絶対パスでも行けるようです
		connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
		return connection;
	}

	public void execSql(String sql) throws SQLException {
		LogUtil.log("SQL:" + sql);

		Connection conn  = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	public void selectSql(String sql) throws SQLException {
		LogUtil.log("SQL:" + sql);
		Connection conn  = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData metaData = rs.getMetaData();
		this.list = new ArrayList<IchigoBunch>();
		while(rs.next()) {
			IchigoBunch row = new IchigoBunch();
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i + 1);
				// this.add(columnName);
				row.put(columnName, rs.getString(i + 1));
				LogUtil.log(columnName + "  " + rs.getString(i + 1));
			}
			this.list.add(row);
		}

	}

	public String escapeSQL(String in) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			char chr = in.charAt(i);
			if (chr == '\'') {
				buf.append("\'\'");
			} else {
				buf.append(chr);
			}
		}
		return buf.toString();
	}


	public void createTable() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists " + this.value);
		sb.append("(");
		String term = "";
		for (IchigoBunch ib: list) {
			sb.append(term);
			String colName = ib.toString();
			sb.append(colName);
			String cons = this.get(colName);
			if (cons != null) {
				sb.append(" ");
				sb.append(cons);
			}
			term = ", ";
		}
		sb.append(")");
		LogUtil.log("SQL:" + sb);
		this.execSql(sb.toString());
    }
	public void insert() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into " + this.value + "(");
		StringBuilder sv = new StringBuilder();
		if (map != null) {
			//sb.append("{");
			map.forEach((key, value) -> {
				if (sv.length() > 0) {
					sv.append(",");
					sb.append(",");
				}
				sb.append(key);
				sv.append("\'" + escapeSQL(value.toString()) + "\'");
			}
			);
			sb.append(") values (");
			sb.append(sv.toString());
			sb.append(")");
		}
		LogUtil.log("SQL:" +sb);
		this.execSql(sb.toString());
	}
	public void updateWithKey(String key) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("update " + this.value + " set ");
		String term = "";
		if (map != null) {
			//sb.append("{");
			map.forEach((k, value) -> {
				if (key.equals(k) == false) {
					sb.append(term);
					sb.append(k);
					sb.append("=");
					sb.append("\'");
					sb.append(escapeSQL(value.toString()));
					sb.append("\'");
				}
			});
		}
		sb.append(" where ");
		sb.append(key);
		sb.append(" = '");
		sb.append(this.escapeSQL(get(key)));
		sb.append("'");
		execSql(sb.toString());
	}
	public void deleteWithKey(String key) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from " + this.value);
		sb.append(" where ");
		sb.append(key);
		sb.append(" = '");
		sb.append(this.escapeSQL(get(key)));
		sb.append("'");
		execSql(sb.toString());
	}
	public void clear() {
		this.list = null;
		this.map = null;
	}

	public List<IchigoBunch> getList() {
		return list;
	}
	public void add(String v) {
		IchigoBunch ib = new IchigoBunch(v);
		if (this.list == null) {
			this.list = new ArrayList<IchigoBunch>();
		}
		this.list.add(ib);
	}
	public Type getType() {
		return this.type;
	}
	public String get(int index) {
		if (this.list == null) {
			return null;
		}
		if (index < 0) {
			return null;
		}
		if (index >= list.size()) {
			return null;
		}
		IchigoBunch ib = list.get(index);
		return ib.toString();
	}
	public void put(String key, String v) {
		IchigoBunch ib = new IchigoBunch(v);
		if (map == null) {
			this.map = new HashMap<String, IchigoBunch>();
		}
		map.put(key, ib);
	}
	public String get(String key) {
		if (map != null) {
			IchigoBunch ib = map.get(key);
			if (ib == null) {
				return null;
			}
			return ib.toString();
		}
		return null;
	}
	public String remove(String key) {
		if (map != null) {
			IchigoBunch ib = map.remove(key);
			if (ib != null) {
				return ib.toString();
			}
		}
		return null;
	}



	public String toString() {
		if (value != null) {
			return value;
		}
		if (map != null) {
			return toMapString();
		}
		if (list != null) {
			return toArrayString();
		}
		return null;
	}

	public String toArrayString() {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			sb.append("\r\n[");
			String term = "";
			for (IchigoBunch stem: list) {
				sb.append(term);
				term = "\r\n  ,";
				if (stem.isValue()) {
					sb.append("\"");
					sb.append(stem.toString());
					sb.append("\"");
				} else {
					sb.append(stem.toString());
				}
			}
			sb.append("]");
			return sb.toString();
		}
		return null;
	}
	public String toMapString() {
		StringBuilder sb = new StringBuilder();
		if (map != null) {
			//sb.append("{");
			map.forEach((key, value) -> {
				if(sb.length() == 0) {
					sb.append("\r\n{");
				} else {
					sb.append("\r\n  ,");
				}
				sb.append("\"");
				sb.append(key);
				sb.append("\":");
				sb.append("\"");
				sb.append(value);
				sb.append("\"");
			});
			sb.append("}");
			return sb.toString();
		}
		return null;
	}
	@Override
	public Iterator<IchigoBunch> iterator() {
		if (this.list != null) {
			return this.list.iterator();
		}
		return null;
	}


    public void toTemplate() {
		if (this.type == Type.TEMP) {
			return;
		}
		this.type = Type.TEMP;
        WordUtil wu = new WordUtil(this.value);
		this.list = new ArrayList<IchigoBunch>();
        while(wu.hasNext()) {
            String str = wu.nextOrEnd("{{");
			if (str == null || str.length() == 0) {
				return;
			}
			IchigoBunch ib = new IchigoBunch(str);
			this.list.add(ib);
			String val = wu.nextOrEnd("}}");
			if (val == null || val.length() == 0) {
				return;
			}
			val = val.trim();
			IchigoBunch ibv = new IchigoBunch(val, Type.VAL);
			this.list.add(ibv);
        }
	}
	public String replaceTemp() {
		toTemplate();
		StringBuilder sb = new StringBuilder();
		for (IchigoBunch ib: this.list) {
			if (ib.getType() == Type.VAL) {
				String r = this.get(ib.toString());
				LogUtil.log("置き換え前a" + ib.toString() + "z");
				LogUtil.log("置き換え後" + r);
				sb.append(r);
			} else {
				sb.append(ib.toString());
				LogUtil.log(ib.toString());
			}
		}
		return sb.toString();
    }

}
