<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="text" required="true" type="java.lang.String" %>
<%@ attribute name="actionUrl" required="false" type="java.lang.String" %>
<%@ attribute name="actionLabel" required="false" type="java.lang.String" %>
<div class="empty">
  <img src="${pageContext.request.contextPath}/images/empty.svg" alt="">
  <h3><c:out value="${title}"/></h3>
  <p><c:out value="${text}"/></p>
  <c:if test="${not empty actionUrl}"><a class="btn" href="${pageContext.request.contextPath}${actionUrl}"><c:out value="${actionLabel}"/></a></c:if>
</div>
