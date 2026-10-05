<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Shelter Dashboard"/><c:set var="activeNav" value="overview"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="grid grid-4">
  <div class="stat"><div class="ico">&#128062;</div><div><b>${stats.listedPets}</b><span>Listed pets</span></div></div>
  <div class="stat cream"><div class="ico">&#128221;</div><div><b>${stats.applicationsReceived}</b><span>Applications received</span></div></div>
  <div class="stat peach"><div class="ico">&#9203;</div><div><b>${stats.pendingApplications}</b><span>Awaiting your review</span></div></div>
  <div class="stat sky"><div class="ico">&#9989;</div><div><b>${stats.approvedApplications}</b><span>Approved</span></div></div>
  <div class="stat lilac"><div class="ico">&#127969;</div><div><b>${stats.adoptionRate}%</b><span>Adoption rate</span></div></div>
</div>
<div class="grid mt-3" style="grid-template-columns:minmax(0,1fr) minmax(0,1.4fr);align-items:start">
  <div class="card card-pad"><h3>Applications by status</h3>
    <c:choose><c:when test="${stats.applicationsReceived == 0}"><p class="muted">No applications yet.</p></c:when><c:otherwise><pf:chart type="donut" data="${stats.applicationsByStatus}"/></c:otherwise></c:choose></div>
  <div class="card card-pad"><div class="row between"><h3>Latest applications</h3><a class="btn btn-ghost btn-sm" href="${ctx}/shelter/applications">View all</a></div>
    <c:choose>
      <c:when test="${empty recentApplications}"><pf:emptyState title="Nothing to review" text="Applications for your pets will appear here."/></c:when>
      <c:otherwise><c:forEach items="${recentApplications}" var="a"><div class="row between" style="padding:10px 0;border-top:1px solid var(--line)">
        <div><b><c:out value="${a.applicantName}"/></b> <span class="muted small">for <c:out value="${a.petName}"/></span></div><pf:badge value="${a.status}"/></div></c:forEach></c:otherwise>
    </c:choose></div>
</div>
<div class="row between mt-3"><h2 class="mb-0">My pets</h2><a class="btn" href="${ctx}/shelter/pets/add">&#10133; Add a pet</a></div>
<c:choose>
  <c:when test="${empty pets}"><div class="mt-2"><pf:emptyState title="You have not listed any pets" text="Add your first pet and it will go live after admin approval." actionUrl="/shelter/pets/add" actionLabel="Add a pet"/></div></c:when>
  <c:otherwise><div class="grid grid-pets mt-2"><c:forEach items="${pets}" var="p" end="2"><pf:petCard pet="${p}"/></c:forEach></div><p class="mt-2"><a href="${ctx}/shelter/pets">See all ${fn:length(pets)} pets &rarr;</a></p></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
