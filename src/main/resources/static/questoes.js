const LETRAS = ['A', 'B', 'C', 'D', 'E'];

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}

function montarCamposAlternativas() {
    document.getElementById('alternativas').innerHTML = LETRAS.map(letra => `
        <div class="alternativa">
            <input type="radio" name="correta" value="${letra}" title="Marcar ${letra} como correta">
            <strong>${letra})</strong>
            <input type="text" id="alt-${letra}" placeholder="Texto da alternativa ${letra}">
        </div>`).join('');
}

function atualizarTipo() {
    const multipla = document.getElementById('tipo').value === 'MULTIPLA';
    document.getElementById('bloco-alternativas').style.display = multipla ? '' : 'none';
}

function lerFormulario() {
    const tipo = document.getElementById('tipo').value;
    const correta = document.querySelector('input[name="correta"]:checked')?.value;
    const alternativas = tipo === 'TEXTO' ? [] : LETRAS
        .map(letra => ({ letra, texto: document.getElementById(`alt-${letra}`).value.trim(), correta: letra === correta }))
        .filter(a => a.texto !== '' || a.correta);
    return { enunciado: document.getElementById('enunciado').value, tipo, alternativas };
}

function mostrarMensagem(texto, erro) {
    const msg = document.getElementById('mensagem');
    msg.textContent = texto;
    msg.className = erro ? 'erro' : 'sucesso';
}

async function cadastrar(evento) {
    evento.preventDefault();
    const resp = await fetch('/api/questoes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(lerFormulario())
    });
    const corpo = await resp.json();
    if (!resp.ok) {
        mostrarMensagem(corpo.erro || 'Não foi possível cadastrar a questão.', true);
        return;
    }
    mostrarMensagem(`Questão ${corpo.id} cadastrada.`, false);
    document.getElementById('form-questao').reset();
    atualizarTipo();
    carregarQuestoes();
}

async function carregarQuestoes() {
    const questoes = await (await fetch('/api/questoes')).json();
    document.getElementById('lista-questoes').innerHTML = questoes.map(q => `
        <tr>
            <td>${q.id}</td>
            <td>${escapar(q.enunciado)}</td>
            <td>${q.tipo === 'MULTIPLA' ? 'Múltipla escolha' : 'Texto livre'}</td>
            <td>${q.alternativas.map(a =>
                `<div class="${a.correta ? 'correta' : ''}">${a.letra}) ${escapar(a.texto)}${a.correta ? ' ✔' : ''}</div>`
            ).join('') || '—'}</td>
        </tr>`).join('');
}

montarCamposAlternativas();
atualizarTipo();
document.getElementById('tipo').addEventListener('change', atualizarTipo);
document.getElementById('form-questao').addEventListener('submit', cadastrar);
carregarQuestoes();