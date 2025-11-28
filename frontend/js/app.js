const API_BASE = localStorage.getItem('entrelinhasApi') || 'http://localhost:8080/api';

function authHeaders() {
    const token = localStorage.getItem('entrelinhasToken');
    return token ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

function setStatus(message, type = 'ok') {
    const el = document.getElementById('status');
    if (!el) return;
    el.textContent = message;
    el.className = `alert status-${type}`;
}

async function apiFetch(path, options = {}) {
    const resp = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: { ...authHeaders(), ...(options.headers || {}) },
    });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || resp.statusText);
    }
    if (resp.status === 204) return null;
    return resp.json();
}

function renderUserSummary(user) {
    const summary = document.getElementById('user-summary');
    if (!summary || !user) return;
    summary.textContent = `${user.fullName} (${user.userType}) - ${user.email}`;
}

function requireAuth() {
    const token = localStorage.getItem('entrelinhasToken');
    const user = JSON.parse(localStorage.getItem('entrelinhasUser') || 'null');
    if (!token || !user) {
        window.location.href = 'index.html';
        return null;
    }
    renderUserSummary(user);
    document.getElementById('welcome-name').textContent = user.fullName;
    document.getElementById('welcome-role').textContent = user.userType;
    return user;
}

function logout() {
    localStorage.removeItem('entrelinhasToken');
    localStorage.removeItem('entrelinhasUser');
    window.location.href = 'index.html';
}

async function loadProfile(userId) {
    try {
        const profile = await apiFetch(`/users/${userId}`);
        const container = document.getElementById('profile-details');
        if (!container) return;
        container.innerHTML = `
            <p><strong>Nome:</strong> ${profile.fullName}</p>
            <p><strong>Tipo:</strong> ${profile.userType}</p>
            <p><strong>Email:</strong> ${profile.email}</p>
            <p><strong>Telefone:</strong> ${profile.phone || '-'} </p>
            <p><strong>Local:</strong> ${profile.city || '-'} / ${profile.state || '-'}</p>
            <p><strong>Serviço principal:</strong> ${profile.mainService || '-'} </p>
            <p><strong>Disponibilidade:</strong> ${profile.availability ? 'Disponível' : 'Indisponível'}</p>
            <p><strong>Média avaliações:</strong> ${profile.averageRating ?? '-'} (${profile.reviewCount || 0} avaliações)</p>
            <p><strong>Serviços concluídos:</strong> ${profile.serviceCount || 0}</p>
            <p><strong>Último acesso:</strong> ${profile.lastAccessAt || '-'}</p>
        `;
        const me = JSON.parse(localStorage.getItem('entrelinhasUser') || 'null');
        if (me && me.id === profile.id) {
            const availabilityToggle = document.getElementById('availability');
            if (availabilityToggle) availabilityToggle.checked = !!profile.availability;
        }
    } catch (err) {
        setStatus(`Erro ao carregar perfil: ${err.message}`, 'error');
    }
}

