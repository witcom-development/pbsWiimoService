<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1><c:out value="${exception.code}"/></h1>

<div>
	<pre><c:out value="${stackTrace}"/></pre>
</div>
