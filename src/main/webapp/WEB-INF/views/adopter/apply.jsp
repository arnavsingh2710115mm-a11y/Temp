<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Adopt ${pet.name}"/><c:set var="activeNav" value="find"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="grid" style="grid-template-columns:minmax(0,2fr) minmax(250px,1fr);align-items:start">
  <form class="card card-pad" method="post" action="${ctx}/adopter/apply">
    <pf:csrf/><input type="hidden" name="petId" value="${pet.id}">
    <h3>Adoption application</h3>
    <div class="form-grid">
      <div class="field"><label for="applicantName">Applicant name</label><input id="applicantName" name="applicantName" required maxlength="100" value="<c:out value='${form_applicantName}'/>"></div>
      <div class="field"><label for="phone">Phone</label><input id="phone" name="phone" type="tel" required value="<c:out value='${form_phone}'/>"></div>
      <div class="field"><label for="email">Email</label><input id="email" name="email" type="email" required value="<c:out value='${form_email}'/>"></div>
      <div class="field"><label for="homeType">Home type</label><select id="homeType" name="homeType" required>
        <c:forEach items="${['Apartment','House with garden','House without garden','Farm / large property']}" var="h"><option ${form_homeType == h ? 'selected' : ''}>${h}</option></c:forEach></select></div>
      <div class="field full"><label for="address">Address</label><input id="address" name="address" required maxlength="255" value="<c:out value='${form_address}'/>"></div>
      <div class="field full"><label for="reason">Why do you want to adopt <c:out value="${pet.name}"/>?</label><textarea id="reason" name="reason" required maxlength="1000"><c:out value="${form_reason}"/></textarea></div>
      <div class="field full"><label for="experience">Previous pet experience</label><input id="experience" name="experience" maxlength="255" placeholder="e.g. Had a dog for 8 years" value="<c:out value='${form_experience}'/>"></div>
      <div class="field full"><label class="check"><input type="checkbox" name="hasOtherPets" ${form_hasOtherPets ? 'checked' : ''}> I have other pets at home</label></div>
      <div class="field full"><label for="message">Message to the shelter (optional)</label><textarea id="message" name="message" maxlength="1000"><c:out value="${form_message}"/></textarea></div>
    </div>
    <button class="btn btn-peach" type="submit">Submit application &#10084;</button>
  </form>
  <div class="card card-pad">
    <div class="pet-img-wrap" style="border-radius:16px"><pf:petImage url="${pet.imageUrl}" alt="${pet.name}"/></div>
    <h3 class="mt-2 mb-0">&#128062; <c:out value="${pet.name}"/></h3><div class="pet-breed"><c:out value="${pet.breed}"/></div>
    <div class="chips"><span class="chip"><c:out value="${pet.ageLabel}"/></span><span class="chip"><c:out value="${pet.gender}"/></span><span class="chip"><c:out value="${pet.location}"/></span></div>
    <p class="small muted mb-0">Shelter: <c:out value="${pet.shelterName}"/></p>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
