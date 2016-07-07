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
	<jsp:attribute name="params">
    oldInstallationId: ${oldInstallation.id},
    newInstallationId: ${newInstallation.id}
	</jsp:attribute>

	<jsp:attribute name="resources">
    <link rel="stylesheet" type="text/css"
			href="<c:url value="/webjars/datatables/1.10.11/css/jquery.dataTables.min.css"/>"></link>
    <link rel="stylesheet" type="text/css"
			href="<c:url value="/webjars/datatables/1.10.11/css/dataTables.bootstrap.min.css"/>"></link>
    <script type="application/javascript"
			src="<c:url value="/webjars/jquery/2.2.1/jquery.min.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/webjars/viz.js/1.3.0/viz.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/webjars/datatables/1.10.11/js/jquery.dataTables.min.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/resources/js/deps.js"/>"></script>
	</jsp:attribute>

	<jsp:attribute name="title">
    Installation difference
	</jsp:attribute>

	<jsp:body>

<table class="table">
<tbody>
<tr>
<td class="col-md-2">Collection name:</td>
<td class="col-md-5"><a
						href="<c:url value="/collection/${oldInstallation.repository.collection.name}"/>">${oldInstallation.repository.collection.name}</a></td>
<td class="col-md-5"><a
						href="<c:url value="/collection/${newInstallation.repository.collection.name}"/>">${newInstallation.repository.collection.name}</a></td>
</tr>
<tr>
<td class="col-md-2">Module name:</td>
<td class="col-md-5"><a
						href="<c:url value="/module/${oldInstallation.module.name}"/>">${oldInstallation.module.name}</a></td>
<td class="col-md-5"><a
						href="<c:url value="/module/${newInstallation.module.name}"/>">${newInstallation.module.name}</a></td>
</tr>
<tr>
<td class="col-md-2">Module version:</td>
<td class="col-md-5">${oldInstallation.version}</td>
<td class="col-md-5">${newInstallation.version}</td>
</tr>
<tr>
<td class="col-md-2">Is complete:</td>
<td class="col-md-5">${oldInstallation.complete}</td>
<td class="col-md-5">${newInstallation.complete}</td>
</tr>
<tr>
<td class="col-md-2">Install size:</td>
<td class="col-md-5">${hrn.formatBytes(oldInstallation.installSize)}</td>
<td class="col-md-5">${hrn.formatBytes(newInstallation.installSize)}</td>
</tr>
<tr>
<td class="col-md-2">Download size:</td>
<td class="col-md-5">${hrn.formatBytes(oldInstallation.downloadSize)}</td>
<td class="col-md-5">${hrn.formatBytes(newInstallation.downloadSize)}</td>
</tr>
<tr>
<td class="col-md-2">Dependency count:</td>
<td class="col-md-5">${oldInstallation.dependencyCount}</td>
<td class="col-md-5">${newInstallation.dependencyCount}</td>
</tr>
<tr>
<td class="col-md-2">File count:</td>
<td class="col-md-5">${oldInstallation.fileCount}</td>
<td class="col-md-5">${newInstallation.fileCount}</td>
</tr>
</tbody>
</table>

      <c:forEach var="piece" items="${diffPieces}">
        <c:if test="${not empty piece.elements}">
          <h2>${piece.title}</h2>
          <table class="datatable table table-striped table-bordered">
            <thead>
              <tr>
                <th>Name</th>
                <th>Epoch</th>
                <th>Version</th>
                <th>Release</th>
                <th>Arch</th>
                <th>Install size</th>
                <th>Download size</th>
                <th>File count</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="rpmDiff" items="${piece.elements}">
                <tr>
                  <c:forEach var="fd" items="${rpmDiff}">
                    <td class="diff">
                      <c:if test="${not fd.differs()}">
                        <div class="common">${fd.old}</div>
                      </c:if>
                      <c:if test="${fd.differs() and fd.hasOld()}">
                        <div class="old">${fd.old}</div>
                      </c:if>
                      <c:if test="${fd.differs() and fd.hasNew()}">
                        <div class="new">${fd.new}</div>
                      </c:if>
                    </td>
                  </c:forEach>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:if>
      </c:forEach>

      <h2>Dependency graph difference</h2>
      <div id="deps-hidden">
        <p>By default dependency graph difference is hidden to save resources, but it can be shown on demand.</p>
        <button type="button" class="btn btn-primary">Show graph</button>
      </div>
      <div id="deps-shown">
        <p>The following is a difference between two dependency graphs. <strong>Red</strong> nodes and edges represent
          packages and requires unique to first installation (showed above on the left side), <strong>green</strong> nodes
          and edges are unique to second (right) installation, while black color means parts common
          to both installations.</p>
        <div id="jsviz"></div>
      </div>
      </jsp:body>
</t:genericpage>
