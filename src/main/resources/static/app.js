// A tela só conversa com a API (/api/...). Nenhum SQL aqui.

async function carregarUsuarios() {
    const resp = await fetch('/api/usuarios');
    const usuarios = await resp.json();
    document.getElementById('usuarios').innerHTML = usuarios
        .map(u => `<tr><td>${u.nome}</td><td>${u.email}</td><td>${u.perfil}</td></tr>`)
        .join('');
}

async function entrar() {
    const email = document.getElementById('email').value;
    const resp = await fetch('/api/usuarios/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    });
    const msg = document.getElementById('mensagem');
    if (resp.ok) {
        const usuario = await resp.json();
        sessionStorage.setItem('usuario', JSON.stringify(usuario));
        msg.textContent = `Olá, ${usuario.nome} (${usuario.perfil})!`;
    } else {
        msg.textContent = 'E-mail não encontrado.';
    }
}

carregarUsuarios();
