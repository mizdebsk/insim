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
    Collection ${collection.name}
	</jsp:attribute>

	<jsp:body>

      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">Collection information</h3>
        </div>
<table class="table">
<tbody>
<tr>
<td class="col-md-2">Name:</td>
<td class="col-md-10">${collection.name}</td>
</tr>
<tr>
<td class="col-md-2">YUM repository</td>
<td class="col-md-10">${collection.location}</td>
</tr>
</tbody>
</table>
      </div>

      <h2>Module list</h2>
      <c:set var="modules" value="${collection.modules}" scope="request" />
      <jsp:include page="list.jsp" />
    </jsp:body>

</t:genericpage>
