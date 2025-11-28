const API_BASE = window.ENTRELINHAS_API_BASE
    || 'http://localhost:8080/api';

function getUserTypeFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('type');
}

async function parseError(resp) {
    try {
        const body = await resp.json();
        if (body?.message) return body.message;
        if (body?.errors) return Array.isArray(body.errors) ? body.errors.join(', ') : body.errors;
    } catch (e) {
        // ignore parse errors
    }
    return resp.statusText || 'Erro inesperado';
}

function mapUniquenessMessage(parsedMessage) {
    if (!parsedMessage) return null;
    const lower = parsedMessage.toLowerCase();
    if (lower.includes('email')) return 'Já existe um usuário com este e-mail.';
    if (lower.includes('phone') || lower.includes('telefone')) return 'Telefone já cadastrado.';
    if (lower.includes('cpf')) return 'Já existe um usuário com este CPF.';
    if (lower.includes('cnpj')) return 'CNPJ já cadastrado no sistema.';
    if (lower.includes('state_registration') || lower.includes('inscricao')) return 'Inscrição estadual já cadastrada.';
    return null;
}

function setFeedback(feedbackEl, message, isError = false) {
    if (!feedbackEl) return;
    feedbackEl.textContent = message;
    feedbackEl.className = isError ? 'feedback error' : 'feedback success';
}

async function login(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('senha').value;
    const feedback = document.getElementById('feedback');
    setFeedback(feedback, 'Autenticando...');
    try {
        const resp = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!resp.ok) {
            const parsed = await parseError(resp);
            const mapped = {
                400: parsed || 'Dados inválidos',
                401: 'E-mail ou senha incorretos',
                403: 'E-mail ou senha incorretos',
                404: 'Serviço temporariamente indisponível',
            }[resp.status] || (resp.status >= 500 ? (parsed || 'Erro interno, tente novamente mais tarde') : 'E-mail ou senha incorretos');
            throw new Error(mapped);
        }
        const data = await resp.json();
        localStorage.setItem('entrelinhasToken', data.accessToken);
        localStorage.setItem('entrelinhasUser', JSON.stringify(data.user));
        window.location.href = 'faccionista-page.html';
    } catch (err) {
        const message = err.name === 'TypeError'
            ? 'Sem conexão com o servidor. Verifique se a API está ativa.'
            : err.message;
        setFeedback(feedback, `Falha no login: ${message}`, true);
    }
}

function resolveUserType() {
    const form = document.getElementById('register-form');
    return form?.dataset?.userType || getUserTypeFromUrl();
}

function buildRegistrationPayload(userType) {
    const pass = document.getElementById('senha').value;
    const confirm = document.getElementById('confirmacao-senha').value;
    const feedback = document.getElementById('feedback');

    if (pass !== confirm) {
        setFeedback(feedback, 'As senhas não conferem. Revise os dois campos ou visualize para confirmar.', true);
        return null;
    }

    const selectedService = document.getElementById('servico-id');
    const mainServiceId = selectedService && selectedService.value
        ? Number(selectedService.value)
        : null;

    if (!mainServiceId) {
        setFeedback(feedback, 'Selecione um serviço principal antes de continuar.', true);
        return null;
    }

    const basePayload = {
        userType,
        fullName: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        password: pass,
        phone: document.getElementById('telefone').value,
        state: document.getElementById('estado').value,
        city: document.getElementById('cidade').value,
        mainServiceId,
    };

    if (userType === 'FACCIONISTA') {
        if (!document.getElementById('cpf').value || !document.getElementById('data-nascimento').value) {
            setFeedback(feedback, 'CPF e data de nascimento são obrigatórios para faccionista', true);
            return null;
        }
        return {
            ...basePayload,
            cpf: document.getElementById('cpf').value,
            birthDate: document.getElementById('data-nascimento').value,
            cnpj: null,
            stateRegistration: null,
        };
    }

    if (userType === 'FACTION') {
        if (!document.getElementById('cnpj').value) {
            setFeedback(feedback, 'CNPJ é obrigatório para facção', true);
            return null;
        }
        return {
            ...basePayload,
            cnpj: document.getElementById('cnpj').value,
            stateRegistration: null,
            cpf: null,
            birthDate: null,
        };
    }

    if (userType === 'COMPANY') {
        if (!document.getElementById('cnpj').value || !document.getElementById('inscricao-estadual').value) {
            setFeedback(feedback, 'CNPJ e inscrição estadual são obrigatórios para empresa', true);
            return null;
        }
        return {
            ...basePayload,
            cnpj: document.getElementById('cnpj').value,
            stateRegistration: document.getElementById('inscricao-estadual').value,
            cpf: null,
            birthDate: null,
        };
    }

    setFeedback(feedback, 'Tipo de usuário inválido', true);
    return null;
}

