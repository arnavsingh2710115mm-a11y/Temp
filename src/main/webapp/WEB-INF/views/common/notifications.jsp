<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Notifications"/><c:set var="activeNav" value="notifications"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="row between mb-2"><p class="muted mb-0">${unreadNotifications} unread</p>
  <c:if test="${unreadNotifications gt 0}"><form method="post" action="${ctx}/notifications"><pf:csrf/><input type="hidden" name="action" value="readAll"><button class="btn btn-ghost btn-sm" type="submit">Mark all as read</button></form></c:if></div>
<c:choose>
  <c:when test="${empty notifications}"><pf:emptyState title="You are all caught up" text="New messages, application updates and shelter news will show up here."/></c:when>
  <c:otherwise><div class="card" style="overflow:hidden">
    <c:forEach items="${notifications}" var="n"><div class="notice ${n.read ? '' : 'unread'}">
      <span>${n.read ? '&#128276;' : '&#128308;'}</span>
      <div class="grow"><c:out value="${n.message}"/><div class="small muted"><fmt:formatDate value="${n.createdAt}" pattern="d MMM yyyy, h:mm a"/></div></div>
      <c:if test="${not n.read}"><form method="post" action="${ctx}/notifications"><pf:csrf/><input type="hidden" name="action" value="read"><input type="hidden" name="id" value="${n.id}"><button class="btn btn-ghost btn-sm" type="submit">Mark read</button></form></c:if>
      <form method="post" action="${ctx}/notifications"><pf:csrf/><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${n.id}"><button class="btn btn-danger btn-sm" type="submit" aria-label="Delete">&#10005;</button></form>
    </div></c:forEach></div></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
