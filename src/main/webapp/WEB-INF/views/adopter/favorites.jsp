<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My Favorites"/><c:set var="activeNav" value="favorites"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:choose>
  <c:when test="${empty pets}"><pf:emptyState title="No favorites yet" text="Tap the heart on any pet to save it here." actionUrl="/pets" actionLabel="Browse pets"/></c:when>
  <c:otherwise><div class="grid grid-pets"><c:forEach items="${pets}" var="p"><pf:petCard pet="${p}" favorites="${favoriteIds}" hearts="true" removeOnUnfavorite="true"/></c:forEach></div></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
