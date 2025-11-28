document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu({ onDelete: handleDeleteAccount });
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) {
        showFeedback('Perfil não encontrado.', 'error');
        return;
    }
    await loadProfile(Number(id), me);
});

async function loadProfile(id, me) {
    try {
        const profile = await apiFetch(`/users/${id}`);
        renderProfile(profile, me);
    } catch (err) {
        showFeedback(`Erro ao carregar perfil: ${err.message}`, 'error');
    }
}

function canRequest(me, target) {
    return me.userType === 'COMPANY' && target.userType === 'FACTION';
}

function canChat(me, target) {
    return (me.userType === 'COMPANY' && target.userType === 'FACTION') ||
        (me.userType === 'FACTION' && target.userType === 'FACCIONISTA');
}

function renderProfile(profile, me) {
    document.getElementById('profile-name').textContent = profile.fullName;
    document.getElementById('profile-type').textContent = formatUserType(profile.userType);
    document.getElementById('profile-location').textContent = `${profile.city || '-'} / ${profile.state || '-'}`;
    document.getElementById('profile-service').textContent = profile.mainService || '-';
    document.getElementById('profile-desc').textContent = profile.description || 'Sem descrição ainda.';
    document.getElementById('profile-rating').textContent = `${profile.averageRating ?? '-'} (${profile.reviewCount || 0} avaliações)`;
    document.getElementById('profile-services').textContent = profile.serviceCount || 0;
    document.getElementById('profile-availability').textContent = profile.availability ? 'Disponível' : 'Indisponível';

    const specific = document.getElementById('profile-specific');
    specific.innerHTML = '';
    if (profile.cpf) specific.innerHTML += `<p><strong>CPF:</strong> ${profile.cpf}</p>`;
    if (profile.cnpj) specific.innerHTML += `<p><strong>CNPJ:</strong> ${profile.cnpj}</p>`;
    if (profile.stateRegistration) specific.innerHTML += `<p><strong>Inscrição estadual:</strong> ${profile.stateRegistration}</p>`;
    if (profile.birthDate) specific.innerHTML += `<p><strong>Nascimento:</strong> ${profile.birthDate}</p>`;

    const actions = document.getElementById('profile-actions');
    actions.innerHTML = '';
    const chatBtn = document.createElement('button');
    chatBtn.className = 'btn-secondary';
    chatBtn.textContent = 'Chat';
    chatBtn.disabled = !canChat(me, profile);
    chatBtn.title = chatBtn.disabled ? 'Chat permitido apenas para pares Empresa↔Facção ou Facção↔Faccionista' : '';
    chatBtn.addEventListener('click', () => goToChat(profile.id));
    actions.appendChild(chatBtn);

    const requestBtn = document.createElement('button');
    requestBtn.className = 'btn-primary';
    requestBtn.textContent = 'Solicitar serviço';
    requestBtn.disabled = !canRequest(me, profile);
    requestBtn.title = requestBtn.disabled ? 'Somente empresas podem solicitar para facções.' : '';
    requestBtn.addEventListener('click', () => goToRequest(profile.id));
    actions.appendChild(requestBtn);

    const reviewBtn = document.createElement('button');
    reviewBtn.className = 'btn-secondary';
    reviewBtn.textContent = 'Avaliar';
    reviewBtn.addEventListener('click', () => goToReview(profile.id));
    actions.appendChild(reviewBtn);
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
