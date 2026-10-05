/* PetFeet - lightweight UI behaviour (no frameworks) */
(function () {
  'use strict';
  var body = document.body;
  var ctx = body.getAttribute('data-ctx') || '';
  var csrfMeta = document.querySelector('meta[name="csrf-token"]');
  var csrf = csrfMeta ? csrfMeta.getAttribute('content') : '';

  body.classList.add('page-enter');

  /* ---- thin progress bar shown while the next page loads ---- */
  var bar = document.createElement('div');
  bar.className = 'page-progress';
  document.body.appendChild(bar);
  function startProgress() { bar.classList.add('run'); }
  window.addEventListener('pageshow', function () { bar.classList.remove('run'); });
  document.addEventListener('click', function (e) {
    var a = e.target.closest('a[href]');
    if (a && a.host === location.host && !a.hasAttribute('target') && !e.ctrlKey && !e.metaKey && a.getAttribute('href').charAt(0) !== '#') { startProgress(); }
  });

  /* ---- mobile navigation + dashboard sidebar ---- */
  document.querySelectorAll('[data-nav-toggle]').forEach(function (btn) {
    btn.addEventListener('click', function () { document.querySelector('.nav').classList.toggle('open'); });
  });
  document.querySelectorAll('[data-side-toggle]').forEach(function (btn) {
    btn.addEventListener('click', function () { document.querySelector('.sidebar').classList.toggle('open'); });
  });

  /* ---- show / hide password ---- */
  document.querySelectorAll('[data-toggle-password]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var input = document.getElementById(btn.getAttribute('data-toggle-password'));
      var show = input.type === 'password';
      input.type = show ? 'text' : 'password';
      btn.textContent = show ? 'Hide' : 'Show';
    });
  });

  /* ---- flash messages disappear after a while ---- */
  document.querySelectorAll('.alert[data-autohide]').forEach(function (el) {
    setTimeout(function () { el.style.transition = 'opacity .5s'; el.style.opacity = '0'; setTimeout(function () { el.remove(); }, 500); }, 6000);
  });

  /* ---- forms: confirm dialogs, password match, loading state ---- */
  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var msg = form.getAttribute('data-confirm');
      if (msg && !window.confirm(msg)) { e.preventDefault(); return; }
      var pw = form.querySelector('[data-match]');
      if (pw) {
        var other = form.querySelector(pw.getAttribute('data-match'));
        if (other && other.value !== pw.value) { e.preventDefault(); pw.setCustomValidity('Passwords do not match'); pw.reportValidity(); return; }
      }
      var submit = form.querySelector('button[type="submit"], .btn[type="submit"]');
      if (submit) { setTimeout(function () { submit.classList.add('loading'); }, 0); }
      startProgress();
    });
    var m = form.querySelector('[data-match]');
    if (m) { m.addEventListener('input', function () { m.setCustomValidity(''); }); }
  });

  /* ---- favourite hearts (AJAX) ---- */
  document.querySelectorAll('.heart-btn[data-pet-id]').forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      var data = new URLSearchParams();
      data.append('petId', btn.getAttribute('data-pet-id'));
      fetch(ctx + '/favorite', { method: 'POST', headers: { 'X-CSRF-TOKEN': csrf, 'Content-Type': 'application/x-www-form-urlencoded' }, body: data })
        .then(function (r) {
          if (r.status === 401) { window.location.href = ctx + '/login'; return null; }
          return r.json();
        })
        .then(function (json) {
          if (!json) return;
          if (json.error) { toast(json.error); return; }
          btn.classList.toggle('on', json.favorite);
          btn.setAttribute('aria-pressed', json.favorite ? 'true' : 'false');
          btn.classList.remove('pop'); void btn.offsetWidth; btn.classList.add('pop');
          if (btn.hasAttribute('data-remove-card') && !json.favorite) {
            var card = btn.closest('.pet-card');
            if (card) { card.style.transition = 'opacity .3s'; card.style.opacity = '0'; setTimeout(function () { card.remove(); }, 300); }
          }
        })
        .catch(function () { toast('Could not update your favourites. Please try again.'); });
    });
  });

  function toast(text) {
    var t = document.createElement('div');
    t.className = 'alert alert-error';
    t.style.cssText = 'position:fixed;right:20px;bottom:20px;z-index:300;max-width:340px;box-shadow:0 10px 30px rgba(0,0,0,.15)';
    t.textContent = text;
    document.body.appendChild(t);
    setTimeout(function () { t.remove(); }, 4000);
  }

  /* ---- compare two pets (selection kept in sessionStorage) ---- */
  var compareBar = document.querySelector('[data-compare-bar]');
  var checks = document.querySelectorAll('input[data-compare-id]');
  function readCompare() { try { return JSON.parse(sessionStorage.getItem('pfCompare') || '[]'); } catch (e) { return []; } }
  function renderCompare() {
    if (!compareBar) return;
    var ids = readCompare();
    checks.forEach(function (c) { c.checked = ids.indexOf(c.getAttribute('data-compare-id')) >= 0; });
    compareBar.classList.toggle('show', ids.length > 0);
    compareBar.querySelector('[data-compare-count]').textContent = ids.length + ' of 2 selected';
    var go = compareBar.querySelector('[data-compare-go]');
    go.style.display = ids.length === 2 ? '' : 'none';
    if (ids.length === 2) { go.href = ctx + '/compare?a=' + ids[0] + '&b=' + ids[1]; }
  }
  checks.forEach(function (c) {
    c.addEventListener('change', function () {
      var ids = readCompare(), id = c.getAttribute('data-compare-id');
      ids = ids.filter(function (x) { return x !== id; });
      if (c.checked) { if (ids.length >= 2) { ids.shift(); } ids.push(id); }
      sessionStorage.setItem('pfCompare', JSON.stringify(ids));
      renderCompare();
    });
  });
  var clear = document.querySelector('[data-compare-clear]');
  if (clear) { clear.addEventListener('click', function () { sessionStorage.removeItem('pfCompare'); renderCompare(); }); }
  renderCompare();

  /* ---- search suggestions ---- */
  document.querySelectorAll('input[data-suggest]').forEach(function (input) {
    var list = document.createElement('div');
    list.className = 'suggest-list';
    input.parentNode.appendChild(list);
    var timer = null, index = -1;
    function close() { list.style.display = 'none'; list.innerHTML = ''; index = -1; }
    input.addEventListener('input', function () {
      clearTimeout(timer);
      var q = input.value.trim();
      if (!q) { close(); return; }
      timer = setTimeout(function () {
        fetch(ctx + '/suggest?q=' + encodeURIComponent(q)).then(function (r) { return r.json(); }).then(function (items) {
          list.innerHTML = '';
          items.forEach(function (text) {
            var b = document.createElement('button');
            b.type = 'button'; b.textContent = text;
            b.addEventListener('click', function () { input.value = text; close(); input.form.submit(); });
            list.appendChild(b);
          });
          list.style.display = items.length ? 'block' : 'none';
        }).catch(close);
      }, 180);
    });
    input.addEventListener('keydown', function (e) {
      var items = list.querySelectorAll('button');
      if (!items.length) return;
      if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
        e.preventDefault();
        index = (index + (e.key === 'ArrowDown' ? 1 : -1) + items.length) % items.length;
        items.forEach(function (b, i) { b.classList.toggle('sel', i === index); });
        input.value = items[index].textContent;
      } else if (e.key === 'Escape') { close(); }
    });
    document.addEventListener('click', function (e) { if (!list.contains(e.target) && e.target !== input) close(); });
  });

  /* ---- scroll reveal (used on "How it works") ---- */
  var reveals = document.querySelectorAll('.reveal');
  if ('IntersectionObserver' in window) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (en) { if (en.isIntersecting) { en.target.classList.add('visible'); io.unobserve(en.target); } });
    }, { threshold: .15 });
    reveals.forEach(function (el, i) { el.style.transitionDelay = (i % 4) * 90 + 'ms'; io.observe(el); });
  } else { reveals.forEach(function (el) { el.classList.add('visible'); }); }

  /* ---- modals: [data-modal-open="id"] copies data-f-* attributes into form fields ---- */
  document.querySelectorAll('[data-modal-open]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var modal = document.getElementById(btn.getAttribute('data-modal-open'));
      Array.prototype.forEach.call(btn.attributes, function (attr) {
        if (attr.name.indexOf('data-f-') !== 0) return;
        var field = modal.querySelector('[name="' + attr.name.substring(7) + '"]');
        if (!field) return;
        if (field.type === 'checkbox') { field.checked = attr.value === 'true'; } else { field.value = attr.value; }
      });
      var title = btn.getAttribute('data-modal-title');
      if (title) { modal.querySelector('[data-modal-title-target]').textContent = title; }
      modal.classList.add('open');
      var first = modal.querySelector('input:not([type=hidden]), select, textarea');
      if (first) { setTimeout(function () { first.focus(); }, 50); }
    });
  });
  document.querySelectorAll('.modal-backdrop').forEach(function (m) {
    m.addEventListener('click', function (e) { if (e.target === m || e.target.closest('[data-modal-close]')) { m.classList.remove('open'); } });
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') { document.querySelectorAll('.modal-backdrop.open').forEach(function (m) { m.classList.remove('open'); }); }
  });
})();
