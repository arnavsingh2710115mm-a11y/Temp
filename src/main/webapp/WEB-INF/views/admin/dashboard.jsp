<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Admin Dashboard"/><c:set var="activeNav" value="overview"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="grid grid-3">
  <div class="stat"><div class="ico">&#128101;</div><div><b>${stats.totalUsers}</b><span>Total users</span></div></div>
  <div class="stat peach"><div class="ico">&#128062;</div><div><b>${stats.totalPets}</b><span>Total pets</span></div></div>
  <div class="stat cream"><div class="ico">&#127881;</div><div><b>${stats.availablePets}</b><span>Available pets</span></div></div>
  <div class="stat sky"><div class="ico">&#9203;</div><div><b>${stats.pendingApplications}</b><span>Pending applications</span></div></div>
  <div class="stat lilac"><div class="ico">&#10084;</div><div><b>${stats.successfulAdoptions}</b><span>Successful adoptions</span></div></div>
  <div class="stat"><div class="ico">&#127968;</div><div><b>${stats.activeShelters}</b><span>Active shelters</span></div></div>
</div>
<c:if test="${stats.pendingPets gt 0}"><div class="alert alert-info mt-3">&#128276; ${stats.pendingPets} pet listing<c:if test="${stats.pendingPets != 1}">s are</c:if><c:if test="${stats.pendingPets == 1}"> is</c:if> waiting for approval. <a href="${ctx}/admin/pets?status=PENDING">Review now</a></div></c:if>
<div class="grid grid-3 mt-3">
  <div class="card card-pad"><h3>Users by role</h3><pf:chart type="donut" data="${stats.usersByRole}"/></div>
  <div class="card card-pad"><h3>Pets by status</h3><pf:chart type="bar" data="${stats.petsByStatus}"/></div>
  <div class="card card-pad"><h3>Applications by status</h3><pf:chart type="donut" data="${stats.applicationsByStatus}"/></div>
</div>
<div class="grid grid-2 mt-3" style="align-items:start">
  <div class="card card-pad">
    <h3>Background services</h3>
    <p class="muted small">Two Java threads run beside the web server (see <a href="${ctx}/about-project">Java Concepts</a>).</p>
    <dl class="kv">
      <dt>Notification service</dt><dd><span class="status-live ${bgStatus.notificationRunning ? '' : 'off'}">${bgStatus.notificationRunning ? 'Running' : 'Stopped'}</span></dd>
      <dt>Notifications delivered</dt><dd>${bgStatus.notificationsProcessed} (queue: ${bgQueueSize})</dd>
      <dt>Last notification task</dt><dd><c:choose><c:when test="${empty bgStatus.lastNotificationRun}">none yet</c:when><c:otherwise><fmt:formatDate value="${bgStatus.lastNotificationRun}" pattern="d MMM, HH:mm:ss"/></c:otherwise></c:choose></dd>
      <dt>Statistics service</dt><dd><span class="status-live ${bgStatus.statisticsRunning ? '' : 'off'}">${bgStatus.statisticsRunning ? 'Running' : 'Stopped'}</span></dd>
      <dt>Statistics refreshes</dt><dd>${bgStatus.statisticsRuns} (every 15 s)</dd>
      <dt>Last statistics update</dt><dd><c:choose><c:when test="${empty bgStatus.lastStatisticsRun}">none yet</c:when><c:otherwise><fmt:formatDate value="${bgStatus.lastStatisticsRun}" pattern="d MMM, HH:mm:ss"/></c:otherwise></c:choose></dd>
      <dt>Cached snapshot</dt><dd>${empty bgSnapshot.totalUsers ? 0 : bgSnapshot.totalUsers} users / ${empty bgSnapshot.totalPets ? 0 : bgSnapshot.totalPets} pets</dd>
      <dt>Web requests served</dt><dd>${totalRequests}</dd>
    </dl>
    <c:if test="${not empty bgStatus.lastError}"><div class="alert alert-error mt-2 mb-0"><c:out value="${bgStatus.lastError}"/></div></c:if>
  </div>
  <div class="card card-pad">
    <div class="row between"><h3 class="mb-0">Recent activity</h3><a class="btn btn-ghost btn-sm" href="${ctx}/admin/logs">All logs</a></div>
    <c:forEach items="${recentLogs}" var="l"><div style="padding:10px 0;border-top:1px solid var(--line)" class="small"><b><c:out value="${l.userName}"/></b> - <c:out value="${l.action}"/><div class="muted"><fmt:formatDate value="${l.createdAt}" pattern="d MMM, h:mm a"/></div></div></c:forEach>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
