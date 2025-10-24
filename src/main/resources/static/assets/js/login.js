var form = document.getElementById('loginForm');
var usuarioInput = document.getElementById('username');
var senhaInput = document.getElementById('password');
var feedback = document.getElementById('loginFeedback');

form.addEventListener('submit', function (e) {
  e.preventDefault();
  var login = usuarioInput.value.trim();
  var senha = senhaInput.value;
  feedback.textContent = '';
  if (!login || !senha) {
    feedback.textContent = 'Informe usuário e senha.';
    return;
  }
  fetch('/api/users/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ login: login, senha: senha }),
  })
    .then(function (resp) {
      if (resp.status === 200) {
        location.href = 'index.html';
        return { ok: true };
      }
      if (resp.status === 401) {
        feedback.textContent = 'Usuário ou senha inválidos.';
        return { ok: false };
      }
      feedback.textContent = 'Falha no login. Tente novamente mais tarde.';
      return { ok: false };
    })
    .catch(function () {
      feedback.textContent = 'Erro de rede ao tentar login.';
    });
});