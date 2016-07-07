<%--
 ! Copyright (c) 2015-2016 Red Hat, Inc.
 !
 ! Licensed under the Apache License, Version 2.0 (the "License");
 ! you may not use this file except in compliance with the License.
 ! You may obtain a copy of the License at
 !
 !     http://www.apache.org/licenses/LICENSE-2.0
 !
 ! Unless required by applicable law or agreed to in writing, software
 ! distributed under the License is distributed on an "AS IS" BASIS,
 ! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ! See the License for the specific language governing permissions and
 ! limitations under the License.
 `--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty modules}">
	<em>No modules.</em>
</c:if>

<c:if test="${not empty modules}">
	<div class="list-group col-md-4">
		<c:forEach items="${modules}" var="module">
			<a href="<c:url value="/module/${module.name}"/>"
				class="list-group-item">${module.name}</a>
		</c:forEach>
	</div>
</c:if>
