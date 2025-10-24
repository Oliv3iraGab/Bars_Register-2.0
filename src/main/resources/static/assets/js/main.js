// Substitui const/let e arrow functions por ES5 para compatibilidade
(function () {
  function getPath() {
    return window.location.pathname || '';
  }

  function markActiveMenu() {
    var path = getPath();
    var items = document.querySelectorAll('.menu-item');
    for (var i = 0; i < items.length; i++) {
      var el = items[i];
      var href = (el.getAttribute('href') || '').trim();
      var isActive = href && path.indexOf(href) >= 0;
      if (isActive) el.classList.add('active');
      else el.classList.remove('active');
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', markActiveMenu);
  } else {
    markActiveMenu();
  }
})();