// Tela de relatórios (RF7): cada relatório tem tabela e gráfico (Chart.js).
// A tela só chama a API /api/relatorios — todo o SQL fica no RelatorioDAO.

const graficos = {};

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
    return valor === null || valor === undefined ? '—' : Number(valor).toLocaleString('pt-BR');
}

function desenharGrafico(id, configuracao) {
    if (typeof Chart === 'undefined') return; // sem internet, o Chart.js não carrega: a tabela continua aparecendo
    if (graficos[id]) graficos[id].destroy();
    graficos[id] = new Chart(document.getElementById(id), configuracao);
}

async function buscar(url) {
    return (await fetch(url)).json();
}

// ---------- R1 ----------
async function carregarR1() {
    const periodo = `inicio=${document.getElementById('r1-inicio').value}&fim=${document.getElementById('r1-fim').value}`;
    const linhas = await buscar(`/api/relatorios/media-por-avaliacao?${periodo}`);
    const pendentes = await buscar(`/api/relatorios/alunos-pendentes?${periodo}`);

    document.getElementById('r1-tabela').innerHTML = linhas.map(l => `
        <tr>
            <td>${escapar(l.titulo)}</td>
            <td>${formatarData(l.data)}</td>
            <td>${l.designados}</td>
            <td>${l.responderam}</td>
            <td>${l.pendentes}</td>
            <td>${numero(l.media)}</td>
        </tr>`).join('') || '<tr><td colspan="6">Nenhuma avaliação no período.</td></tr>';

    document.getElementById('r1-pendentes').innerHTML = pendentes.map(p => `
        <tr><td>${escapar(p.avaliacao)}</td><td>${escapar(p.aluno)}</td></tr>`).join('')
        || '<tr><td colspan="2">Todos os designados já responderam.</td></tr>';

    desenharGrafico('r1-grafico', {
        type: 'bar',
        data: {
            labels: linhas.map(l => l.titulo),
            datasets: [
                { label: 'Média da nota total', data: linhas.map(l => l.media), backgroundColor: '#1e7a46', yAxisID: 'y' },
                { label: 'Alunos pendentes', data: linhas.map(l => l.pendentes), backgroundColor: '#c9a227', yAxisID: 'y2' }
            ]
        },
        options: {
            maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: true, max: 10, title: { display: true, text: 'Média (0 a 10)' } },
                y2: { beginAtZero: true, position: 'right', grid: { drawOnChartArea: false }, ticks: { precision: 0 },
                      title: { display: true, text: 'Pendentes' } }
            }
        }
    });
}

// ---------- R2 ----------
async function carregarAlunosR2() {
    const usuarios = await buscar('/api/usuarios');
    document.getElementById('r2-aluno').innerHTML = usuarios
        .filter(u => u.perfil === 'ALUNO')
        .map(u => `<option value="${u.id}">${escapar(u.nome)}</option>`).join('');
    carregarR2();
}

async function carregarR2() {
    const usuarioId = document.getElementById('r2-aluno').value;
    if (!usuarioId) return;
    const linhas = await buscar(`/api/relatorios/evolucao-do-aluno?usuarioId=${usuarioId}`);

    document.getElementById('r2-tabela').innerHTML = linhas.map(l => {
        const diferenca = l.notaAluno - l.mediaDemais;
        return `
        <tr>
            <td>${escapar(l.titulo)}</td>
            <td>${formatarData(l.data)}</td>
            <td>${numero(l.notaAluno)}</td>
            <td>${numero(l.mediaDemais)}</td>
            <td class="${diferenca >= 0 ? 'sucesso' : 'erro'}">${diferenca >= 0 ? '+' : ''}${numero(diferenca.toFixed(2))}</td>
        </tr>`;
    }).join('') || '<tr><td colspan="5">Este aluno ainda não respondeu nenhuma avaliação.</td></tr>';

    const nome = document.getElementById('r2-aluno').selectedOptions[0].textContent;
    desenharGrafico('r2-grafico', {
        type: 'line',
        data: {
            labels: linhas.map(l => `${l.titulo} (${formatarData(l.data)})`),
            datasets: [
                { label: nome, data: linhas.map(l => l.notaAluno), borderColor: '#1e7a46', backgroundColor: '#1e7a46', tension: .2 },
                { label: 'Média dos demais', data: linhas.map(l => l.mediaDemais), borderColor: '#888', backgroundColor: '#888',
                  borderDash: [6, 4], tension: .2 }
            ]
        },
        options: { maintainAspectRatio: false, scales: { y: { beginAtZero: true, max: 10, title: { display: true, text: 'Nota total' } } } }
    });
}

// ---------- R3 ----------
async function carregarR3() {
    const busca = encodeURIComponent(document.getElementById('r3-busca').value);
    const linhas = await buscar(`/api/relatorios/questoes-mais-erradas?busca=${busca}`);
    const nuncaUsadas = await buscar('/api/relatorios/questoes-nunca-usadas');

    document.getElementById('r3-tabela').innerHTML = linhas.map(l => `
        <tr>
            <td>${l.posicao}º</td>
            <td>${l.id}</td>
            <td>${escapar(l.enunciado)}</td>
            <td>${l.respostas}</td>
            <td>${l.erros}</td>
            <td>${numero(l.percentualAcerto)}%</td>
        </tr>`).join('') || '<tr><td colspan="6">Nenhuma questão encontrada.</td></tr>';

    document.getElementById('r3-nunca-usadas').innerHTML = nuncaUsadas.map(q => `
        <tr><td>${q.id}</td><td>${escapar(q.enunciado)}</td><td>${q.tipo === 'MULTIPLA' ? 'Múltipla escolha' : 'Texto livre'}</td></tr>`).join('')
        || '<tr><td colspan="3">Todas as questões já foram usadas.</td></tr>';

    desenharGrafico('r3-grafico', {
        type: 'bar',
        data: {
            labels: linhas.map(l => `${l.posicao}º — Questão ${l.id}`),
            datasets: [{ label: '% de acerto', data: linhas.map(l => l.percentualAcerto), backgroundColor: '#b3443a' }]
        },
        options: {
            indexAxis: 'y',
            maintainAspectRatio: false,
            scales: { x: { beginAtZero: true, max: 100, title: { display: true, text: '% de acerto' } } }
        }
    });
}

document.getElementById('r1-atualizar').addEventListener('click', carregarR1);
document.getElementById('r2-aluno').addEventListener('change', carregarR2);
document.getElementById('r3-atualizar').addEventListener('click', carregarR3);

carregarR1();
carregarAlunosR2();
carregarR3();