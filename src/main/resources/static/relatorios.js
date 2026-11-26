// Tela de relatórios (RF7): cada relatório tem tabela e gráfico (Chart.js).
// A tela só chama a API /api/relatorios — todo o SQL fica no RelatorioDAO.

const graficos = {};

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
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

document.getElementById('r3-atualizar').addEventListener('click', carregarR3);

carregarR3();