package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBImport {
	public static void main(String[] args) {
		try {
			// Ensure the driver is imported.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			try {
				System.out.println("Connecting to \n" + DBUtil.URL);
				conn = DriverManager.getConnection(DBUtil.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			// Step 1 Drop tables in case they exist.
			Statement stmt = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS EnrollRecord";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS Student";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS Course";
			stmt.executeUpdate(sql);			

			sql = "CREATE TABLE Student "
					+ "(student_id VARCHAR(255) NOT NULL, "
					+ "first_name VARCHAR(255) NOT NULL, " 
					+ "last_name VARCHAR(255) NOT NULL, "
					+ "date_of_birth DATE, "	   				 
					+ "image_url VARCHAR(255),"
					+ " PRIMARY KEY ( student_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE Course "
					+ "(course_id VARCHAR(255) NOT NULL, "
					+ " course_name VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY ( course_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE EnrollRecord "
					+ "(enroll_record_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ " student_id VARCHAR(255) NOT NULL , "
					+ " course_id VARCHAR(255) NOT NULL, "
					+ " enroll_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ " PRIMARY KEY (enroll_record_id),"
					+ "FOREIGN KEY (student_id) REFERENCES Student(student_id),"
					+ "FOREIGN KEY (course_id) REFERENCES Course(course_id))";
			stmt.executeUpdate(sql);

			// Add courses in course table
			sql = "INSERT INTO course (course_id, course_name) VALUES" 
					+ " (\"31787\", \"Web Design\"),"
					+ " (\"32464\", \"Data Structure\"),"
					+ " (\"33457\", \"Algorithms on Graphs\"),"
					+ " (\"35234\", \"Algorithms on Strings\"),"
					+ "(\"31124\", \"Using Databases with Python\")";
			stmt.executeUpdate(sql);
			System.out.println("DBYelpImport: import is done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
