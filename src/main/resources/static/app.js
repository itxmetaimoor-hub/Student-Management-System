// STATE MANAGEMENT
const state = {
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || null,
    role: localStorage.getItem('role') || null,
    activeSection: 'dashboard'
};

// INITIALIZATION
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    lucide.createIcons();
    if (state.token) {
        showDashboard();
    } else {
        showAuth();
    }
}

// VIEW SWITCHERS
function showAuth() {
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('app-container').classList.add('hidden');
    switchAuthTab('login');
}

function showDashboard() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('app-container').classList.remove('hidden');
    
    // Set Profile UI
    document.getElementById('user-display-name').innerText = state.username;
    const roleBadge = document.getElementById('user-display-role');
    roleBadge.innerText = state.role;
    roleBadge.className = 'role-badge ' + state.role.toLowerCase();

    // Adjust Nav visibility
    applyRoleBasedVisibility();
    
    // Show default section
    showSection('dashboard');
}

function switchAuthTab(tab) {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const tabSlider = document.querySelector('.tab-slider');
    const btns = document.querySelectorAll('.tab-btn');

    if (tab === 'login') {
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
        btns[0].classList.add('active');
        btns[1].classList.remove('active');
        tabSlider.style.transform = 'translateX(0)';
    } else {
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
        btns[0].classList.remove('active');
        btns[1].classList.add('active');
        tabSlider.style.transform = 'translateX(100%)';
    }
}

function applyRoleBasedVisibility() {
    const role = state.role;

    // Sidebar navigation visibility
    toggleVisibility('nav-students', role === 'ADMIN');
    toggleVisibility('nav-courses', role === 'ADMIN');
    toggleVisibility('nav-results', role === 'ADMIN');
    
    toggleVisibility('nav-teachers', role === 'ADMIN' || role === 'TEACHER');
    toggleVisibility('nav-attendance', role === 'ADMIN' || role === 'TEACHER');
    
    toggleVisibility('nav-enrollments', role === 'ADMIN' || role === 'STUDENT');

    // Section Action buttons visibility
    toggleVisibility('btn-add-student-trigger', role === 'ADMIN');
    toggleVisibility('btn-add-teacher-trigger', role === 'ADMIN');
    toggleVisibility('btn-add-course-trigger', role === 'ADMIN');
    toggleVisibility('btn-add-result-trigger', role === 'ADMIN');
    toggleVisibility('btn-add-attendance-trigger', role === 'ADMIN' || role === 'TEACHER');
    toggleVisibility('btn-add-enrollment-trigger', role === 'ADMIN' || role === 'STUDENT');

    // Populate dashboard permissions explanation list
    const pList = document.getElementById('permissions-list');
    pList.innerHTML = '';
    
    if (role === 'ADMIN') {
        pList.innerHTML += `
            <li>Full read/write permissions for Students, Courses, Results</li>
            <li>Full read/write permissions for Teachers, Attendance</li>
            <li>Full read/write permissions for Course Enrollments</li>
        `;
    } else if (role === 'TEACHER') {
        pList.innerHTML += `
            <li>Read/write access to Teacher directory and Attendance logging</li>
            <li>Cannot access Student details, Course registrations, or Results</li>
        `;
    } else if (role === 'STUDENT') {
        pList.innerHTML += `
            <li>Read/write access to register course enrollments</li>
            <li>Cannot modify Students, Teachers, Courses, Results, or Attendance</li>
        `;
    }
}

function toggleVisibility(id, condition) {
    const el = document.getElementById(id);
    if (el) {
        if (condition) el.classList.remove('hidden');
        else el.classList.add('hidden');
    }
}

function showSection(sectionId) {
    state.activeSection = sectionId;
    
    // Set title
    const formattedTitle = sectionId.charAt(0).toUpperCase() + sectionId.slice(1);
    document.getElementById('section-title').innerText = formattedTitle;

    // Toggle active section
    document.querySelectorAll('.content-section').forEach(sec => {
        sec.classList.remove('active');
    });
    const targetSec = document.getElementById(`sec-${sectionId}`);
    if (targetSec) targetSec.classList.add('active');

    // Toggle active sidebar item
    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.classList.remove('active');
    });
    const activeBtn = Array.from(document.querySelectorAll('.nav-item')).find(btn => 
        btn.getAttribute('onclick').includes(`'${sectionId}'`)
    );
    if (activeBtn) activeBtn.classList.add('active');

    // Load data for the section
    loadSectionData(sectionId);
}

