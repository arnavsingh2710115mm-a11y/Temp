/* PetFeet - tiny canvas charts (bar + donut), no external library.
   Usage: <canvas data-chart="bar|donut" data-labels="A|B|C" data-values="1|2|3"></canvas> */
(function () {
  'use strict';
  var COLORS = ['#A8D5BA', '#FFD6BA', '#9CC8E2', '#D4C8EE', '#FFE08A', '#F4A9A0', '#7DB997'];
  var INK = '#263238', MUTED = '#5B6B72', GRID = '#E7ECE4';

  function setup(canvas) {
    var ratio = window.devicePixelRatio || 1;
    var w = canvas.clientWidth, h = canvas.clientHeight;
    canvas.width = w * ratio; canvas.height = h * ratio;
    var ctx = canvas.getContext('2d');
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
    return { ctx: ctx, w: w, h: h };
  }

  function bar(canvas, labels, values) {
    var s = setup(canvas), ctx = s.ctx, pad = { l: 34, r: 10, t: 16, b: 34 };
    var max = Math.max.apply(null, values.concat([1]));
    var step = Math.max(1, Math.ceil(max / 4)); max = step * Math.ceil(max / step);
    ctx.font = '700 12px Nunito, sans-serif'; ctx.textAlign = 'right'; ctx.fillStyle = MUTED; ctx.strokeStyle = GRID;
    for (var g = 0; g <= max; g += step) {
      var y = pad.t + (s.h - pad.t - pad.b) * (1 - g / max);
      ctx.beginPath(); ctx.moveTo(pad.l, y); ctx.lineTo(s.w - pad.r, y); ctx.stroke();
      ctx.fillText(g, pad.l - 6, y + 4);
    }
    var slot = (s.w - pad.l - pad.r) / values.length, bw = Math.min(54, slot * 0.6);
    values.forEach(function (v, i) {
      var x = pad.l + slot * i + (slot - bw) / 2;
      var bh = (s.h - pad.t - pad.b) * (v / max), y2 = s.h - pad.b - bh;
      ctx.fillStyle = COLORS[i % COLORS.length];
      ctx.beginPath();
      var r = Math.min(10, bw / 2);
      ctx.moveTo(x, s.h - pad.b); ctx.lineTo(x, y2 + r); ctx.quadraticCurveTo(x, y2, x + r, y2);
      ctx.lineTo(x + bw - r, y2); ctx.quadraticCurveTo(x + bw, y2, x + bw, y2 + r); ctx.lineTo(x + bw, s.h - pad.b); ctx.closePath(); ctx.fill();
      ctx.fillStyle = INK; ctx.textAlign = 'center'; ctx.font = '800 12px Nunito, sans-serif';
      ctx.fillText(v, x + bw / 2, y2 - 6);
      ctx.fillStyle = MUTED; ctx.font = '700 12px Nunito, sans-serif';
      ctx.fillText(labels[i].length > 11 ? labels[i].slice(0, 10) + '…' : labels[i], x + bw / 2, s.h - 12);
    });
  }

  function donut(canvas, labels, values) {
    var s = setup(canvas), ctx = s.ctx;
    var total = values.reduce(function (a, b) { return a + b; }, 0);
    var cx = s.w / 2, cy = s.h / 2, R = Math.min(s.w, s.h) / 2 - 8, r = R * 0.62;
    if (!total) { ctx.fillStyle = MUTED; ctx.textAlign = 'center'; ctx.font = '700 14px Nunito, sans-serif'; ctx.fillText('No data yet', cx, cy); return; }
    var angle = -Math.PI / 2;
    values.forEach(function (v, i) {
      var a2 = angle + (v / total) * Math.PI * 2;
      ctx.beginPath(); ctx.arc(cx, cy, R, angle, a2); ctx.arc(cx, cy, r, a2, angle, true); ctx.closePath();
      ctx.fillStyle = COLORS[i % COLORS.length]; ctx.fill();
      ctx.strokeStyle = '#fff'; ctx.lineWidth = 3; ctx.stroke();
      angle = a2;
    });
    ctx.fillStyle = INK; ctx.textAlign = 'center'; ctx.font = '700 26px Fredoka, sans-serif';
    ctx.fillText(total, cx, cy + 6);
    ctx.fillStyle = MUTED; ctx.font = '700 12px Nunito, sans-serif'; ctx.fillText('total', cx, cy + 24);
  }

  function legend(canvas, labels, values) {
    var holder = canvas.parentNode.nextElementSibling;
    if (!holder || !holder.classList.contains('legend')) return;
    holder.innerHTML = '';
    labels.forEach(function (l, i) {
      var span = document.createElement('span');
      var dot = document.createElement('i'); dot.style.background = COLORS[i % COLORS.length];
      span.appendChild(dot); span.appendChild(document.createTextNode(l + ' (' + values[i] + ')'));
      holder.appendChild(span);
    });
  }

  function draw() {
    document.querySelectorAll('canvas[data-chart]').forEach(function (c) {
      var labels = (c.getAttribute('data-labels') || '').split('|').filter(Boolean);
      var values = (c.getAttribute('data-values') || '').split('|').filter(Boolean).map(Number);
      if (c.getAttribute('data-chart') === 'donut') { donut(c, labels, values); } else { bar(c, labels, values); }
      legend(c, labels, values);
    });
  }
  window.addEventListener('load', draw);
  var t; window.addEventListener('resize', function () { clearTimeout(t); t = setTimeout(draw, 150); });
})();
