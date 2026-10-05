<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<span class="badge badge-${fn:toLowerCase(value)}"><c:out value="${fn:toUpperCase(fn:substring(value,0,1))}${fn:toLowerCase(fn:substring(value,1,fn:length(value)))}"/></span>
