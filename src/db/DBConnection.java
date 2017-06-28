package db;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close() ;
	
	public void createStudent(String firstName, String lastName, String studentId, String birthDate, String img_url, String[] courseIds);

	public void updateStudent(String firstName, String lastName, String studentId, String birthDate, String imgUrl, String[] courseIds);
	
	public Set<String> stringArrayToSet(String[] courseIds);
	
	public java.sql.Date stringToDate(String birthDate);
	/**
	 * Insert the course for a student.
	 * @param studentId
	 * @param businessIds
	 */
	public void addCourse(String studentId, String courseId);

	/**
	 * Delete the visited restaurants for a user.
	 * @param studentId
	 * @param businessIds
	 */
	public void removeCourse(String studentId, String courseId);

	/**
	 * Search for student records
	 * @param name
	 * @return
	 */
	public JSONArray searchStudent(String[] name);
	/**
	 * Get the visited restaurants for a user.
	 * @param studentId
	 * @return
	 */
	public Set<String> getSelectedCourses(String studentId);
	
	/**
	 * Convert courseId set to Course Name List.
	 * @param courseIds
	 * @return
	 */
	
	public Map<String, String> courseIdToNameMap(Set<String> courseIds);
	/**
	 * Get the restaurant json by id.
	 * @param businessId
	 * @param isVisited, set the visited field in json.
	 * @return
	 */
	public JSONObject getStudentById(String studentId);
	
	/**
	 * Get studentIds whose first or last name matches with the search name.	
	 * @param names
	 * @return a set of studentIds.
	 */
	
	public Set<String> getStudentId(String[] names);

	/**
	 * Get user's name for the studentId.
	 * @param studentId
	 * @return First and Last Name
	 */
	public String getFirstLastName(String studentId);
	
	/**
	 * Delete student record in student and enrollrecord table.
	 * @param studentId
	 */
	public void deleteStudent(String studentId);
	/**
	 * Return the courses not in the enrolledCourseIds array from the course table.
	 * @param enrolledCourseIds
	 * @return
	 */
	public JSONArray getAvailableCourses(Set<String> enrolledCourseIds);

}
