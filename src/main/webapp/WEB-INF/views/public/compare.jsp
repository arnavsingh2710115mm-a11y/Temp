<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Compare pets"/><c:set var="activeNav" value="pets"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px">
  <div class="container">
    <p><a href="${ctx}/pets">&larr; Back to all pets</a></p>
    <h1 style="font-size:2.2rem">Compare <c:out value="${first.name}"/> and <c:out value="${second.name}"/></h1>
    <div class="card table-wrap mt-2"><table class="table compare-table">
      <tr><th></th>
        <td><pf:petImage url="${first.imageUrl}" alt="${first.name}"/><h3><c:out value="${first.name}"/></h3></td>
        <td><pf:petImage url="${second.imageUrl}" alt="${second.name}"/><h3><c:out value="${second.name}"/></h3></td></tr>
      <tr><th>Species</th><td><c:out value="${first.species}"/></td><td><c:out value="${second.species}"/></td></tr>
      <tr><th>Breed</th><td><c:out value="${first.breed}"/></td><td><c:out value="${second.breed}"/></td></tr>
      <tr><th>Age</th><td class="${first.age lt second.age ? 'better' : ''}"><c:out value="${first.ageLabel}"/></td><td class="${second.age lt first.age ? 'better' : ''}"><c:out value="${second.ageLabel}"/></td></tr>
      <tr><th>Gender</th><td><c:out value="${first.gender}"/></td><td><c:out value="${second.gender}"/></td></tr>
      <tr><th>Location</th><td><c:out value="${first.location}"/></td><td><c:out value="${second.location}"/></td></tr>
      <tr><th>Shelter</th><td><c:out value="${first.shelterName}"/></td><td><c:out value="${second.shelterName}"/></td></tr>
      <tr><th>Status</th><td><pf:badge value="${first.status}"/></td><td><pf:badge value="${second.status}"/></td></tr>
      <tr><th>About</th><td class="small"><c:out value="${first.description}"/></td><td class="small"><c:out value="${second.description}"/></td></tr>
      <tr><th></th>
        <td><a class="btn btn-ghost btn-sm" href="${ctx}/pet?id=${first.id}">View details</a> <c:if test="${first.available}"><a class="btn btn-peach btn-sm" href="${ctx}/adopter/apply?petId=${first.id}">Adopt Me &#10084;</a></c:if></td>
        <td><a class="btn btn-ghost btn-sm" href="${ctx}/pet?id=${second.id}">View details</a> <c:if test="${second.available}"><a class="btn btn-peach btn-sm" href="${ctx}/adopter/apply?petId=${second.id}">Adopt Me &#10084;</a></c:if></td></tr>
    </table></div>
    <p class="muted small mt-1">Highlighted cells show the younger pet.</p>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
