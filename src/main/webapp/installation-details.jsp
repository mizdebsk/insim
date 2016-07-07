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
    installationId: ${installation.id}
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
    Installation details
	</jsp:attribute>

	<jsp:body>

      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">Installation information</h3>
        </div>
        <table class="table">
<tbody>
<tr>
<td class="col-md-2">Collection name:</td>
<td class="col-md-10"><a
							href="<c:url value="/collection/${installation.repository.collection.name}"/>">${installation.repository.collection.name}</a></td>
</tr>
<tr>
<td class="col-md-2">Module name:</td>
<td class="col-md-10"><a
							href="<c:url value="/module/${installation.module.name}"/>">${installation.module.name}</a></td>
</tr>
<tr>
<td class="col-md-2">Module version:</td>
<td class="col-md-10">${installation.version}</td>
</tr>
<tr>
<td class="col-md-2">Is complete:</td>
<td class="col-md-10">${installation.complete}</td>
</tr>
<tr>
<td class="col-md-2">Install size:</td>
<td class="col-md-10">${hrn.formatBytes(installation.installSize)}</td>
</tr>
<tr>
<td class="col-md-2">Download size:</td>
<td class="col-md-10">${hrn.formatBytes(installation.downloadSize)}</td>
</tr>
<tr>
<td class="col-md-2">Dependency count:</td>
<td class="col-md-10">${installation.dependencyCount}</td>
</tr>
<tr>
<td class="col-md-2">File count:</td>
<td class="col-md-10">${installation.fileCount}</td>
</tr>
</tbody>
</table>
      </div>

      <h2>RPM list</h2>
      <p>The following is a list of RPM packages installed.</p>
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
          <c:forEach var="rpm" items="${installation.rpms}">
            <tr>
              <td>${rpm.name}</td>
              <td>${rpm.epoch}</td>
              <td>${rpm.version}</td>
              <td>${rpm.release}</td>
              <td>${rpm.arch}</td>
              <td>${hrn.formatBytes(rpm.installSize)}</td>
              <td>${hrn.formatBytes(rpm.downloadSize)}</td>
              <td>${rpm.fileCount}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <h2>Dependency graph</h2>
      <div id="deps-hidden">
        <p>By default dependency graph is hidden to save resources, but it can be shown on demand.</p>
        <button type="button" class="btn btn-primary">Show graph</button>
      </div>
      <div id="deps-shown">
        <p>The following is a dependency graph. Nodes represent packages and edges – requires between
          them. Packages in parent modules (assumed to be installed before attempting to install the package)
          are not included in the graph. Big graphs may <strong>fail to render</strong> due to memory constrains.
          You can hover nodes and edges to <strong>tooltip</strong> with some information – useful especially when graph
          is not readable due to its complexity. <strong>Soft-dependencies</strong> are not shown in the graph (yet).</p>
        <div id="jsviz"></div>
      </div>
</jsp:body>
</t:genericpage>
