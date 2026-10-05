<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="System Settings"/><c:set var="activeNav" value="settings"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<form class="card card-pad" method="post" action="${ctx}/admin/settings" style="max-width:720px">
  <pf:csrf/>
  <div class="form-grid">
    <div class="field"><label for="siteName">Site name</label><input id="siteName" name="siteName" required maxlength="60" value="<c:out value='${settings.site_name}'/>"></div>
    <div class="field"><label for="contactEmail">Contact email</label><input id="contactEmail" name="contactEmail" type="email" required value="<c:out value='${settings.contact_email}'/>"><span class="hint">Shown on the Contact page.</span></div>
    <div class="field"><label for="maxApplications">Max pending applications per adopter</label><input id="maxApplications" name="maxApplications" type="number" min="1" max="50" required value="<c:out value='${settings.max_active_applications}'/>"><span class="hint">Adopters above this limit cannot apply again until a shelter responds.</span></div>
  </div>
  <label class="check mb-2"><input type="checkbox" name="allowRegistration" ${settings.allow_registration == 'true' ? 'checked' : ''}> Allow new registrations</label>
  <label class="check mb-2"><input type="checkbox" name="requirePetApproval" ${settings.require_pet_approval == 'true' ? 'checked' : ''}> New pet listings need admin approval</label>
  <button class="btn" type="submit">Save settings</button>
</form>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
