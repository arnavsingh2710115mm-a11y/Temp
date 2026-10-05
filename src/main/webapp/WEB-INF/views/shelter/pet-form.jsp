<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="${editing ? 'Edit pet' : 'Add a pet'}"/><c:set var="activeNav" value="${editing ? 'pets' : 'add'}"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:set var="formUrl" value="${ctx}/shelter/pets/${editing ? 'edit' : 'add'}?_csrf=${csrfToken}"/>
<form class="card card-pad" method="post" action="${formUrl}" enctype="multipart/form-data" style="max-width:860px">
  <c:if test="${editing}"><input type="hidden" name="id" value="${pet.id}"></c:if>
  <div class="form-grid">
    <div class="field"><label for="name">Pet name</label><input id="name" name="name" required maxlength="80" value="<c:out value='${pet.name}'/>"></div>
    <div class="field"><label for="species">Species</label><input id="species" name="species" required maxlength="40" list="speciesList" value="<c:out value='${pet.species}'/>"><datalist id="speciesList"><option>Dog</option><option>Cat</option><option>Rabbit</option><option>Bird</option></datalist></div>
    <div class="field"><label for="breed">Breed</label><input id="breed" name="breed" required maxlength="80" value="<c:out value='${pet.breed}'/>"></div>
    <div class="field"><label for="age">Age (years, 0 = under 1 year)</label><input id="age" name="age" type="number" min="0" max="30" required value="${empty pet.name ? '' : pet.age}"></div>
    <div class="field"><label for="gender">Gender</label><select id="gender" name="gender"><option ${pet.gender == 'Male' ? 'selected' : ''}>Male</option><option ${pet.gender == 'Female' ? 'selected' : ''}>Female</option></select></div>
    <div class="field"><label for="location">Location</label><input id="location" name="location" required maxlength="100" value="<c:out value='${pet.location}'/>"></div>
    <div class="field full"><label for="description">Description</label><textarea id="description" name="description" maxlength="2000" placeholder="Personality, health, vaccinations, what kind of home suits them..."><c:out value="${pet.description}"/></textarea></div>
    <div class="field"><label for="imageFile">Upload photo</label><input id="imageFile" name="imageFile" type="file" accept="image/*"><span class="hint">JPG, PNG, GIF or WEBP, max ${applicationScope.maxPetImageMB} MB.</span></div>
    <div class="field"><label for="imageUrl">...or image URL</label><input id="imageUrl" name="imageUrl" maxlength="255" placeholder="https://..." value="${fn:startsWith(pet.imageUrl, 'http') ? fn:escapeXml(pet.imageUrl) : ''}"><span class="hint">Leave both empty to use a default picture.</span></div>
    <c:if test="${editing and not empty pet.imageUrl}"><div class="field full"><label>Current photo</label><div style="width:160px"><pf:petImage url="${pet.imageUrl}" alt="${pet.name}" cssClass="thumb" /></div></div></c:if>
    <div class="field full"><label for="status">Adoption status</label>
      <c:choose>
        <c:when test="${editing and (pet.status == 'AVAILABLE' or pet.status == 'ADOPTED')}"><select id="status" name="status"><option value="AVAILABLE" ${pet.status == 'AVAILABLE' ? 'selected' : ''}>Available for adoption</option><option value="ADOPTED" ${pet.status == 'ADOPTED' ? 'selected' : ''}>Adopted</option></select></c:when>
        <c:when test="${editing and pet.status == 'REJECTED'}"><input value="Rejected - saving will send it for review again" disabled></c:when>
        <c:when test="${editing}"><input value="Pending admin approval" disabled></c:when>
        <c:otherwise><input value="New listings are reviewed by an admin before going live" disabled></c:otherwise>
      </c:choose></div>
  </div>
  <div class="row"><button class="btn" type="submit">${editing ? 'Save changes' : 'Add pet'}</button><a class="btn btn-ghost" href="${ctx}/shelter/pets">Cancel</a></div>
</form>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
