<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Welcome, ${currentUser.name}"/><c:set var="activeNav" value="overview"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="grid grid-4">
  <div class="stat"><div class="ico">&#128221;</div><div><b>${stats.submitted}</b><span>Applications</span></div></div>
  <div class="stat cream"><div class="ico">&#9203;</div><div><b>${stats.pending}</b><span>Pending</span></div></div>
  <div class="stat"><div class="ico">&#9989;</div><div><b>${stats.approved}</b><span>Approved</span></div></div>
  <div class="stat peach"><div class="ico">&#10060;</div><div><b>${stats.rejected}</b><span>Rejected</span></div></div>
  <div class="stat sky"><div class="ico">&#127969;</div><div><b>${stats.adoptedPets}</b><span>Adopted pets</span></div></div>
  <div class="stat lilac"><div class="ico">&#10084;</div><div><b>${stats.favorites}</b><span>Favorites</span></div></div>
</div>

<div class="grid mt-3" style="grid-template-columns:minmax(0,2fr) minmax(260px,1fr);align-items:start">
  <div class="card card-pad"><h3>Pets you may love</h3>
    <c:choose>
      <c:when test="${empty recommendations}"><pf:emptyState title="No recommendations yet" text="Set your preferred species in your profile or save a few favorites to get suggestions." actionUrl="/profile" actionLabel="Update preferences"/></c:when>
      <c:otherwise><div class="grid grid-pets"><c:forEach items="${recommendations}" var="r"><pf:petCard pet="${r.pet}" favorites="${favoriteIds}" hearts="true" note="${r.reason}"/></c:forEach></div></c:otherwise>
    </c:choose>
  </div>
  <div class="card card-pad"><h3>My applications</h3>
    <c:choose>
      <c:when test="${stats.submitted == 0}"><p class="muted">You have not applied yet. Find a pet you love and tap <b>Adopt Me</b>.</p><a class="btn btn-sm" href="${ctx}/pets">Browse pets</a></c:when>
      <c:otherwise><pf:chart type="donut" data="${stats.applicationsByStatus}"/>
        <c:forEach items="${recentApplications}" var="a"><div class="row between small" style="padding:8px 0;border-top:1px solid var(--line)"><span>&#128062; <c:out value="${a.petName}"/></span><pf:badge value="${a.status}"/></div></c:forEach>
        <a class="btn btn-ghost btn-sm mt-1" href="${ctx}/adopter/applications">Track all</a></c:otherwise>
    </c:choose>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
