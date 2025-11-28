// ==========================================
// 1. LÓGICA DE ESTADOS E CIDADES (IBGE)
// ==========================================
document.addEventListener("DOMContentLoaded", function () {
    const selectEstado = document.getElementById("estado");
    const inputCidade = document.getElementById("cidade");
    const listaCidades = document.getElementById("lista-cidades");

    // Só roda se existir o campo de estado na tela (evita erro na dashboard)
    if (selectEstado) {
        // Carregar estados
        fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome")
            .then((response) => response.json())
            .then((estados) => {
                selectEstado.innerHTML = '<option value="" disabled selected>Selecione o estado</option>';
                estados.forEach((estado) => {
                    const option = document.createElement("option");
                    option.value = estado.sigla;
                    option.textContent = estado.nome;
                    selectEstado.appendChild(option);
                });
            })
            .catch((error) => console.error("Erro ao carregar estados:", error));

        // Carregar cidades ao mudar estado
        selectEstado.addEventListener("change", function () {
            const estadoSigla = this.value;
            listaCidades.innerHTML = ""; // Limpa lista

            if (estadoSigla) {
                fetch(`https://servicodados.ibge.gov.br/api/v1/localidades/estados/${estadoSigla}/municipios`)
                    .then((response) => response.json())
                    .then((cidades) => {
                        cidades.forEach((cidade) => {
                            const option = document.createElement("option");
                            option.value = cidade.nome;
                            listaCidades.appendChild(option);
                        });
                    })
                    .catch((error) => console.error("Erro ao carregar cidades:", error));
            }
        });
    }
});

// ==========================================
// 2. LÓGICA DO CHAT (Por Facção)
// ==========================================
document.addEventListener("DOMContentLoaded", function () {
    const chatWidget = document.getElementById("chat-widget");
    const closeChatBtn = document.getElementById("close-chat");
    const chatForm = document.getElementById("chat-form");
    const chatInput = document.getElementById("chat-input");
    const chatMessages = document.getElementById("chat-messages");
    const chatTitleText = document.getElementById("chat-context-title");

    // Seleciona TODOS os botões de chat dentro dos cards
    const chatButtons = document.querySelectorAll(".btn-chat-card");

    // Adiciona evento de clique para cada botão de facção
    chatButtons.forEach(button => {
        button.addEventListener("click", function() {
            // Pega o nome da facção do atributo data-faccao
            const nomeFaccao = this.getAttribute("data-faccao");

            // Atualiza o título do chat
            chatTitleText.textContent = nomeFaccao;

            // Limpa mensagens antigas (opcional) e mostra saudação personalizada
            chatMessages.innerHTML = "";
            addMessage(`Olá! Você está falando com <strong>${nomeFaccao}</strong>. Como podemos ajudar?`, "system");

            // Abre o chat
            chatWidget.classList.remove("hidden");
            chatWidget.style.display = "flex";
            setTimeout(() => chatInput.focus(), 100);
        });
    });

    // Fechar Chat
    if (closeChatBtn) {
        closeChatBtn.addEventListener("click", () => {
            chatWidget.classList.add("hidden");
            setTimeout(() => chatWidget.style.display = "none", 300);
        });
    }

    // Enviar Mensagem
    if (chatForm) {
        chatForm.addEventListener("submit", (e) => {
            e.preventDefault();
            const text = chatInput.value.trim();

            if (text !== "") {
                addMessage(text, "user");
                chatInput.value = "";

                // Resposta automática simulada
                setTimeout(() => {
                    addMessage("Mensagem recebida pela facção!", "system");
                }, 1000);
            }
        });
    }

    // Função auxiliar
    function addMessage(text, type) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("message", type);

        const now = new Date();
        const timeString = now.getHours() + ":" + String(now.getMinutes()).padStart(2, '0');

        messageDiv.innerHTML = `
            <p>${text}</p>
            <span class="time">${timeString}</span>
        `;
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
});