<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
function setProcSubmit(obj) {
	if(document.getElementById('fileDownload').checked) {
		obj.action = 'sendMessageGetFile.do';
	}
	else {
		obj.action = 'sendMessage.do';
	}
	return true;
}
</script>
<table>
<tr>
	<td>인바운드채널(서버)</td>
	<td>아웃바운드채널(클라이언트)</td>
	<td><c:if test="${!empty type}"><c:out value="${type}"/> 응답</c:if></td>
</tr>
<tr>
	<td style="width:600px;">
	<form method="post" onsubmit='return setProcSubmit(this);'>
	- IP Address : <input type="text" name="ip" value="127.0.0.1" /> <br/>
	- 채널 : <select name="inBoundChannelId" onchange="location.href='sendMessage.do?inBoundChannelId=' + this.options[this.selectedIndex].value;">
	<c:forEach var="channel" items="${listInBound}" varStatus="status">
	<option value="<c:out value="${channel.channelId}"/>" <c:if test="${param.inBoundChannelId eq channel.channelId}">selected</c:if>><c:out value="${channel.channelName}"/>(<c:out value="${channel.channelId}"/>)</option>
	</c:forEach>
	</select>
	<p>- 전송에 이용할 채널 타입
	<select name="sendChannelId">
	<c:forEach var="channel" items="${matchList}" varStatus="status">
	<option value="<c:out value="${channel.channelId}"/>" <c:if test="${param.sendChannelId eq channel.channelId}">selected</c:if>><c:out value="${channel.channelName}"/>(<c:out value="${channel.channelId}"/>)</option>
	</c:forEach>
	</select>
	<p><input id="fileDownload" name="fileDownload" type="checkbox" value="Y"/>파일다운로드</p>
	<p><textarea name="inRequest" style="width:100%;height:500px;"><c:out value="${inRequest}"/></textarea></p>
	<p><input type="submit" value="전송" /></p>
	<input type="hidden" name="type" value="in" />
	</form>
	</td>
	<td style="width:600px;">
	<p>&nbsp;</p>
	<form method="post">
	<select name="outBoundChannelId">
	<c:forEach var="channel" items="${listOutBound}" varStatus="status">
	<option value="<c:out value="${channel.channelId}"/>" <c:if test="${param.outBoundChannelId eq channel.channelId}">selected</c:if>><c:out value="${channel.channelName}"/>(<c:out value="${channel.channelId}"/>)</option>
	</c:forEach>
	</select>
	<p><textarea name="outRequest" style="width:100%;height:500px;"><c:out value="${outRequest}"/></textarea></p>
	<p><input type="submit" value="전송" /></p>
	<input type="hidden" name="type" value="out" />
	</form>
	</td>
	<td style="width:600px;">
	<p>&nbsp;</p>
	<c:if test="${!empty type}">
	<form method="post">
	<p><textarea style="width:100%;height:500px;"><c:out value="${response}"/></textarea></p>
	</form>
	</c:if>
	</td>
</tr>
</table>