document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu({ onDelete: handleDeleteAccount });
    await populateFilters(me);
    bindFilters(me);
    await loadResults(me);
});

let debounceTimer;

async function populateFilters(me) {
    await populateServices('filter-service', { placeholder: 'Todos', valueKey: 'name' });
    const typeSelect = document.getElementById('filter-type');
    if (!typeSelect) return;
    const allowed = allowedTypes(me.userType);
    typeSelect.innerHTML = '';
    if (allowed.length === 1) {
        const option = document.createElement('option');
        option.value = allowed[0];
        option.textContent = formatUserType(allowed[0]);
        typeSelect.appendChild(option);
        typeSelect.disabled = true;
    } else {
        const allOption = document.createElement('option');
        allOption.value = '';
        allOption.textContent = 'Todos';
        typeSelect.appendChild(allOption);
        allowed.forEach(t => {
            const option = document.createElement('option');
            option.value = t;
            option.textContent = formatUserType(t);
            typeSelect.appendChild(option);
        });
    }
}

function bindFilters(me) {
    const searchInput = document.getElementById('search-name');
    ['filter-city', 'filter-state', 'filter-service', 'filter-type'].forEach(id => {
        document.getElementById(id)?.addEventListener('change', () => loadResults(me));
    });
    searchInput?.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => loadResults(me), 250);
    });
}

function allowedTypes(userType) {
    if (userType === 'FACTION') {
        return ['FACCIONISTA', 'COMPANY'];
    }
    return ['FACTION'];
}

async function loadResults(me) {
    const params = new URLSearchParams();
    const name = document.getElementById('search-name')?.value.trim();
    const city = document.getElementById('filter-city')?.value.trim();
    const state = document.getElementById('filter-state')?.value.trim();
    const service = document.getElementById('filter-service')?.value;
    const typeFilter = document.getElementById('filter-type')?.value;
    const allowed = allowedTypes(me.userType);

    if (name) params.append('name', name);
    if (city) params.append('city', city);
    if (state) params.append('state', state);
    if (service) params.append('mainService', service);

    if (allowed.length === 1) {
        params.append('userType', allowed[0]);
    } else if (typeFilter) {
        params.append('userType', typeFilter);
    }

    try {
        const users = await apiFetch(`/users?${params.toString()}`);
        const filtered = users
            .filter(u => allowed.includes(u.userType))
            .filter(u => u.id !== me.id);
        renderCards(filtered, me);
    } catch (err) {
        showFeedback(`Erro na busca: ${err.message}`, 'error');
    }
}

function renderCards(users, me) {
    const grid = document.getElementById('results');
    grid.innerHTML = '';
    if (!users || users.length === 0) {
        grid.innerHTML = '<p class="empty">Nenhum resultado encontrado.</p>';
        return;
    }

    users.forEach(user => {
        const card = document.createElement('article');
        card.className = 'card user-card';
        card.innerHTML = `
            <div class="card-top">
                <div>
                    <h3>${user.fullName}</h3>
                    <p class="text-muted">${formatUserType(user.userType)}</p>
                </div>
                <button class="icon-btn chat-icon" data-id="${user.id}" title="Abrir chat"><i class="fa-solid fa-comments"></i></button>
            </div>
            <div class="chips">
                <span class="chip">${user.city || '-'} / ${user.state || '-'}</span>
                <span class="chip">Serviço: ${user.mainService || '-'}</span>
                <span class="chip">${user.availability ? 'Disponível' : 'Indisponível'}</span>
            </div>
            <p class="description">${user.description || 'Sem descrição ainda.'}</p>
            <div class="card-actions">
                <button class="btn-primary" data-id="${user.id}" data-action="details">Ver detalhes</button>
            </div>
        `;
        card.querySelector('[data-action="details"]').addEventListener('click', (e) => {
            e.stopPropagation();
            goToProfile(user.id);
        });
        card.querySelector('.chat-icon').addEventListener('click', (e) => {
            e.stopPropagation();
            goToChat(user.id);
        });
        grid.appendChild(card);
    });
}

async function handleDeleteAccount() {
    const confirmed = window.confirm('Deseja realmente deletar sua conta? Esta ação não pode ser desfeita.');
    if (!confirmed) return;
    try {
        await apiFetch('/users/me', { method: 'DELETE' });
        localStorage.removeItem('entrelinhasToken');
        localStorage.removeItem('entrelinhasUser');
        window.location.href = 'index.html';
    } catch (err) {
        showFeedback(`Não foi possível deletar a conta: ${err.message}`, 'error');
    }
}
