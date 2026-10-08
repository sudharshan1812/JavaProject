const NAV = [
    { href: 'dashboard.html', label: 'Dashboard', icon: '\u25A0' },
    { href: 'vehicles.html', label: 'Vehicles', icon: '\u25AE' },
    { href: 'register-vehicle.html', label: 'Register Vehicle', icon: '+' },
    { href: 'rfid.html', label: 'RFID Control', icon: '\u2666' },
    { href: 'toll-calculator.html', label: 'Toll Pricing', icon: '\u25C8' },
    { href: 'transactions.html', label: 'Transactions', icon: '\u2261' },
    { href: 'payments.html', label: 'Payments', icon: '\u20B9' },
    { href: 'reports.html', label: 'Reports', icon: '\u25C6' },
    { href: 'settings.html', label: 'Settings', icon: '\u2699' },
];

function titleFrom(page) {
    const match = NAV.find((n) => n.href === page);
    return match ? match.label : 'Smart Toll System';
}

function renderShell() {
    const page = location.pathname.split('/').pop() || 'dashboard.html';
    const sidebar = $('#sidebar-placeholder');
    if (!sidebar) return;

    sidebar.innerHTML = `
        <aside class="sidebar">
            <div class="brand">
                <div class="logo">
                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="#052e1f" stroke-width="2" stroke-linecap="round">
                        <path d="M7 12a5 5 0 0 1 10 0"/>
                        <path d="M4.5 9a8.5 8.5 0 0 1 15 0"/>
                        <path d="M2 6a12 12 0 0 1 20 0"/>
                        <circle cx="12" cy="14.5" r="1.5" fill="#052e1f" stroke="none"/>
                    </svg>
                </div>
                <div><h1>Smart Toll</h1><small>Control System</small></div>
            </div>
            <nav class="nav">
                ${NAV.map((n) => `
                    <a href="${n.href}" class="${n.href === page ? 'active' : ''}">
                        <span class="ico">${n.icon}</span> ${n.label}
                    </a>`).join('')}
            </nav>
            <div class="foot">RFID Toll Plaza<br>R420-001</div>
        </aside>`;

    $('#topbar-title').textContent = titleFrom(page);
    $('#user-name').textContent = localStorage.getItem('username') || 'admin';
    $('#user-avatar').textContent = (localStorage.getItem('username') || 'A').charAt(0).toUpperCase();
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('body.login-page')) return;
    requireAuth();
    renderShell();
});