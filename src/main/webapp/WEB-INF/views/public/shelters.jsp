<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Shelters"/><c:set var="activeNav" value="shelters"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px"><div class="container">
  <div class="section-head"><h1 style="font-size:2.3rem">Our partner shelters</h1><p>Every shelter on PetFeet is registered and its listings are reviewed by our team.</p></div>
  <c:choose>
    <c:when test="${empty shelters}"><pf:emptyState title="No shelters yet" text="Be the first to register your shelter or rescue." actionUrl="/register?role=SHELTER" actionLabel="Register a shelter"/></c:when>
    <c:otherwise><div class="grid grid-3">
      <c:forEach items="${shelters}" var="s"><div class="card card-pad">
        <div class="row"><div class="avatar" style="width:54px;height:54px;background:var(--peach)">&#127968;</div><div><h3 class="mb-0"><c:out value="${s.name}"/></h3><span class="muted small"><c:out value="${empty s.city ? 'India' : s.city}"/></span></div></div>
        <p class="mt-2 mb-0">&#128062; <b>${empty petCounts[s.id] ? 0 : petCounts[s.id]}</b> pets available</p>
        <p class="small muted">&#9993; <c:out value="${s.email}"/><br>&#128222; <c:out value="${s.phone}"/></p>
        <c:if test="${currentUser.role.name() == 'ADOPTER'}"><a class="btn btn-ghost btn-sm" href="${ctx}/messages?to=${s.id}">&#128172; Message</a></c:if>
      </div></c:forEach></div></c:otherwise>
  </c:choose>
</div></section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
