<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Create account"/><c:set var="noFlash" value="true"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<div class="auth-wrap">
  <div class="auth-art">
    <img class="scene" src="${ctx}/images/auth.svg" alt="">
    <h2>Join the PetFeet family</h2>
    <p class="muted">Adopt a friend or help pets find homes.</p>
  </div>
  <div class="auth-form">
    <div class="auth-card">
      <div class="brand" style="margin-bottom:22px"><img src="${ctx}/images/logo.svg" alt=""><span><span class="brand-name">PETFEET</span><span class="brand-tag">Every Paw Deserves a Home.</span></span></div>
      <h2>Create your account</h2>
      <%@ include file="/WEB-INF/views/common/flash.jspf" %>
      <form method="post" action="${ctx}/register" novalidate>
        <pf:csrf/>
        <div class="field"><label>I want to</label>
          <div class="role-pick">
            <label><input type="radio" name="role" value="ADOPTER" ${role != 'SHELTER' ? 'checked' : ''}> &#128054; Adopt a pet<small>Adopter account</small></label>
            <label><input type="radio" name="role" value="SHELTER" ${role == 'SHELTER' ? 'checked' : ''}> &#127968; List pets<small>Shelter account</small></label>
          </div>
        </div>
        <div class="field"><label for="name">Name</label><input id="name" name="name" required maxlength="100" value="<c:out value='${name}'/>" placeholder="Full name or shelter name"></div>
        <div class="form-grid">
          <div class="field"><label for="email">Email</label><input id="email" name="email" type="email" required value="<c:out value='${email}'/>" placeholder="you@example.com"></div>
          <div class="field"><label for="phone">Phone</label><input id="phone" name="phone" type="tel" required pattern="[0-9+\- ]{7,15}" value="<c:out value='${phone}'/>" placeholder="9876543210"></div>
        </div>
        <div class="field"><label for="password">Password</label>
          <div class="input-group"><input id="password" name="password" type="password" required minlength="8" autocomplete="new-password" placeholder="At least 8 characters"><button type="button" data-toggle-password="password">Show</button></div>
          <span class="hint">Use 8+ characters with at least one letter and one number.</span></div>
        <div class="field"><label for="confirmPassword">Confirm password</label><input id="confirmPassword" name="confirmPassword" type="password" required data-match="#password" autocomplete="new-password"></div>
        <button class="btn btn-block" type="submit">Create account</button>
      </form>
      <p class="center mt-2 mb-0">Already registered? <a href="${ctx}/login">Log in</a></p>
    </div>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
