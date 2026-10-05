<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="type" required="true" type="java.lang.String" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<div class="chart-box"><canvas data-chart="${type}" data-labels="<c:forEach items="${data}" var="e" varStatus="s"><c:out value="${e.key}"/><c:if test="${not s.last}">|</c:if></c:forEach>" data-values="<c:forEach items="${data}" var="e" varStatus="s">${e.value}<c:if test="${not s.last}">|</c:if></c:forEach>"></canvas></div>
<div class="legend"></div>
