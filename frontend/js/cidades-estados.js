// frontend/js/cidades-estados.js

// Espera o HTML carregar completamente antes de rodar o script
document.addEventListener("DOMContentLoaded", function() {

    const selectEstado = document.getElementById("estado");
    const selectCidade = document.getElementById("cidade");

    // 1️⃣ Carregar estados ao abrir a página
    fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome")
        .then(response => response.json())
        .then(estados => {
            // 🔹 Limpa o campo e reinsere a opção padrão
            selectEstado.innerHTML = '<option value="" disabled selected>Selecione o estado</option>';

            estados.forEach(estado => {
                const option = document.createElement("option");
                option.value = estado.sigla;
                option.textContent = estado.nome;
                selectEstado.appendChild(option);
            });
        })
        .catch(error => console.error("Erro ao carregar estados:", error));

    // 2️⃣ Quando o usuário escolher um estado, carregar as cidades
    selectEstado.addEventListener("change", function() {
        const estadoSigla = this.value;

        // Limpa cidades anteriores
        selectCidade.innerHTML = '<option value="" disabled selected>Selecione a cidade</option>';

        if (estadoSigla) {
            fetch(`https://servicodados.ibge.gov.br/api/v1/localidades/estados/${estadoSigla}/municipios`)
                .then(response => response.json())
                .then(cidades => {
                    cidades.forEach(cidade => {
                        const option = document.createElement("option");
                        option.value = cidade.nome;
                        option.textContent = cidade.nome;
                        selectCidade.appendChild(option);
                    });
                })
                .catch(error => console.error("Erro ao carregar cidades:", error));
        }
    });
});
