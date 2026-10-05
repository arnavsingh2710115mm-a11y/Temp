<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="url" required="false" type="java.lang.String" %>
<%@ attribute name="alt" required="false" type="java.lang.String" %>
<%@ attribute name="cssClass" required="false" type="java.lang.String" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}"/>
<c:choose>
  <c:when test="${empty url}"><c:set var="src" value="${ctxPath}/images/pets/dog-indie.svg"/></c:when>
  <c:when test="${fn:startsWith(url, 'http') or fn:startsWith(url, 'data:image/')}"><c:set var="src" value="${url}"/></c:when>
  <c:otherwise><c:set var="src" value="${ctxPath}/${url}"/></c:otherwise>
</c:choose>
<img src="${fn:escapeXml(src)}" alt="${fn:escapeXml(alt)}" class="${cssClass}" loading="lazy" onerror="this.onerror=null;this.src='${ctxPath}/images/pets/dog-indie.svg'">
