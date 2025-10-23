// Define visualmente item ativo do menu com base na página atual
(function () {
  const path = (location.pathname.split('/') || []).pop() || 'index.html';
  const links = document.querySelectorAll('.navbar nav a');
  links.forEach((a) => {
    const href = a.getAttribute('href');
    if (href === path) {
      a.setAttribute('aria-current', 'page');
    } else {
      a.removeAttribute('aria-current');
    }
  });
})();