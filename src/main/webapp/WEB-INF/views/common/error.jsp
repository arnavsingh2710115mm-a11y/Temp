<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<%@ page isErrorPage="true" %>
<c:set var="pageTitle" value="Oops!"/><c:set var="noFlash" value="true"/>
<c:set var="status" value="${not empty errorStatus ? errorStatus : pageContext.errorData.statusCode}"/>
<c:choose>
  <c:when test="${not empty errorMessage}"><c:set var="msg" value="${errorMessage}"/></c:when>
  <c:when test="${status == 403}"><c:set var="msg" value="You do not have access to this area. Please log in with the right account."/></c:when>
  <c:when test="${status == 404}"><c:set var="msg" value="We could not find the page you were looking for."/></c:when>
  <c:otherwise><c:set var="msg" value="Something went wrong on our side. Please try again in a moment."/></c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section"><div class="container center" style="max-width:560px">
  <img src="${ctx}/images/empty.svg" alt="" style="width:160px;margin:0 auto 12px">
  <h1 style="font-size:2.4rem">Oops! This paw went missing.</h1>
  <p class="muted"><c:out value="${msg}"/></p>
  <c:if test="${not empty status and status != 0}"><p class="small muted">Error code: <c:out value="${status}"/></p></c:if>
  <div class="row" style="justify-content:center"><a class="btn" href="${ctx}/home">Back to home</a><a class="btn btn-ghost" href="${ctx}/pets">Find a Pet</a>
  <c:if test="${status == 403 and empty currentUser}"><a class="btn btn-peach" href="${ctx}/login">Log in</a></c:if></div>
</div></section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
