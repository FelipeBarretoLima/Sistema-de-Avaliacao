// Tela de correção das questões de texto e nota total (RF6). Só conversa com a API — nenhum SQL aqui.

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

function numero(valor) {
    return Number(valor).toLocaleString('pt-BR');
}

function mostrarMensagem(texto, erro) {
    const msg = document.getElementById('mensagem');
    msg.textContent = texto;
    msg.className = erro ? 'erro' : 'sucesso';
}

async function carregarCorrecoes() {
    const pendentes = await (await fetch('/api/respostas/correcoes')).json();
    document.getElementById('lista-correcoes').innerHTML = pendentes.map(c => `
        <tr>
            <td>${escapar(c.avaliacao)}</td>
            <td>${escapar(c.aluno)}</td>
            <td>${escapar(c.enunciado)} <br><small>(vale ${numero(c.valor)})</small></td>
            <td class="texto-resposta">${escapar(c.textoResposta)}</td>
            <td><input type="number" class="nota" id="nota-${c.respostaId}" min="0" max="${c.valor}" step="0.1"></td>
            <td><button onclick="corrigir(${c.respostaId})">Salvar</button></td>
        </tr>`).join('') || '<tr><td colspan="6">Nenhuma resposta aguardando correção.</td></tr>';
}

async function corrigir(respostaId) {
    const campo = document.getElementById(`nota-${respostaId}`);
    const resp = await fetch(`/api/respostas/correcoes/${respostaId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nota: campo.value === '' ? null : Number(campo.value) })
    });
    const corpo = await resp.json();
    if (!resp.ok) {
        mostrarMensagem(corpo.erro, true);
        return;
    }
    mostrarMensagem(corpo.mensagem, false);
    carregarCorrecoes();
    carregarNotas();
}

async function carregarAvaliacoes() {
    const avaliacoes = await (await fetch('/api/avaliacoes')).json();
    document.getElementById('avaliacao').innerHTML = avaliacoes
        .map(a => `<option value="${a.id}">${escapar(a.titulo)}</option>`).join('');
    carregarNotas();
}

async function carregarNotas() {
    const avaliacaoId = document.getElementById('avaliacao').value;
    if (!avaliacaoId) return;
    const notas = await (await fetch(`/api/respostas/avaliacoes/${avaliacaoId}/notas`)).json();
    document.getElementById('lista-notas').innerHTML = notas.map(n => `
        <tr>
            <td>${escapar(n.nome)}</td>
            <td>${numero(n.notaTotal)}</td>
            <td>${n.questoesSemCorrecao}</td>
        </tr>`).join('') || '<tr><td colspan="3">Ninguém respondeu ainda.</td></tr>';
}

document.getElementById('avaliacao').addEventListener('change', carregarNotas);
carregarCorrecoes();
carregarAvaliacoes();