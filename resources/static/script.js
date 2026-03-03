// Global Variables
let currentUser = null;
let authToken = null;
const API_BASE = 'http://localhost:8080/api';

// Initialize Application
document.addEventListener('DOMContentLoaded', function() {
    checkExistingAuth();
    setupEventListeners();
});

// Check if user is already authenticated
function checkExistingAuth() {
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    
    if (token && user) {
        authToken = token;
        currentUser = JSON.parse(user);
        showDashboard();
    }
}

// Setup Event Listeners
function setupEventListeners() {
    // Login Form
    document.getElementById('loginFormElement').addEventListener('submit', handleLogin);
    
    // Register Form
    document.getElementById('registerFormElement').addEventListener('submit', handleRegister);
    
    // OTP Form
    document.getElementById('otpFormElement').addEventListener('submit', handleOTPVerification);
    
    // Google Login
    document.getElementById('googleLoginBtn').addEventListener('click', handleGoogleLogin);
    
    // Phone Login
    document.getElementById('phoneLoginBtn').addEventListener('click', handlePhoneLogin);
    
    // Resume Upload
    const resumeFile = document.getElementById('resumeFile');
    if (resumeFile) {
        resumeFile.addEventListener('change', handleResumeFileSelect);
    }
    
    // Search Input
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(filterJobs, 300));
    }
}

