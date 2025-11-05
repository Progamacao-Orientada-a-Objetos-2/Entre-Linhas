// frontend/js/cidades-estados.js

document.addEventListener("DOMContentLoaded", function () {
    const selectEstado = document.getElementById("estado");
    const inputCidade = document.getElementById("cidade");
    const listaCidades = document.getElementById("lista-cidades");

    // 🔹 1) Carregar os estados
    fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome")
        .then((response) => response.json())
        .then((estados) => {
            selectEstado.innerHTML =
                '<option value="" disabled selected>Selecione o estado</option>';

            estados.forEach((estado) => {
                const option = document.createElement("option");
                option.value = estado.sigla;
                option.textContent = estado.nome;
                selectEstado.appendChild(option);
            });
        })
        .catch((error) => console.error("Erro ao carregar estados:", error));

    // 🔹 2) Quando o usuário escolher um estado, carregar as cidades
    selectEstado.addEventListener("change", function () {
        const estadoSigla = this.value;

        // Limpa lista anterior
        listaCidades.innerHTML = "";

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
});
