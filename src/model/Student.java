package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class Student {
	private final String firstName;
	private final String lastName;
	private final String studentId;
	private Date dob;
	private Map<String, String> courseEnrolled;
	private Set<String> courseIds;
	private String imgUrl;
	
	private Student(StudentBuilder builder) {		
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.studentId = builder.studentId;
		this.dob = builder.dob;
		this.imgUrl = builder.imgUrl;
		this.courseIds = builder.courseIds;
		this.courseEnrolled = builder.courseEnrolled;
	}
	
	public static String[] jsonArrayToStringArray(JSONArray array) {
		String[] strArray = new String[array.length()];
		try {
			
			for (int i = 0; i < array.length(); i++) {
				strArray[i] = (String) array.get(i);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return strArray;
	}


	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			JSONArray courseMap = new JSONArray();
			int n = courseIds.size();
			if (n > 0) {
				for (Map.Entry<String, String> entry : courseEnrolled.entrySet()) {
					JSONObject course = new JSONObject();
					course.put("courseId", entry.getKey());
					course.put("courseName", entry.getValue());
					courseMap.put(course);
				}
			}
			obj.put("firstName", firstName);
			obj.put("lastName", lastName);
			obj.put("studentId", studentId);
			obj.put("dob", dob);
			obj.put("imgUrl", imgUrl);
			obj.put("courseIds", courseIds);
			obj.put("courseEnrolled", courseMap);
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static class StudentBuilder {
		private final String firstName;
		private final String lastName;
		private Date dob;
		private final String studentId;
		private Map<String, String> courseEnrolled;
		private Set<String> courseIds;
		private String imgUrl;
		
		public StudentBuilder(String firstName, String lastName, String studentId) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.studentId = studentId;
			this.courseEnrolled = new HashMap<String, String>();
			this.courseIds = new HashSet<>();
			this.dob = null;
		}
		
		public Date getDob() {
			return dob;
		}

		public Map<String, String> getCourseEnrolled() {
			return courseEnrolled;
		}
		

		public Set<String> getCourseIds() {
			return courseIds;
		}	

		public String getImgUrl() {
			return imgUrl;
		}		

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getStudentId() {
			return studentId;
		}

		public StudentBuilder dob(Date birthDate) {
			this.dob = birthDate;
			return this;
		}
		
		public StudentBuilder imgUrl(String url) {
			this.imgUrl = url;
			return this;
		}
		
		public StudentBuilder courseIdSet(Set<String> courseIds) {
			this.courseIds = courseIds;
			return this;
		}
		
		public StudentBuilder courseList(Map<String, String> coursesMap) {
			this.courseEnrolled = coursesMap;
			return this;
		}
		
		public StudentBuilder addCourse(String courseId) {
			this.courseIds.add(courseId);
			return this;
		}
		public Student build() {
			return new Student(this);
		}
	}

	public static void main(String[] args) {
		Student newStudent = new Student.StudentBuilder("firstName", "lastName", "studentId")
				.build();
	}	
	
}
