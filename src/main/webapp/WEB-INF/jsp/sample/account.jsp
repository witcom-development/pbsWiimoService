<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>계정</h1>

<h4>${result}</h4>

<div>
	<form method="post">
		<ul>
			<li>아이디 <input type="text" name="id" value="${account.id}"/></li>
			<li>비밀번호 <input type="text" name="password" value="${account.password}"/></li>
			<li>새 비밀번호 <input type="text" name="newPassword" value="${account.newPassword}"/></li>
			<li>비밀번호 확인 <input type="text" name="rePassword" value="${account.rePassword}"/></li>
		</ul>
		<input type="submit" value="로그인" onclick="parentNode.action='<c:url value="/login.do"/>'"/>
		<input type="submit" value="비밀번호 변경" onclick="parentNode.action='<c:url value="/changePassword.do"/>'"/>
	</form>
</div>
