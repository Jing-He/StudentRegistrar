package api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.MySQLDBConnection;
import model.Student;

/**
 * Servlet implementation class AddStudent
 */
@WebServlet("/student")
public class AddStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DBConnection connection = new MySQLDBConnection();  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddStudent() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = new MySQLDBConnection();
		JSONArray array = null;
		if (request.getParameterMap().containsKey("fullname")) {
			String fullname = request.getParameter("fullname");
			String[] names = fullname.split("\\W+");			
			array = connection.searchStudent(names);			
		}
		RpcParser.writeOutput(response, array);
	}

	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			JSONObject input = RpcParser.parseInput(request);
			if (input.has("firstName") && input.has("lastName")
					&& input.has("studentId")) {
				String studentId = input.getString("studentId");
				String firstName = input.getString("firstName");
				String lastName = input.getString("lastName");
				String birthDate = null;
				String imgUrl = null;
				String[] courseIds = null;
				if(input.has("birthDate")) {
					birthDate = input.getString("birthDate");	
				}
				if (input.has("imageUrl")) {
					imgUrl = input.getString("imageUrl");
				}
				if (input.has("courseIds")) {
					JSONArray array = input.getJSONArray("courseIds");					
					courseIds = Student.jsonArrayToStringArray(array);
				}
				connection.createStudent(firstName, lastName, studentId, birthDate, imgUrl, courseIds);				
				RpcParser.writeOutput(response, new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcParser.parseInput(request);
			if (input.has("firstName") && input.has("lastName")
					&& input.has("studentId")) {
				String studentId = input.getString("studentId"); 
				String firstName = input.getString("firstName");
				String lastName = input.getString("lastName");
				String birthDate = null;
				String imgUrl = null;
				String[] courseIds = null;
				if(input.has("birthDate")) {
					birthDate = input.getString("birthDate");	
				}
				if (input.has("imgUrl")) {
					imgUrl = input.getString("imgUrl");
				}
				if (input.has("courseIds")) {
					JSONArray array = input.getJSONArray("courseIds");					
					courseIds = Student.jsonArrayToStringArray(array);
				}
				connection.updateStudent(firstName, lastName, studentId, birthDate, imgUrl, courseIds);				
				RpcParser.writeOutput(response, new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcParser.parseInput(request);
			if (input.has("studentId")) {
				String studentId = (String) input.get("studentId");
				connection.deleteStudent(studentId);
				RpcParser.writeOutput(response, new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*public static void main(String[] args) {
		connection.updateStudent("Lily", "Levy", "123456", null, null, new String[]{"31124","31787","32464"});
		                                                        		
	}*/
	
}