async function searchUsers() {
    const params = new URLSearchParams();
    ['name','state','city','mainService'].forEach(key => {
        const value = document.getElementById(`search-${key}`)?.value;
        if (value) params.append(key, value);
    });
    const type = document.getElementById('search-type')?.value;
    const availability = document.getElementById('search-availability')?.value;
    if (type) params.append('userType', type);
    if (availability) params.append('availability', availability);
    try {
        const users = await apiFetch(`/users?${params.toString()}`);
        const list = document.getElementById('search-results');
        list.innerHTML = '';
        users.forEach(u => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <header><strong>${u.fullName}</strong><small>${u.userType}</small></header>
                <div class="chips">
                    <span class="chip">${u.city || '-'} / ${u.state || '-'}</span>
                    <span class="chip">Serviço: ${u.mainService || '-'}</span>
                    <span class="chip">Avaliação: ${u.averageRating ?? '-'} (${u.reviewCount || 0})</span>
                    <span class="chip">Serviços: ${u.serviceCount || 0}</span>
                    <span class="chip">${u.availability ? 'Disponível' : 'Indisponível'}</span>
                </div>
                <p>${u.description || ''}</p>
                <div class="chips">
                    <button class="btn" data-action="profile" data-id="${u.id}">Ver perfil</button>
                    <button class="btn" data-action="chat" data-id="${u.id}">Abrir chat</button>
                    <button class="btn" data-action="request" data-id="${u.id}">Solicitar serviço</button>
                    <button class="btn" data-action="reviews" data-id="${u.id}">Ver avaliações</button>
                </div>
            `;
            list.appendChild(card);
        });
    } catch (err) {
        setStatus(`Erro na busca: ${err.message}`, 'error');
    }
}

async function updateAvailability(event) {
    event.preventDefault();
    const availability = document.getElementById('availability').checked;
    try {
        await apiFetch('/users/me/availability', {
            method: 'PATCH',
            body: JSON.stringify({ availability })
        });
        setStatus('Disponibilidade atualizada.');
    } catch (err) {
        setStatus(`Erro ao atualizar: ${err.message}`, 'error');
    }
}

async function createRequest(event, currentUser) {
    event.preventDefault();
    if (currentUser.userType !== 'COMPANY') {
        setStatus('Somente empresas podem criar solicitações.', 'warn');
        return;
    }
    const payload = {
        name: document.getElementById('req-name').value,
        description: document.getElementById('req-description').value,
        factionId: Number(document.getElementById('req-faction').value),
        serviceId: Number(document.getElementById('req-service').value),
    };
    try {
        await apiFetch('/requests', { method: 'POST', body: JSON.stringify(payload) });
        setStatus('Solicitação criada com sucesso.');
        await loadRequests();
    } catch (err) {
        setStatus(`Erro ao criar solicitação: ${err.message}`, 'error');
    }
}

async function loadRequests() {
    try {
        const requests = await apiFetch('/requests/mine');
        const list = document.getElementById('request-list');
        list.innerHTML = '';
        requests.forEach(r => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <header>
                    <strong>${r.name}</strong>
                    <small>${r.status}</small>
                </header>
                <p>${r.description || ''}</p>
                <div class="chips">
                    <span class="chip">Serviço ID: ${r.serviceId}</span>
                    <span class="chip">Empresa: ${r.companyId}</span>
                    <span class="chip">Facção: ${r.factionId}</span>
                </div>
                <div class="chips">
                    <button class="btn" data-action="accept" data-id="${r.id}">Aceitar</button>
                    <button class="btn" data-action="reject" data-id="${r.id}">Recusar</button>
                    <button class="btn" data-action="review" data-id="${r.id}">Avaliar</button>
                </div>
            `;
            list.appendChild(card);
        });
    } catch (err) {
        setStatus(`Erro ao carregar solicitações: ${err.message}`, 'error');
    }
}

async function handleRequestAction(action, id) {
    try {
        const endpoint = action === 'accept' ? 'accept' : 'reject';
        const updated = await apiFetch(`/requests/${id}/${endpoint}`, { method: 'POST' });
        setStatus(`Solicitação ${updated.status}.`);
        await loadRequests();
    } catch (err) {
        setStatus(`Erro na solicitação: ${err.message}`, 'error');
    }
}

async function openChat(otherUserId) {
    try {
        await apiFetch(`/chats?otherUserId=${otherUserId}`, { method: 'POST' });
        await loadChats();
    } catch (err) {
        setStatus(`Erro ao abrir chat: ${err.message}`, 'error');
    }
}

let selectedChatId = null;

async function loadChats() {
    try {
        const chats = await apiFetch('/chats');
        const list = document.getElementById('chat-list');
        list.innerHTML = '';
        chats.forEach(c => {
            const item = document.createElement('div');
            item.className = 'card';
            item.innerHTML = `
                <header>
                    <strong>Chat #${c.id}</strong>
                    <small>Com: ${c.otherUser.fullName} (${c.otherUser.userType})</small>
                </header>
                <div class="chips">
                    <button class="btn" data-action="open-chat" data-id="${c.id}">Abrir mensagens</button>
                </div>
            `;
            list.appendChild(item);
        });
    } catch (err) {
        setStatus(`Erro ao listar chats: ${err.message}`, 'error');
    }
}

