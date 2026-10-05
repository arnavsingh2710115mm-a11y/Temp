<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Activity Logs"/><c:set var="activeNav" value="logs"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:choose>
  <c:when test="${empty logs}"><pf:emptyState title="No activity yet" text="Logins, listings and adoptions will be recorded here."/></c:when>
  <c:otherwise><div class="card table-wrap"><table class="table"><thead><tr><th>When</th><th>User</th><th>Activity</th></tr></thead>
    <tbody><c:forEach items="${logs}" var="l"><tr><td style="white-space:nowrap"><fmt:formatDate value="${l.createdAt}" pattern="d MMM yyyy, h:mm:ss a"/></td><td><c:out value="${l.userName}"/></td><td><c:out value="${l.action}"/></td></tr></c:forEach></tbody></table></div>
  <p class="muted small mt-1">Showing the latest 200 entries.</p></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
