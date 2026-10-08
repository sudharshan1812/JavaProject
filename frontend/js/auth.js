function requireAuth() {
    if (!localStorage.getItem('token') || !localStorage.getItem('username')) {
        location.href = 'login.html';
    }
}

function login(username, password) {
    return api('/auth/login', { method: 'POST', body: { username, password } });
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = $('#login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = $('#login-btn');
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner"></span> Signing in...';
            $('#error-msg').textContent = '';
            try {
                const res = await login($('#username').value.trim(), $('#password').value);
                localStorage.setItem('token', res.token);
                localStorage.setItem('username', res.username);
                localStorage.setItem('role', res.role);
                location.href = 'dashboard.html';
            } catch (err) {
                $('#error-msg').textContent = err.message;
                btn.disabled = false;
                btn.textContent = 'Sign In';
            }
        });
    }

    const logoutBtn = $('#logout-btn');
    if (logoutBtn) logoutBtn.addEventListener('click', logout);
});