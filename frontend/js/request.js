document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu();
    await populateServices('service-select');
    const params = new URLSearchParams(window.location.search);
    const factionId = params.get('factionId');
    if (factionId) {
        const targetField = document.getElementById('faction-id');
        if (targetField) targetField.value = factionId;
    }
    bindRequest();
});

function bindRequest() {
    const form = document.getElementById('request-form');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            name: document.getElementById('request-name').value,
            description: document.getElementById('request-description').value,
            factionId: Number(document.getElementById('faction-id').value),
            serviceId: Number(document.getElementById('service-select').value)
        };
        try {
            await apiFetch('/requests', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            showFeedback('Solicitação enviada com sucesso.', 'success');
            document.getElementById('request-form').reset();
        } catch (err) {
            showFeedback(`Erro ao enviar solicitação: ${err.message}`, 'error');
        }
    });
}
