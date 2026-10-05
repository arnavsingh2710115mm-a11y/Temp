<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Find a Paw. Give a Home."/><c:set var="activeNav" value="home"/>
<c:set var="canHeart" value="${empty currentUser or currentUser.role.name() == 'ADOPTER'}"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<section class="hero">
  <div class="paw-trail" aria-hidden="true"><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span><span>&#128062;</span></div>
  <span class="float-paw" style="left:46%;top:12%" aria-hidden="true">&#128062;</span>
  <span class="float-paw" style="left:4%;top:20%;animation-delay:1.5s" aria-hidden="true">&#128062;</span>
  <div class="container hero-grid">
    <div>
      <h1>Find a Paw. Give a Home.</h1>
      <p class="lead">Connect with loving pets waiting for their forever family.</p>
      <div class="hero-cta">
        <a class="btn" href="${ctx}/pets">Find Your Pet</a>
        <c:choose>
          <c:when test="${currentUser.role.name() == 'SHELTER'}"><a class="btn btn-peach" href="${ctx}/shelter/pets/add">List a Pet</a></c:when>
          <c:otherwise><a class="btn btn-peach" href="${ctx}/register?role=SHELTER">List a Pet</a></c:otherwise>
        </c:choose>
      </div>
    </div>
    <div class="hero-art">
      <img src="${ctx}/images/hero.svg" alt="A happy golden dog and a Siamese cat waiting to be adopted">
      <div class="hero-chip one">&#10084; ${publicStats.adoptions} happy adoptions</div>
      <div class="hero-chip two">&#128062; ${publicStats.availablePets} pets waiting</div>
    </div>
  </div>
</section>

<div class="container">
  <div class="stats-strip">
    <div class="strip-item"><div class="strip-icon">&#127881;</div><div><b>${publicStats.adoptions}</b><span>Successful adoptions</span></div></div>
    <div class="strip-item"><div class="strip-icon" style="background:var(--peach-soft)">&#128054;</div><div><b>${publicStats.availablePets}</b><span>Pets ready to adopt</span></div></div>
    <div class="strip-item"><div class="strip-icon" style="background:var(--cream)">&#127968;</div><div><b>${publicStats.shelters}</b><span>Partner shelters</span></div></div>
  </div>
</div>

<section class="section">
  <div class="container">
    <div class="row between" style="align-items:flex-end;margin-bottom:28px">
      <div><h2 class="mb-0">Meet pets looking for a home</h2><p class="muted mb-0">Newest friends from our shelters</p></div>
      <a class="btn btn-ghost" href="${ctx}/pets">See all pets</a>
    </div>
    <c:choose>
      <c:when test="${empty featuredPets}"><pf:emptyState title="No pets listed yet" text="Shelters are adding new friends every day. Check back soon." actionUrl="/register?role=SHELTER" actionLabel="List a Pet"/></c:when>
      <c:otherwise><div class="grid grid-pets"><c:forEach items="${featuredPets}" var="p"><pf:petCard pet="${p}" favorites="${favoriteIds}" hearts="${canHeart}"/></c:forEach></div></c:otherwise>
    </c:choose>
  </div>
</section>

<section class="section section-alt" id="how-it-works">
  <div class="container">
    <div class="section-head"><h2>How adoption works</h2><p>Four simple steps from first look to forever home.</p></div>
    <div class="steps">
      <div class="step reveal"><div class="step-icon">&#128269;</div><h3>Find Your Pet</h3><p class="muted mb-0">Search by species, breed, age or city and save favourites with a heart.</p></div>
      <div class="step reveal"><div class="step-icon" style="background:var(--mint-soft)">&#128214;</div><h3>Learn About Them</h3><p class="muted mb-0">Read their story, see the shelter details and message the shelter with questions.</p></div>
      <div class="step reveal"><div class="step-icon" style="background:var(--cream)">&#128221;</div><h3>Apply for Adoption</h3><p class="muted mb-0">Fill one short form and follow your application through our progress tracker.</p></div>
      <div class="step reveal"><div class="step-icon" style="background:var(--sky)">&#127969;</div><h3>Give Them a Forever Home</h3><p class="muted mb-0">Once approved, the shelter arranges the hand-over and your new family member comes home.</p></div>
    </div>
  </div>
</section>

<section class="section">
  <div class="container">
    <div class="cta-band">
      <div><h2 class="mb-0">Run a shelter or rescue?</h2><p class="muted mb-0" style="margin-top:8px">List your pets, review applications and message adopters in one place.</p></div>
      <a class="btn btn-peach" href="${ctx}/register?role=SHELTER">Register your shelter</a>
    </div>
  </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