// Authentication Functions
async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            authToken = data.token;
            currentUser = data.user;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showDashboard();
            showMessage('Login successful!', 'success');
        } else {
            showMessage(data.error || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const userData = {
        fullName: document.getElementById('registerFullName').value,
        username: document.getElementById('registerUsername').value,
        email: document.getElementById('registerEmail').value,
        phoneNumber: document.getElementById('registerPhone').value,
        password: document.getElementById('registerPassword').value,
        role: document.getElementById('registerRole').value
    };
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Registration successful! Please verify your email/phone.', 'success');
            showOTPVerification();
        } else {
            showMessage(data.error || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleOTPVerification(e) {
    e.preventDefault();
    
    const identifier = currentUser ? currentUser.email : document.getElementById('registerEmail').value;
    const otp = document.getElementById('otpCode').value;
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/verify-otp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ identifier, otp })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('OTP verified successfully!', 'success');
            // Auto-login after OTP verification
            if (currentUser) {
                showDashboard();
            } else {
                showLogin();
            }
        } else {
            showMessage(data.error || 'OTP verification failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleGoogleLogin() {
    // Mock Google OAuth - In production, this would use actual Google OAuth
    const googleUserData = {
        googleId: 'google_' + Date.now(),
        email: 'user@gmail.com',
        fullName: 'Google User'
    };
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/google-login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(googleUserData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            authToken = data.token;
            currentUser = data.user;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showDashboard();
            showMessage('Google login successful!', 'success');
        } else {
            showMessage(data.error || 'Google login failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handlePhoneLogin() {
    const phoneNumber = prompt('Enter your phone number:');
    if (!phoneNumber) return;
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/send-otp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ phoneNumber })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('OTP sent to your phone!', 'success');
            showOTPVerification();
        } else {
            showMessage(data.error || 'Failed to send OTP', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function resendOTP() {
    const identifier = currentUser ? currentUser.email : document.getElementById('registerEmail').value;
    const type = identifier.includes('@') ? 'EMAIL' : 'PHONE';
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/send-otp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ [type.toLowerCase()]: identifier })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('OTP resent successfully!', 'success');
        } else {
            showMessage(data.error || 'Failed to resend OTP', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

// UI Navigation Functions
function showLogin() {
    document.getElementById('authSection').style.display = 'flex';
    document.getElementById('mainDashboard').style.display = 'none';
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('otpForm').style.display = 'none';
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    document.getElementById('otpForm').style.display = 'none';
}

function showOTPVerification() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('otpForm').style.display = 'block';
}

function showDashboard() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainDashboard').style.display = 'block';
    
    // Update UI based on user role
    updateUIForUserRole();
    
    // Load initial data
    loadDepartments();
    loadJobs();
    
    if (currentUser.role === 'ADMIN') {
        loadAdminDashboard();
    }
}

function updateUIForUserRole() {
    // Show/hide admin elements
    const adminElements = document.querySelectorAll('.admin-only');
    adminElements.forEach(el => {
        el.style.display = currentUser.role === 'ADMIN' ? 'flex' : 'none';
    });
    
    // Show/hide recruiter elements
    const recruiterElements = document.querySelectorAll('.recruiter-only');
    recruiterElements.forEach(el => {
        el.style.display = currentUser.role === 'RECRUITER' || currentUser.role === 'ADMIN' ? 'flex' : 'none';
    });
    
    // Update user name
    document.getElementById('userName').textContent = currentUser.fullName || currentUser.username;
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    authToken = null;
    currentUser = null;
    showLogin();
    showMessage('Logged out successfully', 'success');
}

// Dashboard Functions
async function loadDepartments() {
    try {
        const response = await fetch(`${API_BASE}/jobs/departments`);
        const departments = await response.json();
        
        if (response.ok) {
            displayDepartments(departments);
            populateDepartmentFilter(departments);
        }
    } catch (error) {
        console.error('Failed to load departments:', error);
    }
}

function displayDepartments(departments) {
    const container = document.getElementById('departmentsContainer');
    container.innerHTML = '';
    
    departments.forEach(dept => {
        const card = document.createElement('div');
        card.className = 'department-card fade-in';
        card.innerHTML = `
            <h3>${dept.name}</h3>
            <p>${dept.description || 'No description available'}</p>
            <div class="department-skills">
                <strong>Required Skills:</strong> ${dept.requiredSkills || 'Not specified'}
            </div>
            <div class="department-jobs-count">
                ${dept.jobs ? dept.jobs.length : 0} active jobs
            </div>
        `;
        card.onclick = () => filterByDepartment(dept.id);
        container.appendChild(card);
    });
}

function populateDepartmentFilter(departments) {
    const select = document.getElementById('departmentFilter');
    select.innerHTML = '<option value="">All Departments</option>';
    
    departments.forEach(dept => {
        const option = document.createElement('option');
        option.value = dept.id;
        option.textContent = dept.name;
        select.appendChild(option);
    });
}

async function loadJobs() {
    try {
        const response = await fetch(`${API_BASE}/jobs/all`);
        const jobs = await response.json();
        
        if (response.ok) {
            displayJobs(jobs);
        }
    } catch (error) {
        console.error('Failed to load jobs:', error);
    }
}

function displayJobs(jobs) {
    const container = document.getElementById('jobsContainer');
    container.innerHTML = '';
    
    if (jobs.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">No jobs available</p>';
        return;
    }
    
    jobs.forEach(job => {
        const card = document.createElement('div');
        card.className = 'job-card fade-in';
        card.innerHTML = `
            <div class="job-title">${job.title}</div>
            <div class="job-department">${job.department ? job.department.name : 'Unknown Department'}</div>
            <div class="job-description">${job.description.substring(0, 150)}...</div>
            <div class="job-skills">
                ${job.skills ? job.skills.split(',').map(skill => 
                    `<span class="skill-tag">${skill.trim()}</span>`
                ).join('') : ''}
            </div>
            <div class="job-meta">
                <span><i class="fas fa-map-marker-alt"></i> ${job.workLocation || 'Not specified'}</span>
                <span><i class="fas fa-briefcase"></i> ${job.jobType || 'Full-time'}</span>
            </div>
            <div class="job-actions">
                <button onclick="viewJobDetails(${job.id})" class="btn btn-secondary">
                    <i class="fas fa-eye"></i> View Details
                </button>
                ${currentUser.role === 'JOB_SEEKER' ? 
                    `<button onclick="applyForJob(${job.id})" class="btn btn-primary">
                        <i class="fas fa-paper-plane"></i> Apply
                    </button>` : ''
                }
            </div>
        `;
        container.appendChild(card);
    });
}

async function filterJobs() {
    const departmentId = document.getElementById('departmentFilter').value;
    const searchTerm = document.getElementById('searchInput').value;
    
    let url = `${API_BASE}/jobs/search?`;
    const params = [];
    
    if (departmentId) {
        const deptName = document.querySelector(`#departmentFilter option[value="${departmentId}"]`).textContent;
        params.push(`department=${encodeURIComponent(deptName)}`);
    }
    
    if (searchTerm) {
        params.push(`keyword=${encodeURIComponent(searchTerm)}`);
    }
    
    url += params.join('&');
    
    try {
        const response = await fetch(url);
        const jobs = await response.json();
        
        if (response.ok) {
            displayJobs(jobs);
        }
    } catch (error) {
        console.error('Failed to filter jobs:', error);
    }
}

function filterByDepartment(departmentId) {
    document.getElementById('departmentFilter').value = departmentId;
    filterJobs();
}

// Resume Management
function showResumeUpload() {
    const uploadForm = document.getElementById('resumeUploadForm');
    uploadForm.style.display = uploadForm.style.display === 'none' ? 'block' : 'none';
}

function handleResumeFileSelect(e) {
    const file = e.target.files[0];
    if (file) {
        uploadResume(file);
    }
}

async function uploadResume(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/resumes/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            body: formData
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Resume uploaded successfully!', 'success');
            loadMyResumes();
            document.getElementById('resumeUploadForm').style.display = 'none';
            document.getElementById('resumeFile').value = '';
        } else {
            showMessage(data.error || 'Failed to upload resume', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function loadMyResumes() {
    try {
        const response = await fetch(`${API_BASE}/resumes/my`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        const resumes = await response.json();
        
        if (response.ok) {
            displayResumes(resumes);
        }
    } catch (error) {
        console.error('Failed to load resumes:', error);
    }
}

function displayResumes(resumes) {
    const container = document.getElementById('resumesContainer');
    container.innerHTML = '';
    
    if (resumes.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">No resumes uploaded yet</p>';
        return;
    }
    
    resumes.forEach(resume => {
        const card = document.createElement('div');
        card.className = 'resume-card fade-in';
        card.innerHTML = `
            <div class="resume-name">${resume.fileName}</div>
            <div class="resume-info">Uploaded: ${new Date(resume.uploadDate).toLocaleDateString()}</div>
            <div class="resume-info">Type: ${resume.fileType}</div>
            ${resume.skills ? `<div class="resume-info">Skills: ${resume.skills.substring(0, 100)}...</div>` : ''}
            <div class="resume-actions">
                <button onclick="viewResume(${resume.id})" class="btn btn-secondary">
                    <i class="fas fa-eye"></i> View
                </button>
                <button onclick="deleteResume(${resume.id})" class="btn btn-logout">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </div>
        `;
        container.appendChild(card);
    });
}

async function deleteResume(resumeId) {
    if (!confirm('Are you sure you want to delete this resume?')) return;
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/resumes/${resumeId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            showMessage('Resume deleted successfully', 'success');
            loadMyResumes();
        } else {
            showMessage('Failed to delete resume', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

// Job Application
async function applyForJob(jobId) {
    const resumes = await getMyResumes();
    
    if (resumes.length === 0) {
        showMessage('Please upload a resume first', 'warning');
        showResumes();
        return;
    }
    
    if (resumes.length === 1) {
        // Apply with the only available resume
        await submitApplication(jobId, resumes[0].id);
    } else {
        // Let user choose resume
        const resumeId = prompt('Enter resume ID to apply with:\n' + 
            resumes.map(r => `${r.id}: ${r.fileName}`).join('\n'));
        
        if (resumeId) {
            await submitApplication(jobId, parseInt(resumeId));
        }
    }
}

async function submitApplication(jobId, resumeId) {
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/jobs/${jobId}/apply?resumeId=${resumeId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Application submitted successfully!', 'success');
            showMessage(`Match Score: ${data.matchPercentage}%`, 'success');
            loadMyApplications();
        } else {
            showMessage(data.error || 'Failed to submit application', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function getMyResumes() {
    try {
        const response = await fetch(`${API_BASE}/resumes/my`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        return await response.json();
    } catch (error) {
        return [];
    }
}

async function loadMyApplications() {
    try {
        const response = await fetch(`${API_BASE}/applications/my`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        const applications = await response.json();
        
        if (response.ok) {
            displayApplications(applications);
        }
    } catch (error) {
        console.error('Failed to load applications:', error);
    }
}

function displayApplications(applications) {
    const container = document.getElementById('applicationsContainer');
    container.innerHTML = '';
    
    if (applications.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">No applications yet</p>';
        return;
    }
    
    applications.forEach(app => {
        const card = document.createElement('div');
        card.className = 'application-card fade-in';
        card.innerHTML = `
            <div class="application-header">
                <div>
                    <div class="application-job-title">${app.job.title}</div>
                    <div class="text-muted">${app.job.department ? app.job.department.name : ''}</div>
                </div>
                <div class="application-status status-${app.status.toLowerCase().replace('_', '-')}">
                    ${app.status.replace('_', ' ')}
                </div>
            </div>
            <div class="application-metrics">
                <div class="metric">
                    <div class="metric-value">${app.matchPercentage || 0}%</div>
                    <div class="metric-label">Match Score</div>
                </div>
                <div class="metric">
                    <div class="metric-value">${app.weightedScore || 0}</div>
                    <div class="metric-label">Weighted Score</div>
                </div>
            </div>
            ${app.skillGapAnalysis ? 
                `<div class="skill-gap-analysis">
                    <strong>Skill Analysis:</strong> ${app.skillGapAnalysis}
                </div>` : ''
            }
            <div class="text-muted">
                Applied: ${new Date(app.applicationDate).toLocaleDateString()}
            </div>
        `;
        container.appendChild(card);
    });
}

// Admin Functions
async function loadAdminDashboard() {
    try {
        const response = await fetch(`${API_BASE}/admin/dashboard`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        const stats = await response.json();
        
        if (response.ok) {
            displayAdminStats(stats);
        }
    } catch (error) {
        console.error('Failed to load admin dashboard:', error);
    }
}

function displayAdminStats(stats) {
    document.getElementById('totalUsers').textContent = stats.totalUsers || 0;
    document.getElementById('totalJobs').textContent = stats.totalJobs || 0;
    document.getElementById('totalDepartments').textContent = stats.totalDepartments || 0;
    document.getElementById('totalApplications').textContent = stats.totalApplications || 0;
}

// Section Navigation
function showJobs() {
    hideAllSections();
    document.getElementById('jobsSection').style.display = 'block';
}

function showResumes() {
    hideAllSections();
    document.getElementById('resumesSection').style.display = 'block';
    loadMyResumes();
}

function showApplications() {
    hideAllSections();
    document.getElementById('applicationsSection').style.display = 'block';
    loadMyApplications();
}

function showAdminPanel() {
    hideAllSections();
    document.getElementById('adminSection').style.display = 'block';
    loadAdminDashboard();
}

function hideAllSections() {
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.style.display = 'none';
    });
}

// Utility Functions
function showLoading() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loadingOverlay').style.display = 'none';
}

function showMessage(message, type = 'info') {
    const toast = document.getElementById('messageToast');
    const messageText = document.getElementById('messageText');
    
    messageText.textContent = message;
    toast.className = `message-toast ${type}`;
    toast.style.display = 'flex';
    
    setTimeout(() => {
        hideMessage();
    }, 5000);
}

function hideMessage() {
    document.getElementById('messageToast').style.display = 'none';
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Placeholder functions for features not yet implemented
function viewJobDetails(jobId) {
    showMessage('Job details feature coming soon!', 'info');
}

function viewResume(resumeId) {
    showMessage('Resume viewer coming soon!', 'info');
}

function showJobPostForm() {
    showMessage('Job posting form coming soon!', 'info');
}

function showDepartmentForm() {
    showMessage('Department management coming soon!', 'info');
}

function showUsersList() {
    showMessage('User management coming soon!', 'info');
}
