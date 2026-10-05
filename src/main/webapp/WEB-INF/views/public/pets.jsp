<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Find a Pet"/><c:set var="activeNav" value="pets"/>
<c:set var="canHeart" value="${empty currentUser or currentUser.role.name() == 'ADOPTER'}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px">
  <div class="container">
    <div class="section-head"><h1 style="font-size:2.3rem">Find your new best friend</h1><p>Search and filter pets from verified shelters.</p></div>
    <form class="filters" method="get" action="${ctx}/pets">
      <div class="form-grid">
        <div class="field suggest-wrap"><label for="q">Search</label><input id="q" name="q" data-suggest autocomplete="off" placeholder="Name, breed, city..." value="<c:out value='${f_q}'/>"></div>
        <div class="field"><label for="species">Species</label><select id="species" name="species"><option value="">Any</option>
          <c:forEach items="${speciesOptions}" var="s"><option value="${fn:escapeXml(s)}" ${f_species == s ? 'selected' : ''}><c:out value="${s}"/></option></c:forEach></select></div>
        <div class="field"><label for="breed">Breed</label><input id="breed" name="breed" placeholder="e.g. Labrador" value="<c:out value='${f_breed}'/>"></div>
        <div class="field"><label for="location">Location</label><input id="location" name="location" list="locList" placeholder="City" value="<c:out value='${f_location}'/>">
          <datalist id="locList"><c:forEach items="${locationOptions}" var="l"><option value="${fn:escapeXml(l)}"></option></c:forEach></datalist></div>
        <div class="field"><label for="maxAge">Max age (yrs)</label><input id="maxAge" name="maxAge" type="number" min="0" max="30" value="<c:out value='${f_maxAge}'/>"></div>
        <div class="field"><label for="gender">Gender</label><select id="gender" name="gender"><option value="">Any</option><option ${f_gender == 'Male' ? 'selected' : ''}>Male</option><option ${f_gender == 'Female' ? 'selected' : ''}>Female</option></select></div>
        <div class="field"><label for="availability">Availability</label><select id="availability" name="availability"><option value="">All listed</option><option value="AVAILABLE" ${f_availability == 'AVAILABLE' ? 'selected' : ''}>Available</option><option value="ADOPTED" ${f_availability == 'ADOPTED' ? 'selected' : ''}>Adopted</option></select></div>
        <div class="row"><button class="btn" type="submit">Search</button><a class="btn btn-ghost" href="${ctx}/pets">Reset</a></div>
      </div>
    </form>
    <p class="muted"><b>${fn:length(pets)}</b> pet<c:if test="${fn:length(pets) != 1}">s</c:if> found &middot; tick <b>Compare</b> on two pets to see them side by side</p>
    <c:choose>
      <c:when test="${empty pets}"><pf:emptyState title="No pets match your search" text="Try removing a filter or searching for a different breed or city." actionUrl="/pets" actionLabel="Clear filters"/></c:when>
      <c:otherwise><div class="grid grid-pets"><c:forEach items="${pets}" var="p"><pf:petCard pet="${p}" favorites="${favoriteIds}" hearts="${canHeart}" compare="true"/></c:forEach></div></c:otherwise>
    </c:choose>
  </div>
</section>
<div class="compare-bar" data-compare-bar><span data-compare-count></span><a class="btn btn-sm" data-compare-go href="#">Compare now</a><button class="btn btn-sm btn-ghost" style="color:#fff;border-color:#8aa" type="button" data-compare-clear>Clear</button></div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
