package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;



import model.Student;


public class MySQLDBConnection implements DBConnection {
	
	private Connection conn = null;
	public MySQLDBConnection() {
		this(DBUtil.URL);
	}
	
	public MySQLDBConnection(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url);
		} catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}

	@Override
	public void addCourse(String studentId, String courseId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCourse(String studentId, String courseId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getSelectedCourses(String studentId) {
		Set<String> courseIds = new HashSet<>();
		try {
			String sql2 = "SELECT course_id FROM enrollrecord WHERE student_id = ?";
			PreparedStatement statement2 = conn.prepareStatement(sql2);
			statement2.setString(1, studentId);
			ResultSet rs2 = statement2.executeQuery();			
			while (rs2.next()) {
				courseIds.add(rs2.getString("course_id"));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return courseIds;
	}
	
	@Override
	public Map<String, String> courseIdToNameMap(Set<String> courseIds) {
		Map<String, String> courses = new HashMap<>();
		try {
			for (String courseId : courseIds) {
				String sql = "SELECT course_name FROM course WHERE course_id = ?";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, courseId);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					courses.put(courseId, rs.getString("course_name"));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return courses;
	}

	@Override
	public JSONObject getStudentById(String studentId) {		
		try { 
			Set<String> courseIds = getSelectedCourses(studentId);
			Map<String, String> courseMap = courseIdToNameMap(courseIds);
			String sql1 = "SELECT * FROM Student WHERE student_id = ?";			
			PreparedStatement statement1 = conn.prepareStatement(sql1);			 
			statement1.setString(1, studentId);			
			ResultSet rs1 = statement1.executeQuery();			
			if (rs1.next()) {
				Student student = new Student.StudentBuilder(
						rs1.getString("first_name"), 
						rs1.getString("last_name"), 
						rs1.getString("student_id"))
						.dob(rs1.getDate("date_of_birth"))
						.imgUrl(rs1.getString("image_url"))
						.courseIdSet(courseIds)
						.courseList(courseMap)
						.build();
				JSONObject obj = student.toJSONObject();
				return obj;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public JSONArray searchStudent(String[] names) {
		List<JSONObject> studentList = new ArrayList<>();
		Set<String> studentIds = getStudentId(names);
		for (String id : studentIds) {
			JSONObject student = getStudentById(id);
			studentList.add(student);
		}
		return new JSONArray(studentList);
	}
	
	
	
	@Override
	public Set<String> getStudentId(String[] names) {
		Set<String> set = new HashSet<>();
		try {
			// TO BE relaxed.
			String sql = "SELECT student_id FROM student WHERE first_name LIKE ? AND last_name LIKE ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, "%" + names[0] + "%");
			statement.setString(2, "%" + names[1] + "%");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String studentId = rs.getString("student_id");
				set.add(studentId);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	@Override
	public String getFirstLastName(String studentId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<String> stringArrayToSet(String[] courseIds) {
		Set<String> set = new HashSet<>();
		for (String id : courseIds) {
			set.add(id);
		}
		return set;
	}

	@Override
	public void updateStudent(String firstName, String lastName, String studentId, String birthDate, String imgUrl, String[] courseIds) {
		try{
			JSONObject originalStudent = getStudentById(studentId);
			String[] originalCourseIds = Student.jsonArrayToStringArray(originalStudent.getJSONArray("courseIds"));
			Set<String> orignalCourseIdSet = stringArrayToSet(originalCourseIds);			
			Set<String> courseIdSet = stringArrayToSet(courseIds);						
			String sql = null;
			
			// Add/Update birthday
			if (birthDate != null) {
				if (! originalStudent.has("dob") || ! birthDate.equals(originalStudent.get("dob"))) {
					sql = "UPDATE student SET date_of_birth = ? WHERE student_id = ?";
					System.out.println("\nDBYelpImport executing query:\n" + sql);
					PreparedStatement statement = conn.prepareStatement(sql);
					java.sql.Date sqlDate = stringToDate(birthDate);
					statement.setDate(1, sqlDate);
					statement.setString(2, studentId);
					statement.executeUpdate();
				}
			}
			
			// Add / update image url.
			if (imgUrl != null) {
				if (! originalStudent.has("imgUrl") || ! imgUrl.equals(originalStudent.getString("imgUrl"))) {
					sql = "UPDATE student SET image_url = ? WHERE student_id = ?";
					System.out.println("\nDBYelpImport executing query:\n" + sql);
					PreparedStatement statement = conn.prepareStatement(sql);			
					statement.setString(1, imgUrl);
					statement.setString(2, studentId);
					statement.executeUpdate();
				}
			}
			// Insert newly added courses.
			for (String courseId : courseIdSet) {
				if (! orignalCourseIdSet.contains(courseId)) {
					sql = "INSERT INTO enrollrecord (student_id, course_id) VALUES"
							+ " (?, ?)";
					System.out.println("\nDBYelpImport executing query:\n" + sql);
					PreparedStatement statement = conn.prepareStatement(sql);			
					statement.setString(1, studentId);
					statement.setString(2, courseId);
					statement.executeUpdate();
				}
			}
			// Delete removed courses.
			for (String courseId : orignalCourseIdSet) {
				if (! courseIdSet.contains(courseId)) {					
					sql = "DELETE FROM enrollrecord WHERE student_id = ? AND course_id = ?";
					System.out.println("\nDBYelpImport executing query:\n" + sql);
					PreparedStatement statement = conn.prepareStatement(sql);			
					statement.setString(1, studentId);
					statement.setString(2, courseId);
					statement.executeUpdate();
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override 
	public java.sql.Date stringToDate(String birthDate) {
		java.sql.Date sqlDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = sdf.parse(birthDate);
			sqlDate = new java.sql.Date(date.getTime());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sqlDate;
	}
	
	@Override
	public void createStudent(String firstName, String lastName, String studentId, String birthDate, String imgUrl, String[] courseIds) {
		try {
			java.sql.Date sqlDate = null;
			if (birthDate != null){
				sqlDate = stringToDate(birthDate);
			}
			String sql = "INSERT IGNORE INTO student VALUES"
					+ " (?, ?, ?, ?, ?)";			 
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, studentId);
			statement.setString(2, firstName);
			statement.setString(3, lastName);
			statement.setDate(4, sqlDate);
			statement.setString(5, imgUrl);			
			System.out.println("\nDBYelpImport executing query:\n" + sql);
			statement.executeUpdate();
			System.out.println(courseIds);
			if (courseIds != null && courseIds.length > 0) {
				for (String id : courseIds) {
					System.out.println(id);
					insertCourse(studentId, id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void insertCourse(String studentId, String courseId) {
		try {
			String sql = "INSERT INTO enrollrecord (student_id, course_id) VALUES"
					+ " (?, ?)";			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, studentId);
			statement.setString(2, courseId);
			System.out.println("\nDBYelpImport executing query:\n" + sql);
			statement.executeUpdate();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteStudent(String studentId) {
		try {
			// Delete records in enrollrecord table
			String sql = "DELETE FROM enrollrecord WHERE student_id = ?";
			System.out.println("\nDBYelpImport executing query:\n" + sql);
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, studentId);
			statement.executeUpdate();
			// Delete record in student table
			sql = "DELETE FROM student WHERE student_id = ?";
			System.out.println("\nDBYelpImport executing query:\n" + sql);
			statement = conn.prepareStatement(sql);
			statement.setString(1, studentId);
			statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public JSONArray getAvailableCourses(Set<String> enrolledCourseIds) {
		JSONArray array = new JSONArray();
		try {
			String sql = "SELECT * FROM course";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				String courseId = rs.getString("course_id");
				if (!enrolledCourseIds.contains(courseId)) {
					array.put(new JSONObject().put("courseName", courseName).put("courseId", courseId));
				}
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return array;
	}

}
