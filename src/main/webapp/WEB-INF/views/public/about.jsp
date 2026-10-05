<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="About Us"/><c:set var="activeNav" value="about"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="section" style="padding-top:44px"><div class="container" style="max-width:860px">
  <h1>About PetFeet</h1>
  <p class="lead muted" style="font-size:1.15rem">PetFeet is an online pet adoption platform built on one belief: every paw deserves a home.</p>
  <div class="grid grid-3 mt-3">
    <div class="card card-pad"><div class="step-icon">&#128054;</div><h3>For adopters</h3><p class="muted mb-0">Browse, compare and save pets, apply online and follow each step of your application.</p></div>
    <div class="card card-pad"><div class="step-icon" style="background:var(--mint-soft)">&#127968;</div><h3>For shelters</h3><p class="muted mb-0">List pets, review applications, and talk with adopters without sharing personal phone numbers first.</p></div>
    <div class="card card-pad"><div class="step-icon" style="background:var(--cream)">&#9989;</div><h3>Trusted listings</h3><p class="muted mb-0">Every new listing is reviewed by an admin before it appears publicly.</p></div>
  </div>
  <div class="cta-band mt-3"><div><h2 class="mb-0">Ready to meet your match?</h2></div><a class="btn" href="${ctx}/pets">Find Your Pet</a></div>
  <p class="muted small mt-3">PetFeet is a college Java web project. See the <a href="${ctx}/about-project">About Project / Java Concepts</a> page for the technical details.</p>
</div></section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
