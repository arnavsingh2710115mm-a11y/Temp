<%@ include file="/WEB-INF/views/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Messages"/><c:set var="activeNav" value="messages"/>
<%@ include file="/WEB-INF/views/common/dash-start.jspf" %>
<c:set var="isShelter" value="${currentUser.role.name() == 'SHELTER'}"/>
<div class="grid" style="grid-template-columns:minmax(0,1.6fr) minmax(280px,1fr);gap:22px;align-items:start">
  <div>
    <div class="tabs"><a class="${tab == 'inbox' ? 'active' : ''}" href="${ctx}/messages?tab=inbox">Inbox <c:if test="${unreadMessages gt 0}">(${unreadMessages} new)</c:if></a><a class="${tab == 'sent' ? 'active' : ''}" href="${ctx}/messages?tab=sent">Sent</a></div>
    <c:choose>
      <c:when test="${empty messages}"><pf:emptyState title="${tab == 'sent' ? 'No sent messages' : 'Your inbox is empty'}" text="${isShelter ? 'Adopters who write to you will appear here.' : 'Send a message to a shelter to ask about a pet.'}"/></c:when>
      <c:otherwise><div class="card" style="overflow:hidden"><c:forEach items="${messages}" var="m">
        <div class="msg ${tab == 'inbox' and not m.read ? 'unread' : ''}">
          <div class="avatar"><c:out value="${fn:toUpperCase(fn:substring(tab == 'sent' ? m.receiverName : m.senderName, 0, 1))}"/></div>
          <div class="msg-body">
            <div class="row between"><b><c:out value="${tab == 'sent' ? 'To: '.concat(m.receiverName) : m.senderName}"/></b><span class="small muted"><fmt:formatDate value="${m.createdAt}" pattern="d MMM yyyy, h:mm a"/></span></div>
            <p><c:out value="${m.body}"/></p>
            <div class="actions mt-1">
              <c:if test="${tab == 'inbox'}">
                <a class="btn btn-ghost btn-sm" href="${ctx}/messages?to=${m.senderId}#compose">Reply</a>
                <c:if test="${not m.read}"><form method="post" action="${ctx}/messages"><pf:csrf/><input type="hidden" name="action" value="read"><input type="hidden" name="id" value="${m.id}"><button class="btn btn-ghost btn-sm" type="submit">Mark read</button></form></c:if>
              </c:if>
              <form method="post" action="${ctx}/messages" data-confirm="Delete this message?"><pf:csrf/><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${m.id}"><input type="hidden" name="tab" value="${tab}"><button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
            </div>
          </div>
        </div></c:forEach></div></c:otherwise>
    </c:choose>
  </div>
  <form class="card card-pad" id="compose" method="post" action="${ctx}/messages">
    <pf:csrf/><input type="hidden" name="action" value="send">
    <h3>New message</h3>
    <c:choose>
      <c:when test="${empty contacts}"><p class="muted small">${isShelter ? 'You can message adopters once they apply for one of your pets or write to you.' : 'No shelters are available yet.'}</p></c:when>
      <c:otherwise>
        <div class="field"><label for="receiverId">${isShelter ? 'Adopter' : 'Shelter'}</label>
          <select id="receiverId" name="receiverId" required><option value="">Choose...</option>
            <c:forEach items="${contacts}" var="u"><option value="${u.id}" ${u.id == selectedReceiver ? 'selected' : ''}><c:out value="${u.name}"/></option></c:forEach></select></div>
        <div class="field"><label for="body">Message</label><textarea id="body" name="body" required maxlength="1000"><c:out value="${prefill}"/></textarea></div>
        <button class="btn btn-block" type="submit">Send message</button>
      </c:otherwise>
    </c:choose>
  </form>
</div>
<%@ include file="/WEB-INF/views/common/dash-end.jspf" %>
