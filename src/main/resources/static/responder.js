// Tela de responder avaliação (RF5). Só conversa com a API — nenhum SQL aqui.

let aluno = JSON.parse(sessionStorage.getItem('usuario') || 'null');
let avaliacaoAtual = null;

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

function formatarData(iso) {
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
}

function numero(valor) {
    return Number(valor).toLocaleString('pt-BR');
}

function mostrar(id, visivel) {
    document.getElementById(id).classList.toggle('oculto', !visivel);
}

function mostrarMensagem(texto, erro) {
    const msg = document.getElementById('mensagem');
    msg.textContent = texto;
    msg.className = erro ? 'erro' : 'sucesso';
}

async function entrar() {
    const resp = await fetch('/api/usuarios/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: document.getElementById('email').value.trim() })
    });
    if (!resp.ok) {
        document.getElementById('mensagem-login').textContent = 'E-mail não encontrado.';
        return;
    }
    aluno = await resp.json();
    sessionStorage.setItem('usuario', JSON.stringify(aluno));
    iniciar();
}

function trocarAluno() {
    sessionStorage.removeItem('usuario');
    aluno = null;
    mostrarMensagem('', false);
    iniciar();
}

async function carregarAvaliacoes() {
    const avaliacoes = await (await fetch(`/api/respostas/avaliacoes?usuarioId=${aluno.id}`)).json();
    document.getElementById('lista-avaliacoes').innerHTML = avaliacoes.map(a => `
        <tr>
            <td>${escapar(a.titulo)}</td>
            <td>${formatarData(a.data)}</td>
            <td>${a.horaInicio.substring(0, 5)} às ${a.horaFim.substring(0, 5)}</td>
            <td>${a.jaRespondeu
                ? 'Respondida'
                : `<button onclick="abrirProva(${a.id}, '${escapar(a.titulo).replace(/'/g, '&#39;')}')">Responder</button>`}</td>
        </tr>`).join('') || '<tr><td colspan="4">Nenhuma avaliação designada.</td></tr>';
}

async function abrirProva(avaliacaoId, titulo) {
    const resp = await fetch(`/api/respostas/avaliacoes/${avaliacaoId}/questoes?usuarioId=${aluno.id}`);
    const corpo = await resp.json();
    if (!resp.ok) {
        mostrarMensagem(corpo.erro, true);
        return;
    }
    avaliacaoAtual = avaliacaoId;
    document.getElementById('titulo-prova').textContent = titulo;
    document.getElementById('questoes').innerHTML = corpo.map(q => `
        <div class="questao" data-questao="${q.questaoId}" data-tipo="${q.tipo}">
            <h3>${q.ordem}. ${escapar(q.enunciado)} <span class="valor-questao">(${numero(q.valor)} pt)</span></h3>
            ${q.tipo === 'MULTIPLA'
                ? q.alternativas.map(a => `
                    <label><input type="radio" name="q${q.questaoId}" value="${a.letra}"> ${a.letra}) ${escapar(a.texto)}</label>`).join('')
                : `<textarea rows="3" name="q${q.questaoId}" placeholder="Sua resposta"></textarea>`}
        </div>`).join('');
    mostrarMensagem('', false);
    mostrar('bloco-avaliacoes', false);
    mostrar('bloco-prova', true);
}

async function enviar(evento) {
    evento.preventDefault();
    const respostas = [...document.querySelectorAll('.questao')].map(div => {
        const questaoId = Number(div.dataset.questao);
        if (div.dataset.tipo === 'MULTIPLA') {
            const marcada = div.querySelector('input:checked');
            return { questaoId, letra: marcada ? marcada.value : null, texto: null };
        }
        return { questaoId, letra: null, texto: div.querySelector('textarea').value };
    });

    const resp = await fetch('/api/respostas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ avaliacaoId: avaliacaoAtual, usuarioId: aluno.id, respostas })
    });
    const corpo = await resp.json();
    if (!resp.ok) {
        mostrarMensagem(corpo.erro, true);
        return;
    }
    const pendentes = corpo.questoesSemCorrecao > 0
        ? ` ${corpo.questoesSemCorrecao} questão(ões) de texto aguardando a correção do professor.`
        : '';
    mostrarMensagem(`Respostas enviadas! Nota até agora: ${numero(corpo.notaTotal)}.${pendentes}`, false);
    mostrar('bloco-prova', false);
    mostrar('bloco-avaliacoes', true);
    carregarAvaliacoes();
}

function iniciar() {
    mostrar('bloco-login', !aluno);
    mostrar('bloco-avaliacoes', !!aluno);
    mostrar('bloco-prova', false);
    if (aluno) {
        document.getElementById('nome-aluno').textContent = aluno.nome;
        carregarAvaliacoes();
    }
}

document.getElementById('entrar').addEventListener('click', entrar);
document.getElementById('trocar').addEventListener('click', trocarAluno);
document.getElementById('form-prova').addEventListener('submit', enviar);
iniciar();