async function register(event) {
    event.preventDefault();
    const feedback = document.getElementById('feedback');
    const userType = resolveUserType();
    if (!userType) {
        setFeedback(feedback, 'Tipo de cadastro ausente. Volte e selecione um perfil.', true);
        return;
    }

    const payload = buildRegistrationPayload(userType);
    if (!payload) return;

    setFeedback(feedback, 'Registrando...');
    try {
        const resp = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!resp.ok) {
            const parsed = await parseError(resp);
            const uniquenessMessage = mapUniquenessMessage(parsed);

            const mapped = uniquenessMessage || {
                400: parsed || 'Dados inválidos',
                404: 'Endpoint de cadastro indisponível',
                409: parsed || 'E-mail já cadastrado',
            }[resp.status] || (resp.status >= 500 ? (parsed || 'Erro interno, tente novamente mais tarde') : (parsed || 'Falha ao registrar'));
            throw new Error(mapped);
        }
        await resp.json();
        localStorage.setItem('entrelinhasSignupSuccess', 'Cadastro realizado! Faça login para continuar.');
        window.location.href = 'index.html';
    } catch (err) {
        const message = err.name === 'TypeError'
            ? 'Sem conexão com o servidor. Verifique se a API está ativa.'
            : err.message;
        setFeedback(feedback, `Erro ao cadastrar: ${message}`, true);
    }
}

function bindLogin() {
    const form = document.getElementById('login-form');
    if (form) form.addEventListener('submit', login);
}

function bindRegister() {
    const form = document.getElementById('register-form');
    if (form) form.addEventListener('submit', register);
}

function bindPasswordToggle() {
    document.querySelectorAll('.toggle-senha').forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.dataset.target;
            const input = document.getElementById(targetId);
            if (!input) return;
            input.type = input.type === 'password' ? 'text' : 'password';
            btn.textContent = input.type === 'password' ? '👁' : '🙈';
        });
    });
}

function showSignupSuccess() {
    const feedback = document.getElementById('feedback');
    const saved = localStorage.getItem('entrelinhasSignupSuccess');
    if (saved && feedback) {
        setFeedback(feedback, saved, false);
        localStorage.removeItem('entrelinhasSignupSuccess');
    }
}

async function carregarServicos() {
    const select = document.getElementById('servico-id');
    if (!select) return;

    select.innerHTML = '<option value="" disabled selected>Selecione</option>';

    try {
        const resp = await fetch(`${API_BASE}/services`);
        if (!resp.ok) throw new Error('Não foi possível carregar serviços');
        const data = await resp.json();

        data.forEach(svc => {
            const option = document.createElement('option');
            option.value = svc.id;
            option.textContent = svc.name;
            select.appendChild(option);
        });
        if (data.length === 0) {
            setFeedback(document.getElementById('feedback'), 'Nenhum serviço disponível. Contate o administrador para cadastrar serviços.', true);
            select.value = '';
            const submitBtn = document.querySelector('#register-form button[type="submit"]');
            if (submitBtn) submitBtn.disabled = true;
        } else {
            const submitBtn = document.querySelector('#register-form button[type="submit"]');
            if (submitBtn) submitBtn.disabled = false;
            select.value = '';
        }
    } catch (err) {
        console.error('Erro ao carregar serviços:', err);
        setFeedback(document.getElementById('feedback'), 'Não foi possível carregar serviços. Verifique sua conexão.', true);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    bindLogin();
    bindRegister();
    bindPasswordToggle();
    showSignupSuccess();
    carregarServicos();
});
