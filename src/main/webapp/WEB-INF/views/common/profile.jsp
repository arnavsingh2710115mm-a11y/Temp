<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My Profile"/><c:set var="activeNav" value="profile"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:set var="isAdopter" value="${profile.role.name() == 'ADOPTER'}"/>
<div class="grid" style="grid-template-columns:minmax(0,2fr) minmax(260px,1fr);gap:22px;align-items:start">
  <form class="card card-pad" method="post" action="${ctx}/profile">
    <pf:csrf/>
    <h3>Personal details</h3>
    <div class="form-grid">
      <div class="field"><label for="name">Name</label><input id="name" name="name" required maxlength="100" value="<c:out value='${profile.name}'/>"></div>
      <div class="field"><label for="email">Email</label><input id="email" value="<c:out value='${profile.email}'/>" disabled><span class="hint">Email cannot be changed here.</span></div>
      <div class="field"><label for="phone">Phone</label><input id="phone" name="phone" required value="<c:out value='${profile.phone}'/>"></div>
      <div class="field"><label for="city">City</label><input id="city" name="city" maxlength="80" value="<c:out value='${profile.city}'/>"></div>
      <div class="field full"><label for="address">Address</label><input id="address" name="address" maxlength="255" value="<c:out value='${profile.address}'/>"></div>
    </div>
    <c:if test="${isAdopter}">
      <h3 class="mt-2">Pet preferences</h3><p class="muted small">Used for "Pets you may love" recommendations.</p>
      <div class="form-grid">
        <div class="field"><label for="preferredSpecies">Preferred species</label><select id="preferredSpecies" name="preferredSpecies"><option value="">No preference</option>
          <c:forEach items="${['Dog','Cat','Rabbit']}" var="s"><option ${profile.preferredSpecies == s ? 'selected' : ''}>${s}</option></c:forEach></select></div>
        <div class="field"><label for="preferredMaxAge">Maximum age (years)</label><input id="preferredMaxAge" name="preferredMaxAge" type="number" min="0" max="30" value="${profile.preferredMaxAge}"></div>
      </div>
    </c:if>
    <h3 class="mt-2">Change password</h3><p class="muted small">Leave blank to keep your current password.</p>
    <div class="form-grid">
      <div class="field"><label for="currentPassword">Current password</label><input id="currentPassword" name="currentPassword" type="password" autocomplete="current-password"></div>
      <div class="field"><label for="newPassword">New password</label><input id="newPassword" name="newPassword" type="password" minlength="8" autocomplete="new-password"></div>
    </div>
    <button class="btn" type="submit">Save changes</button>
  </form>
  <div class="card card-pad">
    <div class="avatar" style="width:64px;height:64px;font-size:1.6rem"><c:out value="${fn:toUpperCase(fn:substring(profile.name,0,1))}"/></div>
    <h3 class="mt-1 mb-0"><c:out value="${profile.name}"/></h3><span class="badge badge-${fn:toLowerCase(profile.role.name())}"><c:out value="${profile.roleLabel}"/></span>
    <p class="small muted mt-2">Member since <fmt:formatDate value="${profile.createdAt}" pattern="d MMM yyyy"/></p>
    <h3 style="font-size:1rem">What you can do</h3>
    <ul class="small" style="padding-left:18px;margin:0"><c:forEach items="${profile.capabilities}" var="cap"><li><c:out value="${cap}"/></li></c:forEach></ul>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
