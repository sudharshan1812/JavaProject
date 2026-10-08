const API_BASE = 'http://localhost:8081/api';

const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => [...document.querySelectorAll(sel)];

async function api(path, options = {}) {
    const token = localStorage.getItem('token');
    const res = await fetch(API_BASE + path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: 'Bearer ' + token } : {}),
            ...(options.headers || {}),
        },
        body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    });
    if (res.status === 401 && !path.startsWith('/auth')) {
        location.href = 'login.html';
        throw new Error('Unauthorized');
    }
    const data = res.status === 204 ? null : await res.json().catch(() => null);
    if (!res.ok) throw new Error((data && data.error) || 'Request failed');
    return data;
}

function inr(value) {
    return '\u20B9' + Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

function fmtTime(iso) {
    if (!iso) return '-';
    return new Date(iso).toLocaleString('en-IN', {
        day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit',
    });
}

function toast(message, type = 'ok', ms = 2600) {
    const el = $('#toast');
    el.textContent = message;
    el.className = 'toast show ' + (type === 'err' ? 'err' : 'ok');
    clearTimeout(toast._t);
    toast._t = setTimeout(() => el.classList.remove('show'), ms);
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (c) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
    }[c]));
}

function badge(status) {
    const key = String(status || '').toLowerCase();
    const cls = ['paid', 'success', 'active'].includes(key) ? 'paid'
        : ['pending'].includes(key) ? 'pending'
        : ['failed', 'inactive'].includes(key) ? 'failed' : 'pending';
    return `<span class="badge ${cls}">${escapeHtml(status)}</span>`;
}