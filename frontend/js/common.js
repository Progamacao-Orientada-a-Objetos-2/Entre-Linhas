const API_BASE = window.ENTRELINHAS_API_BASE || 'http://localhost:8080/api';

function currentUser() {
    try {
        return JSON.parse(localStorage.getItem('entrelinhasUser') || 'null');
    } catch (e) {
        return null;
    }
}

function authHeaders() {
    const token = localStorage.getItem('entrelinhasToken');
    return token ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

function requireAuth() {
    const user = currentUser();
    const token = localStorage.getItem('entrelinhasToken');
    if (!user || !token) {
        window.location.href = 'index.html';
        return null;
    }
    const userNameEl = document.getElementById('user-name');
    const userTypeEl = document.getElementById('user-type');
    if (userNameEl) userNameEl.textContent = user.fullName || user.email;
    if (userTypeEl) userTypeEl.textContent = formatUserType(user.userType);
    return user;
}

async function apiFetch(path, options = {}) {
    const resp = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: { ...authHeaders(), ...(options.headers || {}) },
    });
    if (resp.status === 401) {
        localStorage.removeItem('entrelinhasToken');
        localStorage.removeItem('entrelinhasUser');
        window.location.href = 'index.html';
        return Promise.reject(new Error('Sessão expirada'));
    }
    if (!resp.ok) {
        try {
            const data = await resp.json();
            if (data?.message) throw new Error(data.message);
            if (data?.errors) throw new Error(Array.isArray(data.errors) ? data.errors.join(', ') : data.errors);
        } catch (e) {
            const message = e instanceof Error ? e.message : null;
            throw new Error(message || resp.statusText || 'Erro inesperado');
        }
    }
    if (resp.status === 204) return null;
    return resp.json();
}

function showFeedback(message, type = 'info', id = 'feedback') {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = message;
    el.className = `feedback ${type}`;
}

function formatUserType(value) {
    if (!value) return '';
    return {
        COMPANY: 'Empresa',
        FACTION: 'Facção',
        FACCIONISTA: 'Faccionista'
    }[value] || value;
}

function setupMenu({ onDelete } = {}) {
    const toggle = document.getElementById('menu-toggle');
    const panel = document.getElementById('menu-panel');
    const overlay = document.getElementById('menu-overlay');
    const drawer = document.getElementById('menu-drawer');
    const user = currentUser();

    if (panel) {
        const close = () => {
            panel?.classList.remove('open');
            overlay?.classList.remove('visible');
        };
        const open = () => {
            panel?.classList.add('open');
            overlay?.classList.add('visible');
        };
        toggle?.addEventListener('click', () => {
            if (panel?.classList.contains('open')) {
                close();
            } else {
                open();
            }
        });
        overlay?.addEventListener('click', close);
        panel?.querySelectorAll('button').forEach(btn => {
            btn.addEventListener('click', () => {
                const action = btn.dataset.action;
                if (action === 'profile') {
                    window.location.href = 'my-profile.html';
                }
                if (action === 'logout') {
                    localStorage.removeItem('entrelinhasToken');
                    localStorage.removeItem('entrelinhasUser');
                    window.location.href = 'index.html';
                }
                if (action === 'delete') {
                    if (typeof onDelete === 'function') {
                        onDelete();
                    }
                }
                close();
            });
        });
    } else if (drawer) {
        const closeDrawer = () => {
            drawer.classList.remove('open');
            overlay?.classList.remove('visible');
        };
        toggle?.addEventListener('click', () => {
            drawer.classList.toggle('open');
            overlay?.classList.toggle('visible');
        });
        overlay?.addEventListener('click', closeDrawer);
        drawer.querySelector('#menu-logout')?.addEventListener('click', () => {
            localStorage.removeItem('entrelinhasToken');
            localStorage.removeItem('entrelinhasUser');
            window.location.href = 'index.html';
        });
    }
    if (user) {
        const nameEl = document.getElementById('user-name');
        const typeEl = document.getElementById('user-type');
        if (nameEl) nameEl.textContent = user.fullName || user.email;
        if (typeEl) typeEl.textContent = formatUserType(user.userType);
    }

    document.querySelectorAll('[data-link]').forEach(el => {
        el.addEventListener('click', (e) => {
            const target = e.currentTarget.getAttribute('data-link');
            if (target) window.location.href = target;
        });
    });
}

async function populateServices(selectId, { placeholder = 'Selecione', valueKey = 'id' } = {}) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = '';
    const placeholderOption = document.createElement('option');
    placeholderOption.value = '';
    placeholderOption.textContent = placeholder;
    select.appendChild(placeholderOption);
    try {
        const data = await apiFetch('/services');
        data.forEach(svc => {
            const option = document.createElement('option');
            option.value = svc[valueKey];
            option.textContent = svc.name;
            select.appendChild(option);
        });
    } catch (err) {
        showFeedback('Não foi possível carregar serviços. Verifique sua conexão ou cadastre serviços pelo administrador.', 'warn');
    }
}

function goToProfile(userId) {
    window.location.href = `profile.html?id=${userId}`;
}

function goToChat(userId) {
    window.location.href = `chat.html?userId=${userId}`;
}

function goToRequest(userId) {
    window.location.href = `request.html?factionId=${userId}`;
}

function goToReview(userId) {
    window.location.href = `review.html?receiverId=${userId}`;
}

