package cn.colorforme.sina.capture;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class LoadInsertFile {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.1.9:3306/autocolor";

	// Database credentials
	static final String USER = "mjzc";
	static final String PASS = "mjzc";

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		File newFile = new File("d:/t_color_photo2.sql");
		List<String> newLst = FileUtils.readLines(newFile, "utf-8");
		Statement stm = conn.createStatement();
		for (String line : newLst) {
			stm.executeUpdate(line);
		}

	}
}
