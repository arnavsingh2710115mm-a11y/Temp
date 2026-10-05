<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="User Management"/><c:set var="activeNav" value="users"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="row between mb-2">
  <div class="tabs mb-0" style="margin-bottom:0"><a class="${empty roleFilter ? 'active' : ''}" href="${ctx}/admin/users">All</a><a class="${roleFilter == 'ADMIN' ? 'active' : ''}" href="${ctx}/admin/users?role=ADMIN">Admins</a><a class="${roleFilter == 'SHELTER' ? 'active' : ''}" href="${ctx}/admin/users?role=SHELTER">Shelters</a><a class="${roleFilter == 'ADOPTER' ? 'active' : ''}" href="${ctx}/admin/users?role=ADOPTER">Adopters</a></div>
  <button class="btn" type="button" data-modal-open="userCreate">&#10133; Add user</button>
</div>
<div class="card table-wrap"><table class="table">
  <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Joined</th><th>Actions</th></tr></thead>
  <tbody><c:forEach items="${users}" var="u"><c:if test="${empty roleFilter or roleFilter == u.role.name()}"><tr>
    <td><b><c:out value="${u.name}"/></b></td><td><c:out value="${u.email}"/></td><td><c:out value="${u.phone}"/></td>
    <td><span class="badge badge-${fn:toLowerCase(u.role.name())}"><c:out value="${u.roleLabel}"/></span></td>
    <td><span class="badge ${u.active ? 'badge-available' : 'badge-inactive'}">${u.active ? 'Active' : 'Inactive'}</span></td>
    <td><fmt:formatDate value="${u.createdAt}" pattern="d MMM yyyy"/></td>
    <td><div class="actions">
      <button class="btn btn-ghost btn-sm" type="button" data-modal-open="userEdit" data-f-id="${u.id}" data-f-name="${fn:escapeXml(u.name)}" data-f-email="${fn:escapeXml(u.email)}" data-f-phone="${fn:escapeXml(u.phone)}" data-f-role="${u.role.name()}" data-f-active="${u.active}" data-f-password="">Edit</button>
      <c:if test="${u.id != currentUser.id}">
        <form method="post" action="${ctx}/admin/users"><pf:csrf/><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="${u.id}"><button class="btn btn-ghost btn-sm" type="submit">${u.active ? 'Deactivate' : 'Activate'}</button></form>
        <form method="post" action="${ctx}/admin/users" data-confirm="Delete ${fn:escapeXml(u.name)} and all of their data?"><pf:csrf/><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${u.id}"><button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
      </c:if>
    </div></td></tr></c:if></c:forEach></tbody>
</table></div>

<div class="modal-backdrop" id="userCreate"><div class="modal">
  <div class="modal-head"><h3 class="mb-0">Add user</h3><button class="modal-close" type="button" data-modal-close aria-label="Close">&#10005;</button></div>
  <form method="post" action="${ctx}/admin/users"><pf:csrf/><input type="hidden" name="action" value="create">
    <div class="field"><label>Name</label><input name="name" required maxlength="100"></div>
    <div class="field"><label>Email</label><input name="email" type="email" required></div>
    <div class="field"><label>Phone</label><input name="phone" required></div>
    <div class="field"><label>Password</label><input name="password" type="password" required minlength="8"><span class="hint">8+ characters, letters and numbers.</span></div>
    <div class="field"><label>Role</label><select name="role"><option value="ADOPTER">Adopter</option><option value="SHELTER">Shelter</option><option value="ADMIN">Admin</option></select></div>
    <label class="check mb-2"><input type="checkbox" name="active" checked> Account is active</label>
    <button class="btn" type="submit">Create user</button>
  </form>
</div></div>
<div class="modal-backdrop" id="userEdit"><div class="modal">
  <div class="modal-head"><h3 class="mb-0">Edit user</h3><button class="modal-close" type="button" data-modal-close aria-label="Close">&#10005;</button></div>
  <form method="post" action="${ctx}/admin/users"><pf:csrf/><input type="hidden" name="action" value="update"><input type="hidden" name="id">
    <div class="field"><label>Name</label><input name="name" required maxlength="100"></div>
    <div class="field"><label>Email</label><input name="email" type="email" required></div>
    <div class="field"><label>Phone</label><input name="phone" required></div>
    <div class="field"><label>New password</label><input name="password" type="password" minlength="8" autocomplete="new-password"><span class="hint">Leave empty to keep the current password.</span></div>
    <div class="field"><label>Role</label><select name="role"><option value="ADOPTER">Adopter</option><option value="SHELTER">Shelter</option><option value="ADMIN">Admin</option></select></div>
    <label class="check mb-2"><input type="checkbox" name="active"> Account is active</label>
    <button class="btn" type="submit">Save changes</button>
  </form>
</div></div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
