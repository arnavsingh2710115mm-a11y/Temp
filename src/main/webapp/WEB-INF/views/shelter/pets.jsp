<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My Pets"/><c:set var="activeNav" value="pets"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="row between mb-2"><p class="muted mb-0">${fn:length(pets)} listing<c:if test="${fn:length(pets) != 1}">s</c:if></p><a class="btn" href="${ctx}/shelter/pets/add">&#10133; Add a pet</a></div>
<c:choose>
  <c:when test="${empty pets}"><pf:emptyState title="No pets listed yet" text="Add a pet to start receiving adoption applications." actionUrl="/shelter/pets/add" actionLabel="Add a pet"/></c:when>
  <c:otherwise><div class="grid grid-pets"><c:forEach items="${pets}" var="p">
    <article class="pet-card">
      <a class="pet-img-wrap" href="${ctx}/pet?id=${p.id}"><pf:petImage url="${p.imageUrl}" alt="${p.name}"/></a>
      <span class="pet-status"><pf:badge value="${p.status}"/></span>
      <div class="pet-body">
        <h3 class="pet-title">&#128062; <c:out value="${p.name}"/></h3><span class="pet-breed"><c:out value="${p.breed}"/></span>
        <div class="chips"><span class="chip"><c:out value="${p.ageLabel}"/></span><span class="chip"><c:out value="${p.gender}"/></span><span class="chip"><c:out value="${p.location}"/></span></div>
        <p class="pet-blurb small"><c:choose>
          <c:when test="${p.status == 'PENDING'}">Waiting for admin approval.</c:when>
          <c:when test="${p.status == 'REJECTED'}">Not approved. Edit the listing to submit it again.</c:when>
          <c:when test="${p.status == 'ADOPTED'}">Adopted - a happy ending!</c:when>
          <c:otherwise>Live on PetFeet.</c:otherwise></c:choose></p>
        <div class="pet-actions">
          <a class="btn btn-ghost" href="${ctx}/shelter/pets/edit?id=${p.id}">Edit</a>
          <form method="post" action="${ctx}/shelter/pets/delete" data-confirm="Delete ${fn:escapeXml(p.name)}? This also removes its applications." style="flex:1;display:flex"><pf:csrf/><input type="hidden" name="id" value="${p.id}"><button class="btn btn-danger btn-block" type="submit">Delete</button></form>
        </div>
      </div>
    </article></c:forEach></div></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
