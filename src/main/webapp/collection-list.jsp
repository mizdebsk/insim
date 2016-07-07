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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:genericpage>
	<jsp:attribute name="title">
	Collections
	</jsp:attribute>

	<jsp:body>
    <c:if test="${empty collections}">
      <em>No collections.</em>
    </c:if>

    <c:if test="${not empty collections}">
      <div class="list-group col-md-3">
      <c:forEach items="${collections}" var="collection">
         <a href="<c:url value="/collection/${collection.name}"/>"
						class="list-group-item">${collection.name}</a>
      </c:forEach>
      </div>
    </c:if>
    
    </jsp:body>
</t:genericpage>
