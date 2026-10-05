<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="${pet.name} - ${pet.breed}"/><c:set var="activeNav" value="pets"/>
<c:set var="canHeart" value="${empty currentUser or currentUser.role.name() == 'ADOPTER'}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px">
  <div class="container">
    <p><a href="${ctx}/pets">&larr; Back to all pets</a></p>
    <div class="detail-grid">
      <div class="detail-img"><pf:petImage url="${pet.imageUrl}" alt="${pet.name}, ${pet.breed}"/></div>
      <div>
        <div class="row"><pf:badge value="${pet.status}"/><span class="muted small">Listed <fmt:formatDate value="${pet.createdAt}" pattern="d MMM yyyy"/></span></div>
        <h1 style="margin:10px 0 2px">&#128062; <c:out value="${pet.name}"/></h1>
        <div class="pet-breed" style="font-size:1.1rem"><c:out value="${pet.species}"/> &middot; <c:out value="${pet.breed}"/></div>
        <div class="facts">
          <div class="fact"><span>Age</span><b><c:out value="${pet.ageLabel}"/></b></div>
          <div class="fact"><span>Gender</span><b><c:out value="${pet.gender}"/></b></div>
          <div class="fact"><span>Location</span><b><c:out value="${pet.location}"/></b></div>
          <div class="fact"><span>Status</span><b><c:out value="${pet.status}"/></b></div>
        </div>
        <h3>About <c:out value="${pet.name}"/></h3>
        <p><c:out value="${pet.description}"/></p>
        <div class="shelter-box">
          <b>&#127968; Shelter: <c:out value="${pet.shelterName}"/></b>
          <div class="small muted" style="margin-top:4px">&#9993; <c:out value="${pet.shelterEmail}"/> &nbsp; &#128222; <c:out value="${pet.shelterPhone}"/></div>
        </div>
        <div class="row mt-3">
          <c:choose>
            <c:when test="${pet.available}"><a class="btn btn-peach" href="${ctx}/adopter/apply?petId=${pet.id}">Apply for Adoption &#10084;</a></c:when>
            <c:when test="${pet.status == 'ADOPTED'}"><span class="badge badge-adopted" style="font-size:.95rem;padding:8px 18px">&#127881; Already adopted</span></c:when>
            <c:otherwise><span class="badge badge-pending" style="font-size:.95rem;padding:8px 18px">Waiting for admin approval - only visible to you</span></c:otherwise>
          </c:choose>
          <c:if test="${currentUser.role.name() == 'ADOPTER'}"><c:url var="msgUrl" value="/messages"><c:param name="to" value="${pet.shelterId}"/><c:param name="text" value="Hi! I have a question about ${pet.name}."/></c:url><a class="btn btn-ghost" href="${msgUrl}">&#128172; Message shelter</a></c:if>
          <c:if test="${currentUser.role.name() == 'SHELTER' and currentUser.id == pet.shelterId}"><a class="btn btn-ghost" href="${ctx}/shelter/pets/edit?id=${pet.id}">Edit listing</a></c:if>
          <c:if test="${canHeart and pet.available}">
            <button type="button" class="heart-btn ${not empty favoriteIds and favoriteIds.contains(pet.id) ? 'on' : ''}" data-pet-id="${pet.id}" style="position:static" aria-label="Favorite">&#10084;</button>
          </c:if>
        </div>
        <c:if test="${empty currentUser and pet.available}"><p class="muted small mt-2">You will be asked to log in or register before applying.</p></c:if>
      </div>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