async function loadMessages(chatId) {
    selectedChatId = chatId;
    try {
        const messages = await apiFetch(`/chats/${chatId}/messages`);
        const box = document.getElementById('message-box');
        box.innerHTML = '';
        const meId = JSON.parse(localStorage.getItem('entrelinhasUser')).id;
        messages.forEach(m => {
            const div = document.createElement('div');
            div.className = 'message' + (m.senderId === meId ? ' me' : '');
            div.innerHTML = `<strong>${m.senderName}</strong> <small>${m.sentAt}</small><br>${m.content}`;
            box.appendChild(div);
        });
    } catch (err) {
        setStatus(`Erro ao carregar mensagens: ${err.message}`, 'error');
    }
}

async function sendMessage(event) {
    event.preventDefault();
    if (!selectedChatId) {
        setStatus('Selecione um chat para enviar mensagens.', 'warn');
        return;
    }
    const content = document.getElementById('message-content').value;
    if (!content) return;
    try {
        await apiFetch(`/chats/${selectedChatId}/messages`, {
            method: 'POST',
            body: JSON.stringify({ content })
        });
        document.getElementById('message-content').value = '';
        await loadMessages(selectedChatId);
    } catch (err) {
        setStatus(`Erro ao enviar mensagem: ${err.message}`, 'error');
    }
}

async function submitReview(event) {
    event.preventDefault();
    const payload = {
        receiverId: Number(document.getElementById('review-receiver').value),
        serviceRequestId: Number(document.getElementById('review-request').value),
        rating: Number(document.getElementById('review-rating').value),
        comment: document.getElementById('review-comment').value,
    };
    try {
        await apiFetch('/reviews', { method: 'POST', body: JSON.stringify(payload) });
        setStatus('Avaliação registrada.');
    } catch (err) {
        setStatus(`Erro ao avaliar: ${err.message}`, 'error');
    }
}

async function loadReviewsFor(userId) {
    try {
        const reviews = await apiFetch(`/reviews/${userId}`);
        const list = document.getElementById('review-list');
        list.innerHTML = '';
        reviews.forEach(r => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <header><strong>${r.rating} estrelas</strong><small>De: ${r.senderName}</small></header>
                <p>${r.comment || ''}</p>
                <small>Ref. solicitação ${r.serviceRequestId}</small>
            `;
            list.appendChild(card);
        });
    } catch (err) {
        setStatus(`Erro ao carregar avaliações: ${err.message}`, 'error');
    }
}

function setupEventDelegates(currentUser) {
    document.getElementById('search-results').addEventListener('click', async (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;
        const id = btn.dataset.id;
        switch (btn.dataset.action) {
            case 'profile':
                loadProfile(id);
                break;
            case 'chat':
                openChat(id);
                break;
            case 'request':
                document.getElementById('req-faction').value = id;
                document.getElementById('req-name').focus();
                setStatus('Facção preenchida no formulário de solicitação.');
                break;
            case 'reviews':
                loadReviewsFor(id);
                break;
        }
    });

    document.getElementById('request-list').addEventListener('click', async (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;
        const id = btn.dataset.id;
        if (btn.dataset.action === 'review') {
            document.getElementById('review-request').value = id;
            setStatus('Solicitação vinculada ao formulário de avaliação.');
            return;
        }
        await handleRequestAction(btn.dataset.action, id);
    });

    document.getElementById('chat-list').addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-action="open-chat"]');
        if (!btn) return;
        loadMessages(btn.dataset.id);
    });
}

function initApp() {
    const user = requireAuth();
    if (!user) return;
    document.getElementById('logout').addEventListener('click', logout);
    document.getElementById('availability-form').addEventListener('submit', updateAvailability);
    document.getElementById('search-form').addEventListener('submit', (e) => { e.preventDefault(); searchUsers(); });
    document.getElementById('request-form').addEventListener('submit', (e) => createRequest(e, user));
    document.getElementById('review-form').addEventListener('submit', submitReview);
    document.getElementById('message-form').addEventListener('submit', sendMessage);
    document.getElementById('open-chat-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const target = document.getElementById('chat-user-id').value;
        if (target) openChat(target);
    });
    setupEventDelegates(user);
    searchUsers();
    loadRequests();
    loadChats();
    loadProfile(user.id);
}

document.addEventListener('DOMContentLoaded', initApp);
