<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
ddddddddddddddddddddddd
<h1><c:out value="${helloWorld.message}"/></h1>

<h4><spring:message code="sample.availableLanguages"/></h4>
<ul>
<c:forEach var="language" items="${availableLanguages}">
	<li><a href="<c:url value="/helloWorld.do?lang=${language}"/>"><spring:message code="sample.language.${language}"/></a></li>
</c:forEach>
</ul>
