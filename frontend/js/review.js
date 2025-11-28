document.addEventListener('DOMContentLoaded', async () => {
    const me = requireAuth();
    if (!me) return;
    setupMenu();
    const params = new URLSearchParams(window.location.search);
    const receiverId = params.get('receiverId');
    const requestId = params.get('requestId');
    if (receiverId) document.getElementById('review-receiver').value = receiverId;
    if (requestId) document.getElementById('review-request').value = requestId;
    bindReview();
});

function bindReview() {
    const form = document.getElementById('review-form');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            receiverId: Number(document.getElementById('review-receiver').value),
            serviceRequestId: Number(document.getElementById('review-request').value),
            rating: Number(document.getElementById('review-rating').value),
            comment: document.getElementById('review-comment').value
        };
        try {
            await apiFetch('/reviews', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            showFeedback('Avaliação registrada.', 'success');
            form.reset();
        } catch (err) {
            showFeedback(`Erro ao enviar avaliação: ${err.message}`, 'error');
        }
    });
}
