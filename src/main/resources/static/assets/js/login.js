const form = document.getElementById('loginForm');
const usuarioInput = document.getElementById('username');
const senhaInput = document.getElementById('password');
const feedback = document.getElementById('loginFeedback');

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const login = usuarioInput.value.trim();
  const senha = senhaInput.value;
  feedback.textContent = '';
  if (!login || !senha) {
    feedback.textContent = 'Informe usuário e senha.';
    return;
  }
  try {
    const resp = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ login, senha }),
    });
    if (resp.status === 200) {
      // login ok
      location.href = 'index.html';
      return;
    }
    if (resp.status === 401) {
      feedback.textContent = 'Usuário ou senha inválidos.';
      return;
    }
    feedback.textContent = 'Falha no login. Tente novamente mais tarde.';
  } catch (err) {
    feedback.textContent = 'Erro de rede ao tentar login.';
  }
});