// REST CLIENT HELPER
async function fetchAPI(endpoint, options = {}) {
    const url = `http://localhost:8080${endpoint}`;
    
    // Inject headers
    const headers = { ...options.headers };
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }
    if (options.body && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(url, config);

        // Handle unauthorized
        if (response.status === 401 || response.status === 403) {
            showToast('Access Denied: You do not have permissions for this action.', 'error');
            if (response.status === 401) {
                handleLogout();
            }
            throw new Error('Unauthorized');
        }

        // If no content returned
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (err) {
        if (err.message !== 'Unauthorized') {
            console.error('API Error:', err);
            showToast('Network error or server returned failure.', 'error');
        }
        throw err;
    }
}

// AUTH HANDLERS
async function handleLogin(event) {
    event.preventDefault();
    const usernameEl = document.getElementById('login-username');
    const passwordEl = document.getElementById('login-password');

    const body = JSON.stringify({
        username: usernameEl.value,
        password: passwordEl.value,
        role: "LOGIN" // Dummy role for validation DTO matches if any
    });

    try {
        const data = await fetchAPI('/auth/login', {
            method: 'POST',
            body
        });

        if (data.token) {
            state.token = data.token;
            state.username = data.username;
            state.role = data.role;

            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);

            showToast('Login successful!', 'success');
            showDashboard();

            // Clear inputs
            usernameEl.value = '';
            passwordEl.value = '';
        } else {
            showToast(data || 'Invalid credentials', 'error');
        }
    } catch (err) {
        showToast('Login failed. Please check your credentials.', 'error');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const usernameEl = document.getElementById('register-username');
    const passwordEl = document.getElementById('register-password');
    const roleEl = document.getElementById('register-role');

    const body = JSON.stringify({
        username: usernameEl.value,
        password: passwordEl.value,
        role: roleEl.value
    });

    try {
        await fetchAPI('/auth/register', {
            method: 'POST',
            body
        });

        showToast('Registration successful! You can now log in.', 'success');
        switchAuthTab('login');

        // Clear inputs
        usernameEl.value = '';
        passwordEl.value = '';
        roleEl.selectedIndex = 0;
    } catch (err) {
        showToast('Registration failed. Username might be taken.', 'error');
    }
}

function handleLogout() {
    state.token = null;
    state.username = null;
    state.role = null;

    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');

    showToast('Logged out successfully.', 'info');
    showAuth();
}

// DATA LOAD CONTROLLERS
function loadSectionData(sectionId) {
    switch (sectionId) {
        case 'dashboard':
            loadStats();
            break;
        case 'students':
            loadStudents();
            break;
        case 'teachers':
            loadTeachers();
            break;
        case 'courses':
            loadCourses();
            break;
        case 'attendance':
            loadAttendance();
            break;
        case 'enrollments':
            loadEnrollments();
            break;
        case 'results':
            loadResults();
            break;
    }
}

async function loadStats() {
    try {
        // We can load count of records directly by listing the data or custom endpoints
        // For simplicity, we just list and count
        if (state.role === 'ADMIN') {
            const students = await fetchAPI('/students');
            const courses = await fetchAPI('/courses');
            const teachers = await fetchAPI('/teachers');

            document.getElementById('stat-students-count').innerText = students.length;
            document.getElementById('stat-courses-count').innerText = courses.length;
            document.getElementById('stat-teachers-count').innerText = teachers.length;
        } else {
            document.getElementById('stat-students-count').innerText = 'N/A';
            document.getElementById('stat-courses-count').innerText = 'N/A';
            document.getElementById('stat-teachers-count').innerText = 'N/A';
        }
    } catch (err) {
        // Silently handle if user doesn't have roles
    }
}

