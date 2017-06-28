/**
 * 
 */

(function () {

	/**
	 * Variables
	 */
	var default_img = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Tamia_striatus_eating.jpg/220px-Tamia_striatus_eating.jpg";

	/**
	 * Initialize
	 */
	function init() {
		// Register event listeners
		$('find-btn').addEventListener('click', showSearchStudentPage);
		$('create-btn').addEventListener('click', showCreateStudentPage);
		$('search-btn').addEventListener('click', searchWithName);
		$('add-course-btn').addEventListener('click', addCourse);
		$('save-btn').addEventListener('click', createStudent);
		// $('edit-profile-btn').addEventListener('click', editStudent);
		// $('delete-profile-btn').addEventListener('click', deleteStudent);
	}
	function showSearchStudentPage() {
		//alert("search student page");
		cleanProfilePage();
		cleanSearchPage();
		var searchStudentPage = $("search-student-page");
		var createStudentPage = $("profile-page");
		showElement(searchStudentPage, 'block');
		hideElement(createStudentPage);
	}

	function showCreateStudentPage() {
		//alert('creating student...');
		cleanProfilePage();
		var searchStudentPage = $("search-student-page");
		var createStudentPage = $("profile-page");
		showElement(createStudentPage, 'flex');
		hideElement(searchStudentPage);
		hideElement($("courseTable-div"));
		$('fname').readOnly = false;
		$('lname').readOnly = false;
		$('studentId').readOnly = false;
		
		$('cancel-btn').addEventListener('click', showSearchStudentPage);
		
		var courses = ['\'\''];
		loadCourses(courses);
	}
	
	// -------------------------------------------------
	//  AJAX call server-side APIs
	// -------------------------------------------------
	function loadCourses(enrolledCourses) {
		var method = 'GET';
		var req = JSON.stringify({});
		var url = './courses?';
		var n = enrolledCourses.length;
		for (var i = 0; i < n - 1; i++) {
			url = url + 'course_id[]=' + enrolledCourses[i] + '&';
		}
		url = url + 'course_id[]=' + enrolledCourses[n - 1];
		//alert(url);
		ajax(method, url, req,
			// Successful callback
			function (res) {
				var availableCourses = JSON.parse(res);
				//alert(availableCourses[0]);
				if (availableCourses.length != 0 ) {
					listCoursesInSelector(availableCourses);
				}
		}
		);
	}
	function listCoursesInSelector(courses) {
		var selector = $('selector');
		// Clear all options
		
		// add default selection
		
		//  
		//alert(selector.length);
		while (selector.length > 0) {			
				selector.remove(0);			
		}
		// Add default
		var option = $('option', {
			value: 'Select a course'
		});
		option.innerHTML = 'Select a course';
		selector.appendChild(option);
		//alert(selector.length);
		for (var i = 0; i < courses.length; i++) {
			addOneCourseInSelector(selector, courses[i].courseId, courses[i].courseName);			
		}
	}
	
	function addOneCourseInSelector(selector, courseId, courseName) {
		option = $('option', {
			id: courseId,
			className: 'course'
		});
		option.innerHTML = courseName;
		selector.appendChild(option);
	}
	
	function listCoursesInTable(courses) {
		
		var courseTable = $('courseTable');
		var old_tbody = $('tbody');
		// Clear the table contents
		/*courseTable.innerHTML = '';
		
		var tr = $('tr', {
			id: 'courseTable-header' 
		});
		// Add Header
		var th1 = $('th', {});
		th1.innerHTML = 'Course ID';
		tr.appendChild(th1);
		var th2 = $('th', {});
		th2.innerHTML = 'Course Name';
		tr.appendChild(th2);
		var th3 = $('th', {});
		th3.innerHTML = 'Delete';
		tr.appendChild(th3);*/
		var new_tbody = $('tbody', {
			id: 'tbody'
		});
		courseTable.replaceChild(new_tbody, old_tbody);
		for (var i = 0; i < courses.length; i++) {
			addCourseToTable(courses[i].courseId, courses[i].courseName);
		}
	}
	
	function addCourseToTable(courseId, courseName) {
		var tbody = $('tbody');
		var tr = $('tr', {
			id: 'course-' + courseId,
			className: 'course'
		});
		var td1 = $('td', {});
		td1.innerHTML = courseId;
		tr.appendChild(td1);
		var td2 = $('td', {});
		td2.innerHTML = courseName;
		tr.appendChild(td2);
		var td3 = $('td', {});
		var i = $(i, {
			className: 'fa fa-trash-o deleteCourse'
		});
		td3.appendChild(i);
		tr.appendChild(td3);	
		tbody.appendChild(tr);
		i.onclick = function() {
			deleteFromTable(tr, tbody);
			addOneCourseInSelector($('selector'), courseId, courseName);
		};	
	}
	
	function deleteFromTable(tr, tbody) {
		tbody.removeChild(tr);		
	}
	
	function createStudent() {
		alert("I'm here");
		
		/*
		req = appendAttribute(req, 'fname', "firstName");
		req = appendAttribute(req, 'lname', "lastName");
		req = appendAttribute(req, 'studentId', "studentId");
		req = appendAttribute(req, 'birthday', "birthDate");
		req = appendAttribute(req, 'imgUrl', "imageUrl");
		*/
		
		//alert(req.lastName);
		var isCreated = $('fname').readOnly;
		alert(isCreated);
		var table = document.getElementById('courseTable');
		var style = window.getComputedStyle(table);
		alert(style);
		var courseIdArray;
		var display = style.getPropertyValue('display');
		if (display != 'none') {
			courseIdArray = readTable('courseTable');
			//req[courseIds] = courseIdArray;
		}
		var firstName = $('fname');
		var lastName = $('lname');
		var studentId = $('studentId');
		var birthday = $('birthday');
		var imageUrl = $('imgUrl');
		var req = {firstName: firstName.value,
				lastName: lastName.value,
				studentId: studentId.value,
				birthDate: birthday.value,
				imageUrl: imageUrl.value,
				courseIds: courseIdArray
		};
		var request = JSON.stringify(req);
		alert(request);
		var url = './student';
		var method = isCreated ? 'POST' : 'PUT';

		ajax(method, url, request,
				// successful callback
				function (res) {
			var result = JSON.parse(res);
			if (result.status === 'OK') {
				showProfile();
			} else {
				var note = $('field-requirement');				
				note.innerHTML = '*NOTE: Please fill up the fields with an asterik (*) to create a new user.';
				note.style.display = 'inline';
			}
		}
		);
		cleanProfilePage();	
	}

	function appendAttribute(obj, id, atrributeName) {
		var value = $(id).value;
		alert(value);
		if (value != '') {
			obj[attributeName] = value;
		}
		alert(obj);
		return obj;
	}

	function readTable(tablIde) {
		var array = [];
		var table = document.getElementById(tablIde);
		var rowN = table.rows.length;
		for (var i = 1; i < rowN; i++) {
			var rowCells = table.rows.item(i).cells;
			array.push(rowCells[0].innerHTML);
		}
		return array;
	}
	function deleteTableContents(table) {
		var c = table.children;	
		var array = new Array(c.length - 1);
		var count = 0;
		for (var i = 0; i < c.length; i++) {
			alert(c[i].className);
			/*if (c[i].className) {
				array[count++] = c[i];
			}*/
		}
		/*for (var i = 0; i < array.length; i++) {
			deleteFromTable(array[i], table);
		}*/
	}
	
	function addCourse() {
		//alert('Hello I am here');
		var selector = document.getElementById('selector');
		var courseName = selector[selector.selectedIndex].text;
		//alert(courseName);
		var courseId = selector[selector.selectedIndex].id;
		//alert(courseId);
		// Set selector to default value
		selector.selectedIndex = 0;
		//alert('hello');
		// remove the option
		var option = document.getElementById(courseId);
		selector.removeChild(option);
		
		showElement($("courseTable-div"));		
		addCourseToTable(courseId, courseName);
		
	}


	/**
	 * API #1
	 * Search a student with input name.
	 * API end point: [GET] /StudentRegistrar/student?fullname=xxxx
	 */
	function searchWithName() {
		//console.log('Search student...');

		// The request parameters
		var url = './student';
		var fullname = $('fullname').value;
		var params = 'fullname=' + fullname;
		var req = JSON.stringify({});
		//alert(params);
		// display loading message
		//showLoadingMessage('Searching for student' + fullname + '...');  

		ajax('GET', url + '?' + params, req,
				// successful callback
				function (res) {
			var students = JSON.parse(res);
			//alert(students);
			if (!students || students.length === 0) {
				showWarningMessage('No student' + fullname + ' found.');
			} else {
				listStudents(students);
			}
		},
		// failed callback
		function () {
			showErrorMessage('Cannot load student ' + fullname + '.');
		}
		);
	}

	/**
	 * 
	 *
	 */


	function listStudents (students) {
		// Clear the current results
		var studentTable = $('studentTable');
		var old_tbody = $('student-tbody');
		var new_tbody = $('tbody', {
			id: 'student-tbody'
		});
		alert("hello!");		
		for (var i = 0; i < students.length; i++) {
			
			addStudent(new_tbody, students[i], students);
			alert(new_tbody);
		}
		studentTable.replaceChild(new_tbody, old_tbody);
		showElement($('studentTable-div'), 'block');
	}

	function addStudent(tbody, student, students) {
		var student_id = student.studentId;
		var tr = $('tr', {
			id: 'student-' + student_id,
			className: 'student'
		});
		tbody.appendChild(tr);
		alert("tbody");
		var td1 = $('td', {
			className: 'name'
		});

		var name = $('p', {
			className: 'fullname'
		})

		name.innerHTML = student.firstName + ' ' + student.lastName;
		name.onclick = function() {
			browseProfilePage();
		}
		alert(name.innerHTML);		
		td1.appendChild(name);
		tr.appendChild(td1);

		var td2 = $('td', {
			className: 'studentId'
		});
		var id = $('p', {
			className: 'studentId_p'
		});
		id.innerHTML = student_id;
		alert("td2 appended.");
		td2.appendChild(id);
		tr.appendChild(td2);
		var td3 = $('td', {
			className: 'fa-icons'
		});
		

		var icon1 = $('i', {
			id: 'edit-icon-' + student_id,
			className: 'fa fa-pencil-square-o edit-profile-btn'
		});
		alert('i created');
		icon1.onclick = function() {
			editStudent(student);
		};
		alert('icon1 action added.');
		
		td3.appendChild(icon1);
		alert('icon1 ready');
		var icon2 = $('i', {
			id: 'delete-icon-' + student_id,
			className: 'fa fa-trash-o delete-profile-btn'
		});
		icon2.onclick = function() {
			var index = students.indexOf(student);
			alert(index);
			if (index != -1) {
				students.splice(index, 1);
			}
			deleteStudent(student_id, students);
		};
		td3.appendChild(icon2);
		tr.appendChild(td3);
		alert('ready.')
		//tbody.appendChild(tr);
	}

	
	function cleanSearchPage() {
		$('fullname').value ='';
		$('student-tbody').innerHTML = '';
		$('studentTable-div').style.display = 'none';
		
	}
	function cleanProfilePage() {
		// Clear all fields
		$('fname').value = '';
		$('lname').value = '';
		$('studentId').value = '';
		$('imgUrl').value = '';
		$('birthday').value = '';
		$('image').src = default_img;
		var old_tbody = $('tbody')
		var new_tbody = $('tbody', {
			id: 'tbody'
		});
		var courseTable = $('courseTable');
		courseTable.replaceChild(new_tbody, old_tbody);
	}
	
	function editStudent(student) {
		
		// Clean search page
		$('fullname').value = '';
		$('student-tbody').innerHTML = '';
		hideElement($('studentTable-div'));
		firstName = $('fname');
		alert(student);
		firstName.value = student['firstName'];
		alert(firstName.value);
		firstName.readOnly = true;
		lastName = $('lname');
		lastName.value = student['lastName'];
		lastName.readOnly = true;
		alert(lastName.value);
		studentId= $('studentId');
		studentId.value = student['studentId'];
		studentId.readOnly = true;
		if (typeof student['dob'] != 'undefined') {
			var birthday = $('birthday');
			alert(student['dob']);
			birthday.value = student['dob'];
		}
		if (typeof student['imgUrl'] != 'undefined') {
			var imgUrl = $('imgUrl');
			alert(student['imgUrl']);
			imgUrl.value = student['imgUrl'];
			var image = $('image');
			alert(image);
			image.src=student['imgUrl'];
			alert("Image Ready");
		}
		alert(student['courseIds']);
		if (typeof student['courseIds']!= 'undefined') {
			//alert(student[])
			loadCourses(student['courseIds']);
			
			//alert(student['courseEnrolled'][0]);
			for (var i = 0; i < student['courseEnrolled'].length; i++) {
				var course = student['courseEnrolled'][i];
				addCourseToTable(course['courseId'], course['courseName']);
			}
		}
		showElement($('profile-page'),'flex');
		hideElement($('search-student-page'));
		
		
		//alert(student.image_url);
	}

	function deleteStudent(student_id, students) {
		console.log('Delete student ID: ' + student_id);

		// The request parameters
		var url = './student';    
		var req = JSON.stringify({
			studentId: student_id
		});
		ajax('DELETE', url, req,
				// successful callback
				function (res) {
			listStudents(students);
		});
	}



	function showLoadingMessage(msg) {
		var studentTableContainer = $('studenttable-container');
		studentTableContainer.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' + msg + '</p>';
	}

	function showWarningMessage(msg) {
		var studentTableContainer = $('studenttable-container');
		studentTableContainer.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' + msg + '</p>';
	}

	function showErrorMessage(msg) {
		var studentTableContainer = $('studenttable-container');
		studentTableContainer.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' + msg + '</p>';
	}

	/**
	 * A helper function that creates a DOM element <tag options...>
	 * 
	 * @param tag
	 * @param options
	 * @returns
	 */
	function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}

		var element = document.createElement(tag);

		for (var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}

		return element;
	}

	/**
	 * AJAX helper
	 * 
	 * @param method - GET|POST|PUT|DELETE
	 * @param url - API end point
	 * @param callback - This the successful callback
	 * @param errorHandler - This is the failed callback
	 */
	function ajax(method, url, data, callback, errorHandler) {
		var xhr = new XMLHttpRequest();

		xhr.open(method, url, true);

		xhr.onload = function () {
			switch (xhr.status) {
			case 200:
				callback(xhr.responseText);
				break;
			case 403:		
				break;
			case 401:
				errorHandler();
				break;
			}
		};

		xhr.onerror = function () {
			console.error("The request couldn't be completed.");
			errorHandler();
		};

		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
			xhr.send(data);
		}
	}  

	function hideElement(element) {
		element.style.display = 'none';
	}

	function showElement(element, style) {
		var displayStyle = style ? style : 'initial';
		element.style.display = displayStyle;
	}


	init();

})();

//END