package api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.MySQLDBConnection;
import model.Student;

/**
 * Servlet implementation class GetCourses
 */
@WebServlet("/courses")
public class GetCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static DBConnection connection = new MySQLDBConnection();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCourses() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray availableCourses = null;
		try {
			DBConnection connection = new MySQLDBConnection();

			if (request.getParameterMap().containsKey("course_id[]")) {
				Map<String, String[]> map = request.getParameterMap();
				Iterator it = map.entrySet().iterator();
				Set<String> enrolledCourseIds = new HashSet<>();
				while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        String[] courseId= (String[]) pair.getValue();
				        for (String str : courseId) {
				        	enrolledCourseIds.add(str);
				        }
				}
				
				/*for (String[] i : list) {
					for (String j : i) {
						enrolledCourseIds.add(j);
					}
				}*/			
				availableCourses = connection.getAvailableCourses(enrolledCourseIds);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		RpcParser.writeOutput(response, availableCourses);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public static void main(String[] args) {
		Set<String> set = new HashSet<>();
		set.add("31124");
		JSONArray array = connection.getAvailableCourses(set);
		System.out.println(array);
	}

}
