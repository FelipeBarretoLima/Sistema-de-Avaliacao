// Tela de avaliações (RF2, RF3 e RF4). Só conversa com a API — nenhum SQL aqui.

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

function formatarData(iso) {
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
}

function horario(hora) {
    return hora.substring(0, 5);
}

async function carregarQuestoes() {
    const questoes = await (await fetch('/api/questoes')).json();
    document.getElementById('lista-questoes').innerHTML = questoes.map(q => `
        <tr>
            <td><input type="checkbox" class="escolher-questao" value="${q.id}"></td>
            <td>${q.id}</td>
            <td>${escapar(q.enunciado)}</td>
            <td>${q.tipo === 'MULTIPLA' ? 'Múltipla escolha' : 'Texto livre'}</td>
            <td><input type="number" class="valor" id="valor-${q.id}" min="0.5" step="0.5" value="1"></td>
        </tr>`).join('');
}

async function carregarAlunos() {
    const usuarios = await (await fetch('/api/usuarios')).json();
    document.getElementById('lista-alunos').innerHTML = usuarios
        .filter(u => u.perfil === 'ALUNO')
        .map(u => `<label><input type="checkbox" class="escolher-aluno" value="${u.id}"> ${escapar(u.nome)}</label>`)
        .join('');
}

function atualizarTotal() {
    const total = [...document.querySelectorAll('.escolher-questao:checked')]
        .reduce((soma, caixa) => soma + Number(document.getElementById(`valor-${caixa.value}`).value || 0), 0);
    document.getElementById('total').textContent = total.toLocaleString('pt-BR');
}

function lerFormulario() {
    return {
        titulo: document.getElementById('titulo').value,
        descricao: document.getElementById('descricao').value || null,
        data: document.getElementById('data').value || null,
        horaInicio: document.getElementById('horaInicio').value || null,
        horaFim: document.getElementById('horaFim').value || null,
        questoes: [...document.querySelectorAll('.escolher-questao:checked')].map(caixa => ({
            questaoId: Number(caixa.value),
            valor: Number(document.getElementById(`valor-${caixa.value}`).value)
        })),
        alunos: [...document.querySelectorAll('.escolher-aluno:checked')].map(caixa => Number(caixa.value))
    };
}

function mostrarMensagem(texto, erro) {
    const msg = document.getElementById('mensagem');
    msg.textContent = texto;
    msg.className = erro ? 'erro' : 'sucesso';
}

async function cadastrar(evento) {
    evento.preventDefault();
    const resp = await fetch('/api/avaliacoes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(lerFormulario())
    });
    const corpo = await resp.json();
    if (!resp.ok) {
        mostrarMensagem(corpo.erro || 'Não foi possível cadastrar a avaliação.', true);
        return;
    }
    mostrarMensagem(`Avaliação ${corpo.id} cadastrada.`, false);
    document.getElementById('form-avaliacao').reset();
    atualizarTotal();
    carregarAvaliacoes();
}

async function carregarAvaliacoes() {
    const avaliacoes = await (await fetch('/api/avaliacoes')).json();
    document.getElementById('lista-avaliacoes').innerHTML = avaliacoes.map(a => `
        <tr>
            <td>${a.id}</td>
            <td>${escapar(a.titulo)}</td>
            <td>${formatarData(a.data)}</td>
            <td>${horario(a.horaInicio)} às ${horario(a.horaFim)}</td>
            <td>${a.questoes.length}</td>
            <td>${a.questoes.reduce((soma, q) => soma + Number(q.valor), 0).toLocaleString('pt-BR')}</td>
            <td>${a.alunos.length}</td>
        </tr>`).join('');
}

document.getElementById('form-avaliacao').addEventListener('submit', cadastrar);
document.getElementById('lista-questoes').addEventListener('input', atualizarTotal);
document.getElementById('lista-questoes').addEventListener('change', atualizarTotal);
document.getElementById('marcar-todos').addEventListener('click', () =>
    document.querySelectorAll('.escolher-aluno').forEach(caixa => caixa.checked = true));

carregarQuestoes();
carregarAlunos();
carregarAvaliacoes();