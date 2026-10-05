<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Adoption Applications"/><c:set var="activeNav" value="applications"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:choose>
  <c:when test="${empty applications}"><pf:emptyState title="No applications yet" text="When adopters apply for your pets you will review them here."/></c:when>
  <c:otherwise><c:forEach items="${applications}" var="a"><div class="card app-card">
    <div class="app-head">
      <pf:petImage url="${a.petImage}" alt="${a.petName}" cssClass="thumb"/>
      <div style="flex:1;min-width:200px"><h3 class="mb-0"><c:out value="${a.applicantName}"/> <span class="muted" style="font-weight:600">wants to adopt</span> <c:out value="${a.petName}"/></h3>
        <span class="small muted">PF-<fmt:formatNumber value="${a.id}" pattern="0000"/> &middot; <fmt:formatDate value="${a.applicationDate}" pattern="d MMM yyyy, h:mm a"/></span></div>
      <pf:badge value="${a.status}"/>
    </div>
    <dl class="kv mt-2" style="grid-template-columns:auto 1fr;text-align:left">
      <dt>Contact</dt><dd style="text-align:left"><c:out value="${a.phone}"/> &middot; <c:out value="${a.email}"/></dd>
      <dt>Address</dt><dd style="text-align:left"><c:out value="${a.address}"/></dd>
      <dt>Home</dt><dd style="text-align:left"><c:out value="${a.homeType}"/> &middot; other pets: ${a.hasOtherPets ? 'yes' : 'no'}</dd>
      <dt>Experience</dt><dd style="text-align:left"><c:out value="${empty a.experience ? 'Not provided' : a.experience}"/></dd>
      <dt>Reason</dt><dd style="text-align:left;font-weight:600"><c:out value="${a.reason}"/></dd>
      <c:if test="${not empty a.message}"><dt>Message</dt><dd style="text-align:left;font-weight:600"><c:out value="${a.message}"/></dd></c:if>
    </dl>
    <div class="actions mt-2">
      <c:if test="${a.status == 'PENDING'}">
        <form method="post" action="${ctx}/shelter/applications" data-confirm="Approve this application? The pet will be marked as adopted and other pending applications will be closed."><pf:csrf/><input type="hidden" name="action" value="approve"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-sm" type="submit">&#10004; Approve</button></form>
        <form method="post" action="${ctx}/shelter/applications" data-confirm="Reject this application?"><pf:csrf/><input type="hidden" name="action" value="reject"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-danger btn-sm" type="submit">Reject</button></form>
      </c:if>
      <c:if test="${a.status == 'APPROVED'}">
        <form method="post" action="${ctx}/shelter/applications" data-confirm="Mark the hand-over as completed?"><pf:csrf/><input type="hidden" name="action" value="complete"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-peach btn-sm" type="submit">&#127969; Mark completed</button></form>
      </c:if>
      <a class="btn btn-ghost btn-sm" href="${ctx}/messages?to=${a.adopterId}">&#128172; Message adopter</a>
    </div>
  </div></c:forEach></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
