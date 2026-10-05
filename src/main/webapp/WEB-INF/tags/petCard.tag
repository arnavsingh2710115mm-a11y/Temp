<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="pf" tagdir="/WEB-INF/tags" %>
<%@ attribute name="pet" required="true" type="com.petfeet.model.Pet" %>
<%@ attribute name="favorites" required="false" type="java.util.Set" %>
<%@ attribute name="hearts" required="false" type="java.lang.Boolean" %>
<%@ attribute name="compare" required="false" type="java.lang.Boolean" %>
<%@ attribute name="removeOnUnfavorite" required="false" type="java.lang.Boolean" %>
<%@ attribute name="note" required="false" type="java.lang.String" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}"/>
<article class="pet-card">
  <a class="pet-img-wrap" href="${ctxPath}/pet?id=${pet.id}" aria-label="View ${fn:escapeXml(pet.name)}">
    <pf:petImage url="${pet.imageUrl}" alt="${pet.name}, ${pet.breed}"/>
  </a>
  <span class="pet-status"><pf:badge value="${pet.status}"/></span>
  <c:if test="${hearts}">
    <button type="button" class="heart-btn ${not empty favorites and favorites.contains(pet.id) ? 'on' : ''}" data-pet-id="${pet.id}" ${removeOnUnfavorite ? 'data-remove-card' : ''} aria-label="Favorite ${fn:escapeXml(pet.name)}" title="Save to favorites">&#10084;</button>
  </c:if>
  <div class="pet-body">
    <h3 class="pet-title">&#128062; <c:out value="${pet.name}"/></h3>
    <span class="pet-breed"><c:out value="${pet.breed}"/></span>
    <div class="chips"><span class="chip"><c:out value="${pet.ageLabel}"/></span><span class="chip"><c:out value="${pet.gender}"/></span><span class="chip">&#128205; <c:out value="${pet.location}"/></span></div>
    <p class="pet-blurb"><c:choose>
      <c:when test="${not empty note}"><c:out value="${note}"/></c:when>
      <c:when test="${fn:length(pet.description) gt 82}"><c:out value="${fn:substring(pet.description, 0, 80)}"/>&hellip;</c:when>
      <c:otherwise><c:out value="${pet.description}"/></c:otherwise></c:choose></p>
    <div class="pet-actions">
      <a class="btn btn-ghost" href="${ctxPath}/pet?id=${pet.id}">View Details</a>
      <c:if test="${pet.available}"><a class="btn btn-peach" href="${ctxPath}/adopter/apply?petId=${pet.id}">Adopt Me &#10084;</a></c:if>
    </div>
    <c:if test="${compare}"><label class="compare-check"><input type="checkbox" data-compare-id="${pet.id}"> Compare</label></c:if>
  </div>
</article>
