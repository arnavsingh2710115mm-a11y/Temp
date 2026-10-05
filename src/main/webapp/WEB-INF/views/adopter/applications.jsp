<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My Applications"/><c:set var="activeNav" value="applications"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:choose>
  <c:when test="${empty applications}"><pf:emptyState title="No applications yet" text="When you apply for a pet you can follow every step here." actionUrl="/pets" actionLabel="Find a Pet"/></c:when>
  <c:otherwise><c:forEach items="${applications}" var="a"><div class="card app-card">
    <div class="app-head">
      <pf:petImage url="${a.petImage}" alt="${a.petName}" cssClass="thumb"/>
      <div style="flex:1;min-width:200px"><h3 class="mb-0">&#128062; <c:out value="${a.petName}"/></h3>
        <span class="small muted">Application <b>PF-<fmt:formatNumber value="${a.id}" pattern="0000"/></b> &middot; <fmt:formatDate value="${a.applicationDate}" pattern="d MMM yyyy"/> &middot; <c:out value="${a.shelterName}"/></span></div>
      <pf:badge value="${a.status}"/>
    </div>
    <pf:tracker app="${a}"/>
    <div class="actions mt-1">
      <a class="btn btn-ghost btn-sm" href="${ctx}/pet?id=${a.petId}">View pet</a>
      <a class="btn btn-ghost btn-sm" href="${ctx}/messages?to=${a.shelterId}">&#128172; Message shelter</a>
    </div>
  </div></c:forEach></c:otherwise>
</c:choose>
<h2 class="mt-3">Adoption history</h2>
<c:choose>
  <c:when test="${empty history}"><p class="muted">Your adopted pets will appear here.</p></c:when>
  <c:otherwise><div class="grid grid-3"><c:forEach items="${history}" var="h"><div class="card card-pad row">
    <pf:petImage url="${h.petImage}" alt="${h.petName}" cssClass="thumb"/>
    <div><b>&#10084; <c:out value="${h.petName}"/></b><div class="small muted"><c:out value="${h.petBreed}"/> &middot; from <c:out value="${h.shelterName}"/><br>Adopted <fmt:formatDate value="${h.adoptedOn}" pattern="d MMM yyyy"/></div></div>
  </div></c:forEach></div></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
