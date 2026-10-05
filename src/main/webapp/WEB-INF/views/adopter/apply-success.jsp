<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Application sent"/><c:set var="activeNav" value="applications"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="card card-pad center" style="max-width:560px;margin:30px auto">
  <div class="success-burst"><svg viewBox="0 0 64 64"><path d="M16 34 l12 12 l22 -26"/></svg></div>
  <h2>Application submitted successfully!</h2>
  <p>Your application for <b><c:out value="${application.petName}"/></b> is with <c:out value="${application.shelterName}"/>.</p>
  <p style="font-size:1.1rem">Application ID: <b>PF-<fmt:formatNumber value="${application.id}" pattern="0000"/></b></p>
  <p class="muted small">We will notify you as soon as the shelter responds.</p>
  <div class="row" style="justify-content:center"><a class="btn" href="${ctx}/adopter/applications">Track my application</a><a class="btn btn-ghost" href="${ctx}/pets">Keep browsing</a></div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
