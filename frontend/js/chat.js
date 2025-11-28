document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu();
    bindOpenChat();
    bindSendMessage();
    await loadChats();
    const otherId = new URLSearchParams(window.location.search).get('userId');
    if (otherId) {
        await openChatWith(otherId);
    }
});

async function loadChats() {
    try {
        const chats = await apiFetch('/chats');
        const list = document.getElementById('chat-list');
        list.innerHTML = '';
        chats.forEach(c => {
            const item = document.createElement('button');
            item.className = 'btn-secondary';
            item.textContent = `${c.otherUserName} (${formatUserType(c.otherUserType)})`;
            item.addEventListener('click', () => selectChat(c.id));
            list.appendChild(item);
        });
    } catch (err) {
        showFeedback(`Erro ao carregar chats: ${err.message}`, 'error');
    }
}

async function openChatWith(otherId) {
    try {
        const chat = await apiFetch(`/chats?otherUserId=${otherId}`, { method: 'POST' });
        await loadChats();
        await selectChat(chat.id);
    } catch (err) {
        showFeedback(`Não foi possível abrir o chat: ${err.message}`, 'error');
    }
}

function bindOpenChat() {
    const form = document.getElementById('open-chat-form');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const otherId = document.getElementById('chat-user-id').value;
        if (!otherId) return;
        await openChatWith(otherId);
    });
}

async function selectChat(chatId) {
    document.getElementById('chat-id').value = chatId;
    await loadMessages(chatId);
}

async function loadMessages(chatId) {
    try {
        const messages = await apiFetch(`/chats/${chatId}/messages`);
        const box = document.getElementById('message-box');
        box.innerHTML = '';
        messages.forEach(m => {
            const bubble = document.createElement('div');
            bubble.className = 'card';
            bubble.innerHTML = `<strong>${m.senderName}:</strong> ${m.content}`;
            box.appendChild(bubble);
        });
    } catch (err) {
        showFeedback(`Erro ao carregar mensagens: ${err.message}`, 'error');
    }
}

function bindSendMessage() {
    const form = document.getElementById('message-form');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const chatId = document.getElementById('chat-id').value;
        const content = document.getElementById('message-content').value;
        if (!chatId || !content) return;
        try {
            await apiFetch(`/chats/${chatId}/messages`, {
                method: 'POST',
                body: JSON.stringify({ content })
            });
            document.getElementById('message-content').value = '';
            await loadMessages(chatId);
        } catch (err) {
            showFeedback(`Erro ao enviar mensagem: ${err.message}`, 'error');
        }
    });
}
