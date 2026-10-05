<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Analytics"/><c:set var="activeNav" value="analytics"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<div class="grid grid-4">
  <div class="stat"><div class="ico">&#128101;</div><div><b>${stats.totalUsers}</b><span>Total users</span></div></div>
  <div class="stat peach"><div class="ico">&#128062;</div><div><b>${stats.totalPets}</b><span>Total pets</span></div></div>
  <div class="stat cream"><div class="ico">&#127881;</div><div><b>${stats.availablePets}</b><span>Available pets</span></div></div>
  <div class="stat sky"><div class="ico">&#127969;</div><div><b>${stats.adoptedPets}</b><span>Adopted pets</span></div></div>
  <div class="stat lilac"><div class="ico">&#9203;</div><div><b>${stats.pendingApplications}</b><span>Pending applications</span></div></div>
  <div class="stat"><div class="ico">&#9989;</div><div><b>${stats.approvedApplications}</b><span>Approved applications</span></div></div>
</div>
<div class="grid grid-2 mt-3">
  <div class="card card-pad"><h3>Users by role</h3><pf:chart type="donut" data="${stats.usersByRole}"/></div>
  <div class="card card-pad"><h3>Pets by status</h3><pf:chart type="bar" data="${stats.petsByStatus}"/></div>
  <div class="card card-pad"><h3>Listed pets by species</h3><pf:chart type="bar" data="${stats.petsBySpecies}"/></div>
  <div class="card card-pad"><h3>Applications by status</h3><pf:chart type="donut" data="${stats.applicationsByStatus}"/></div>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
