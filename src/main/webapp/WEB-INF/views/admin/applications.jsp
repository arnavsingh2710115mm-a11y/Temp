<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Adoption Management"/><c:set var="activeNav" value="applications"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="tabs"><a class="${empty statusFilter ? 'active' : ''}" href="${ctx}/admin/applications">All</a><a class="${statusFilter == 'PENDING' ? 'active' : ''}" href="${ctx}/admin/applications?status=PENDING">Pending</a><a class="${statusFilter == 'APPROVED' ? 'active' : ''}" href="${ctx}/admin/applications?status=APPROVED">Approved</a><a class="${statusFilter == 'COMPLETED' ? 'active' : ''}" href="${ctx}/admin/applications?status=COMPLETED">Completed</a><a class="${statusFilter == 'REJECTED' ? 'active' : ''}" href="${ctx}/admin/applications?status=REJECTED">Rejected</a></div>
<div class="card table-wrap"><table class="table">
  <thead><tr><th>ID</th><th>Pet</th><th>Applicant</th><th>Shelter</th><th>Home</th><th>Date</th><th>Status</th></tr></thead>
  <tbody><c:set var="shown" value="0"/><c:forEach items="${applications}" var="a"><c:if test="${empty statusFilter or statusFilter == a.status}"><c:set var="shown" value="${shown + 1}"/><tr>
    <td>PF-<fmt:formatNumber value="${a.id}" pattern="0000"/></td><td><b><c:out value="${a.petName}"/></b></td>
    <td><c:out value="${a.applicantName}"/><div class="small muted"><c:out value="${a.email}"/></div></td><td><c:out value="${a.shelterName}"/></td>
    <td><c:out value="${a.homeType}"/></td><td><fmt:formatDate value="${a.applicationDate}" pattern="d MMM yyyy"/></td><td><pf:badge value="${a.status}"/></td></tr></c:if></c:forEach></tbody>
</table></div>
<c:if test="${shown == 0}"><div class="mt-2"><pf:emptyState title="No applications here" text="Nothing matches this filter."/></div></c:if>
<p class="muted small mt-2">Applications are approved or rejected by the shelter that owns the pet.</p>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
