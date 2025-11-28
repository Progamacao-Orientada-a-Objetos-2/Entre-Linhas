document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu({ onDelete: handleDeleteAccount });
    bindLinks();
    await populateServices('profile-service', { placeholder: 'Selecione', valueKey: 'id' });
    await loadMyProfile(me.id);
    bindSubmit();
});

let currentProfile;

function bindLinks() {
    document.querySelectorAll('[data-link]')?.forEach(btn => {
        btn.addEventListener('click', () => {
            const target = btn.getAttribute('data-link');
            if (target) window.location.href = target;
        });
    });
}

async function loadMyProfile(id) {
    try {
        const profile = await apiFetch(`/users/${id}`);
        currentProfile = profile;
        document.getElementById('profile-name').value = profile.fullName || '';
        document.getElementById('profile-email').value = profile.email || '';
        document.getElementById('profile-phone').value = profile.phone || '';
        document.getElementById('profile-state').value = profile.state || '';
        document.getElementById('profile-city').value = profile.city || '';
        document.getElementById('profile-desc').value = profile.description || '';
        document.getElementById('profile-availability').checked = !!profile.availability;
        document.getElementById('profile-service').value = profile.mainServiceId || '';
        renderSpecific(profile);
    } catch (err) {
        showFeedback(`Erro ao carregar perfil: ${err.message}`, 'error');
    }
}

function renderSpecific(profile) {
    const container = document.getElementById('specific-fields');
    container.innerHTML = '';
    if (profile.userType === 'FACCIONISTA') {
        container.innerHTML = `
            <div>
                <label class="label" for="profile-cpf">CPF</label>
                <input type="text" id="profile-cpf" value="${profile.cpf || ''}" required>
            </div>
            <div>
                <label class="label" for="profile-birth">Data de nascimento</label>
                <input type="date" id="profile-birth" value="${profile.birthDate || ''}" required>
            </div>`;
    }
    if (profile.userType === 'COMPANY' || profile.userType === 'FACTION') {
        container.innerHTML += `
            <div>
                <label class="label" for="profile-cnpj">CNPJ</label>
                <input type="text" id="profile-cnpj" value="${profile.cnpj || ''}" required>
            </div>`;
    }
    if (profile.userType === 'COMPANY') {
        container.innerHTML += `
            <div>
                <label class="label" for="profile-state-registration">Inscrição estadual</label>
                <input type="text" id="profile-state-registration" value="${profile.stateRegistration || ''}" required>
            </div>`;
    }
}

function buildPayload() {
    const payload = {
        fullName: document.getElementById('profile-name').value,
        email: document.getElementById('profile-email').value,
        phone: document.getElementById('profile-phone').value,
        state: document.getElementById('profile-state').value,
        city: document.getElementById('profile-city').value,
        description: document.getElementById('profile-desc').value,
        mainServiceId: document.getElementById('profile-service').value ? Number(document.getElementById('profile-service').value) : null,
        availability: document.getElementById('profile-availability').checked,
    };

    if (currentProfile.userType === 'FACCIONISTA') {
        payload.cpf = document.getElementById('profile-cpf').value;
        payload.birthDate = document.getElementById('profile-birth').value;
    }
    if (currentProfile.userType === 'COMPANY' || currentProfile.userType === 'FACTION') {
        payload.cnpj = document.getElementById('profile-cnpj').value;
    }
    if (currentProfile.userType === 'COMPANY') {
        payload.stateRegistration = document.getElementById('profile-state-registration').value;
    }
    return payload;
}

function bindSubmit() {
    const form = document.getElementById('profile-form');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = buildPayload();
        try {
            const updated = await apiFetch('/users/me', {
                method: 'PUT',
                body: JSON.stringify(payload),
            });
            currentProfile = updated;
            localStorage.setItem('entrelinhasUser', JSON.stringify({
                id: updated.id,
                fullName: updated.fullName,
                userType: updated.userType,
                email: updated.email,
            }));
            showFeedback('Perfil atualizado com sucesso.', 'success');
            await loadMyProfile(updated.id);
        } catch (err) {
            showFeedback(`Erro ao salvar: ${err.message}`, 'error');
        }
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
