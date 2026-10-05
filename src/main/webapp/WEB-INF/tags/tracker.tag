<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="app" required="true" type="com.petfeet.model.AdoptionApplication" %>
<c:set var="cur" value="${app.progressStep}"/>
<ol class="tracker">
<c:forEach items="${['Application submitted','Under review','Shelter response','Approved','Adopted &#10084;']}" var="label" varStatus="st">
  <c:set var="cls" value=""/>
  <c:choose>
    <c:when test="${app.status == 'COMPLETED'}"><c:set var="cls" value="done"/></c:when>
    <c:when test="${app.status == 'REJECTED' and st.index == 2}"><c:set var="cls" value="bad"/></c:when>
    <c:when test="${st.index lt cur}"><c:set var="cls" value="done"/></c:when>
    <c:when test="${st.index == cur and app.status != 'REJECTED'}"><c:set var="cls" value="active"/></c:when>
  </c:choose>
  <li class="${cls}"><c:choose><c:when test="${app.status == 'REJECTED' and st.index == 2}">Not approved</c:when><c:otherwise>${label}</c:otherwise></c:choose></li>
</c:forEach>
</ol>
