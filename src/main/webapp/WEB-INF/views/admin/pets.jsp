<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Pet Listings"/><c:set var="activeNav" value="pets"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="tabs"><a class="${empty statusFilter ? 'active' : ''}" href="${ctx}/admin/pets">All</a><a class="${statusFilter == 'PENDING' ? 'active' : ''}" href="${ctx}/admin/pets?status=PENDING">Pending</a><a class="${statusFilter == 'AVAILABLE' ? 'active' : ''}" href="${ctx}/admin/pets?status=AVAILABLE">Available</a><a class="${statusFilter == 'ADOPTED' ? 'active' : ''}" href="${ctx}/admin/pets?status=ADOPTED">Adopted</a><a class="${statusFilter == 'REJECTED' ? 'active' : ''}" href="${ctx}/admin/pets?status=REJECTED">Rejected</a></div>
<div class="card table-wrap"><table class="table">
  <thead><tr><th>Pet</th><th>Species / breed</th><th>Age</th><th>Shelter</th><th>Status</th><th>Listed</th><th>Actions</th></tr></thead>
  <tbody><c:set var="shown" value="0"/><c:forEach items="${pets}" var="p"><c:if test="${empty statusFilter or statusFilter == p.status}"><c:set var="shown" value="${shown + 1}"/><tr>
    <td><div class="row"><pf:petImage url="${p.imageUrl}" alt="${p.name}" cssClass="thumb"/><b><c:out value="${p.name}"/></b></div></td>
    <td><c:out value="${p.species}"/> / <c:out value="${p.breed}"/></td><td><c:out value="${p.ageLabel}"/></td><td><c:out value="${p.shelterName}"/></td>
    <td><pf:badge value="${p.status}"/></td><td><fmt:formatDate value="${p.createdAt}" pattern="d MMM yyyy"/></td>
    <td><div class="actions">
      <a class="btn btn-ghost btn-sm" href="${ctx}/pet?id=${p.id}">View</a>
      <c:if test="${p.status != 'AVAILABLE' and p.status != 'ADOPTED'}"><form method="post" action="${ctx}/admin/pets"><pf:csrf/><input type="hidden" name="action" value="approve"><input type="hidden" name="id" value="${p.id}"><input type="hidden" name="status" value="${statusFilter}"><button class="btn btn-sm" type="submit">&#10004; Approve</button></form></c:if>
      <c:if test="${p.status == 'PENDING' or p.status == 'AVAILABLE'}"><form method="post" action="${ctx}/admin/pets" data-confirm="Reject listing ${fn:escapeXml(p.name)}?"><pf:csrf/><input type="hidden" name="action" value="reject"><input type="hidden" name="id" value="${p.id}"><input type="hidden" name="status" value="${statusFilter}"><button class="btn btn-danger btn-sm" type="submit">Reject</button></form></c:if>
    </div></td></tr></c:if></c:forEach></tbody>
</table></div>
<c:if test="${shown == 0}"><div class="mt-2"><pf:emptyState title="No listings here" text="Nothing matches this filter."/></div></c:if>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
