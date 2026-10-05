<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Login"/><c:set var="noFlash" value="true"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="auth-wrap">
  <div class="auth-art">
    <img class="scene" src="${ctx}/images/auth.svg" alt="">
    <h2>Welcome back!</h2>
    <p class="muted">Someone special is waiting for you.</p>
  </div>
  <div class="auth-form">
    <div class="auth-card">
      <div class="brand" style="margin-bottom:22px"><img src="${ctx}/images/logo.svg" alt=""><span><span class="brand-name">PETFEET</span><span class="brand-tag">Every Paw Deserves a Home.</span></span></div>
      <h2>Log in to PetFeet</h2>
      <%@ include file="/WEB-INF/views/common/flash.jspf" %>
      <form method="post" action="${ctx}/login" novalidate>
        <pf:csrf/>
        <div class="field"><label for="email">Email</label><input id="email" name="email" type="email" required autocomplete="email" value="<c:out value='${email}'/>" placeholder="you@example.com"></div>
        <div class="field"><label for="password">Password</label>
          <div class="input-group"><input id="password" name="password" type="password" required autocomplete="current-password" placeholder="Your password"><button type="button" data-toggle-password="password">Show</button></div>
        </div>
        <div class="row between" style="margin-bottom:18px">
          <label class="check"><input type="checkbox" name="remember" ${remember ? 'checked' : ''}> Remember me</label>
          <a href="#" data-modal-open="forgot">Forgot password?</a>
        </div>
        <button class="btn btn-block" type="submit">Log in</button>
      </form>
      <p class="center mt-2 mb-0">New to PetFeet? <a href="${ctx}/register">Create an account</a></p>
      <div class="demo-box"><b>Demo accounts</b> (password for all: <code>Demo@123</code>)<br>
        Admin: <code>admin@petfeet.com</code><br>Shelter: <code>shelter@petfeet.com</code><br>Adopter: <code>adopter@petfeet.com</code></div>
    </div>
  </div>
</div>
<div class="modal-backdrop" id="forgot"><div class="modal">
  <div class="modal-head"><h3 class="mb-0">Forgot your password?</h3><button class="modal-close" type="button" data-modal-close aria-label="Close">&#10005;</button></div>
  <div class="alert alert-info">E-mail based reset is not enabled in this college version.</div>
  <p>Please ask a PetFeet admin to set a new password for you (Admin &rarr; User Management &rarr; Edit user). After logging in you can change it under <b>Profile</b>.</p>
  <button class="btn" type="button" data-modal-close>Got it</button>
</div></div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
