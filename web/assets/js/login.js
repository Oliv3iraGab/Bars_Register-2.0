// Validações simples de formulário de login
(function () {
  const form = document.getElementById("loginForm");
  const usuario = document.getElementById("usuario");
  const senha = document.getElementById("senha");
  const erroUsuario = document.getElementById("erro-usuario");
  const erroSenha = document.getElementById("erro-senha");
  const feedback = document.getElementById("loginFeedback");

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    let ok = true;
    erroUsuario.textContent = "";
    erroSenha.textContent = "";
    feedback.textContent = "";

    if (!usuario.value.trim()) {
      ok = false;
      erroUsuario.textContent = "Informe o usuário.";
      usuario.focus();
    }

    if (!senha.value.trim()) {
      ok = false;
      erroSenha.textContent = "Informe a senha.";
      if (usuario.value.trim()) senha.focus();
    }

    if (ok) {
      feedback.textContent = "Login validado (mock). Redirecionando para o Início...";
      setTimeout(() => {
        window.location.href = "index.html";
      }, 1000);
    }
  });
})();