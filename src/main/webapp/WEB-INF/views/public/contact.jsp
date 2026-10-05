<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Contact"/><c:set var="activeNav" value="contact"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px"><div class="container" style="max-width:900px">
  <h1>Contact us</h1><p class="muted">Questions about adopting or listing a pet? Write to the PetFeet team.</p>
  <div class="grid grid-2 mt-2">
    <form class="card card-pad" method="post" action="${ctx}/contact">
      <pf:csrf/>
      <div class="field"><label for="cname">Your name</label><input id="cname" name="name" required maxlength="100" value="<c:out value='${currentUser.name}'/>"></div>
      <div class="field"><label for="cemail">Email</label><input id="cemail" name="email" type="email" required value="<c:out value='${currentUser.email}'/>"></div>
      <div class="field"><label for="cmsg">Message</label><textarea id="cmsg" name="message" required maxlength="500"></textarea></div>
      <button class="btn" type="submit">Send message</button>
    </form>
    <div class="card card-pad"><h3>Get in touch</h3>
      <p>&#9993; <c:out value="${contactEmail}"/></p><p>&#128205; Knowledge Park III, Greater Noida</p>
      <p class="muted small mb-0">Messages sent here arrive as notifications in the admin dashboard.</p></div>
  </div>
</div></section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