// CRUD ACTIONS: STUDENTS
async function loadStudents() {
    try {
        const students = await fetchAPI('/students');
        const tbody = document.querySelector('#tbl-students tbody');
        tbody.innerHTML = '';
        students.forEach(st => {
            tbody.innerHTML += `
                <tr>
                    <td>${st.id}</td>
                    <td>${st.name}</td>
                    <td>${st.course}</td>
                    <td>${st.age}</td>
                    <td class="col-actions">
                        <button class="table-action-btn edit" onclick="editStudent(${st.id}, '${st.name}', '${st.course}', ${st.age})">
                            <i data-lucide="edit-3"></i>
                        </button>
                        <button class="table-action-btn delete" onclick="deleteStudent(${st.id})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveStudent(event) {
    event.preventDefault();
    const id = document.getElementById('student-id').value;
    const name = document.getElementById('student-name').value;
    const course = document.getElementById('student-course').value;
    const age = document.getElementById('student-age').value;

    const payload = { name, course, age: parseInt(age) };
    const method = id ? 'PUT' : 'POST';
    const endpoint = id ? `/students/${id}` : '/students';

    try {
        await fetchAPI(endpoint, {
            method,
            body: JSON.stringify(payload)
        });
        showToast('Student saved successfully!', 'success');
        closeModal('modal-student');
        loadStudents();
    } catch (err) {}
}

function editStudent(id, name, course, age) {
    document.getElementById('student-id').value = id;
    document.getElementById('student-name').value = name;
    document.getElementById('student-course').value = course;
    document.getElementById('student-age').value = age;
    document.getElementById('student-modal-title').innerText = 'Update Student';
    openModal('modal-student');
}

async function deleteStudent(id) {
    if (confirm('Are you sure you want to delete this student?')) {
        try {
            await fetchAPI(`/students/${id}`, { method: 'DELETE' });
            showToast('Student deleted successfully.', 'success');
            loadStudents();
        } catch (err) {}
    }
}

// CRUD ACTIONS: TEACHERS
async function loadTeachers() {
    try {
        const teachers = await fetchAPI('/teachers');
        const tbody = document.querySelector('#tbl-teachers tbody');
        tbody.innerHTML = '';
        teachers.forEach(t => {
            tbody.innerHTML += `
                <tr>
                    <td>${t.id}</td>
                    <td>${t.name}</td>
                    <td>${t.subject}</td>
                    <td>${t.email}</td>
                    <td class="col-actions">
                        ${state.role === 'ADMIN' ? `
                        <button class="table-action-btn edit" onclick="editTeacher(${t.id}, '${t.name}', '${t.subject}', '${t.email}')">
                            <i data-lucide="edit-3"></i>
                        </button>
                        <button class="table-action-btn delete" onclick="deleteTeacher(${t.id})">
                            <i data-lucide="trash-2"></i>
                        </button>` : 'N/A'}
                    </td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveTeacher(event) {
    event.preventDefault();
    const id = document.getElementById('teacher-id').value;
    const name = document.getElementById('teacher-name').value;
    const subject = document.getElementById('teacher-subject').value;
    const email = document.getElementById('teacher-email').value;

    const payload = { name, subject, email };
    const method = id ? 'PUT' : 'POST';
    const endpoint = id ? `/teachers/${id}` : '/teachers';

    try {
        await fetchAPI(endpoint, {
            method,
            body: JSON.stringify(payload)
        });
        showToast('Teacher saved successfully!', 'success');
        closeModal('modal-teacher');
        loadTeachers();
    } catch (err) {}
}

function editTeacher(id, name, subject, email) {
    document.getElementById('teacher-id').value = id;
    document.getElementById('teacher-name').value = name;
    document.getElementById('teacher-subject').value = subject;
    document.getElementById('teacher-email').value = email;
    document.getElementById('teacher-modal-title').innerText = 'Update Teacher';
    openModal('modal-teacher');
}

async function deleteTeacher(id) {
    if (confirm('Are you sure you want to delete this teacher?')) {
        try {
            await fetchAPI(`/teachers/${id}`, { method: 'DELETE' });
            showToast('Teacher deleted successfully.', 'success');
            loadTeachers();
        } catch (err) {}
    }
}

// CRUD ACTIONS: COURSES
async function loadCourses() {
    try {
        const courses = await fetchAPI('/courses');
        const tbody = document.querySelector('#tbl-courses tbody');
        tbody.innerHTML = '';
        courses.forEach(c => {
            tbody.innerHTML += `
                <tr>
                    <td>${c.id}</td>
                    <td>${c.title}</td>
                    <td>${c.instructor}</td>
                    <td class="col-actions">
                        <button class="table-action-btn edit" onclick="editCourse(${c.id}, '${c.title}', '${c.instructor}')">
                            <i data-lucide="edit-3"></i>
                        </button>
                        <button class="table-action-btn delete" onclick="deleteCourse(${c.id})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveCourse(event) {
    event.preventDefault();
    const id = document.getElementById('course-id').value;
    const title = document.getElementById('course-title').value;
    const instructor = document.getElementById('course-instructor').value;

    const payload = { title, instructor };
    const method = id ? 'PUT' : 'POST';
    const endpoint = id ? `/courses/${id}` : '/courses';

    try {
        await fetchAPI(endpoint, {
            method,
            body: JSON.stringify(payload)
        });
        showToast('Course saved successfully!', 'success');
        closeModal('modal-course');
        loadCourses();
    } catch (err) {}
}

function editCourse(id, title, instructor) {
    document.getElementById('course-id').value = id;
    document.getElementById('course-title').value = title;
    document.getElementById('course-instructor').value = instructor;
    document.getElementById('course-modal-title').innerText = 'Update Course';
    openModal('modal-course');
}

async function deleteCourse(id) {
    if (confirm('Are you sure you want to delete this course?')) {
        try {
            await fetchAPI(`/courses/${id}`, { method: 'DELETE' });
            showToast('Course deleted successfully.', 'success');
            loadCourses();
        } catch (err) {}
    }
}

// CRUD ACTIONS: ATTENDANCE
async function loadAttendance() {
    try {
        const list = await fetchAPI('/attendance');
        const tbody = document.querySelector('#tbl-attendance tbody');
        tbody.innerHTML = '';
        list.forEach(att => {
            tbody.innerHTML += `
                <tr>
                    <td>${att.id}</td>
                    <td>${att.studentId}</td>
                    <td>${att.courseId}</td>
                    <td>${att.attendanceDate}</td>
                    <td><span class="role-badge ${att.status === 'Present' ? 'student' : 'admin'}">${att.status}</span></td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveAttendance(event) {
    event.preventDefault();
    const studentId = document.getElementById('attendance-student-id').value;
    const courseId = document.getElementById('attendance-course-id').value;
    const date = document.getElementById('attendance-date').value;
    const status = document.getElementById('attendance-status').value;

    const payload = {
        studentId: parseInt(studentId),
        courseId: parseInt(courseId),
        status,
        attendanceDate: date
    };

    try {
        await fetchAPI('/attendance', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showToast('Attendance logged successfully!', 'success');
        closeModal('modal-attendance');
        loadAttendance();
    } catch (err) {}
}

// CRUD ACTIONS: ENROLLMENTS
async function loadEnrollments() {
    try {
        const list = await fetchAPI('/enrollments');
        const tbody = document.querySelector('#tbl-enrollments tbody');
        tbody.innerHTML = '';
        list.forEach(enr => {
            tbody.innerHTML += `
                <tr>
                    <td>${enr.id}</td>
                    <td>${enr.studentId}</td>
                    <td>${enr.courseId}</td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveEnrollment(event) {
    event.preventDefault();
    const studentId = document.getElementById('enrollment-student-id').value;
    const courseId = document.getElementById('enrollment-course-id').value;

    const payload = {
        studentId: parseInt(studentId),
        courseId: parseInt(courseId)
    };

    try {
        await fetchAPI('/enrollments', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showToast('Enrollment registered successfully!', 'success');
        closeModal('modal-enrollment');
        loadEnrollments();
    } catch (err) {}
}

// CRUD ACTIONS: RESULTS
async function loadResults() {
    try {
        const list = await fetchAPI('/results');
        const tbody = document.querySelector('#tbl-results tbody');
        tbody.innerHTML = '';
        list.forEach(res => {
            tbody.innerHTML += `
                <tr>
                    <td>${res.id}</td>
                    <td>${res.studentId}</td>
                    <td>${res.courseId}</td>
                    <td>${res.marks}</td>
                    <td><span class="role-badge ${res.marks >= 50 ? 'student' : 'admin'}">${res.grade}</span></td>
                </tr>
            `;
        });
        lucide.createIcons();
    } catch (err) {}
}

async function saveResult(event) {
    event.preventDefault();
    const studentId = document.getElementById('result-student-id').value;
    const courseId = document.getElementById('result-course-id').value;
    const marks = document.getElementById('result-marks').value;
    const grade = document.getElementById('result-grade').value;

    const payload = {
        studentId: parseInt(studentId),
        courseId: parseInt(courseId),
        marks: parseInt(marks),
        grade
    };

    try {
        await fetchAPI('/results', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showToast('Result published successfully!', 'success');
        closeModal('modal-result');
        loadResults();
    } catch (err) {}
}

function calculateGrade(marks) {
    const gradeInput = document.getElementById('result-grade');
    const m = parseInt(marks);
    if (isNaN(m)) {
        gradeInput.value = '';
        return;
    }
    if (m >= 85) gradeInput.value = 'A';
    else if (m >= 70) gradeInput.value = 'B';
    else if (m >= 50) gradeInput.value = 'C';
    else if (m >= 40) gradeInput.value = 'D';
    else gradeInput.value = 'F';
}

// MODAL CONTROLS
function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
    
    // Reset inputs if any
    const form = document.querySelector(`#${modalId} form`);
    if (form) {
        form.reset();
        const hiddenId = form.querySelector('input[type="hidden"]');
        if (hiddenId) hiddenId.value = '';
    }

    // Reset title defaults
    if (modalId === 'modal-student') document.getElementById('student-modal-title').innerText = 'Add Student';
    if (modalId === 'modal-teacher') document.getElementById('teacher-modal-title').innerText = 'Add Teacher';
    if (modalId === 'modal-course') document.getElementById('course-modal-title').innerText = 'Add Course';
}

// TOAST NOTIFICATIONS
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = 'info';
    if (type === 'success') icon = 'check-circle';
    if (type === 'error') icon = 'alert-circle';
    
    toast.innerHTML = `
        <i data-lucide="${icon}"></i>
        <div class="toast-content">
            <p>${message}</p>
        </div>
    `;
    
    container.appendChild(toast);
    lucide.createIcons();
    
    setTimeout(() => {
        toast.style.transform = 'translateX(120%)';
        toast.style.opacity = '0';
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 4000);
}
