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
    moduleName: '${module.name}'
	</jsp:attribute>

	<jsp:attribute name="resources">
    <script type="application/javascript"
			src="<c:url value="/webjars/jquery/2.2.1/jquery.min.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/webjars/chartjs/1.0.2/Chart.min.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/webjars/dygraphs/1.1.1/dygraph-combined.js"/>"></script>
    <script type="application/javascript"
			src="<c:url value="/resources/js/graph.js"/>"></script>
	</jsp:attribute>

	<jsp:attribute name="title">
    Module ${module.name}
	</jsp:attribute>

	<jsp:body>

      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">Module information</h3>
        </div>

        <table class="table">
<tbody>
<tr>
<td class="col-md-2">Name:</td>
<td class="col-md-10">${module.name}</td>
</tr>
<tr>
<td class="col-md-2">Is metamodule:</td>
<td class="col-md-10">${module.metamodule}</td>
</tr>
<tr>
<td class="col-md-2">Packages:</td>
<td class="col-md-10"><ul class="list-inline">
              <c:forEach var="rpm" items="${module.installRpms}">
                <li>${rpm}</li>
                </c:forEach>
							</ul>
</td>
</tr>
<tr>
<td class="col-md-2">Based on:</td>
<td class="col-md-10"><ul class="list-inline">
              <c:forEach var="parent" items="${module.parents}">
                <li><a
										href="<c:url value="/module/${parent.name}"/>">${parent.name}</a></li>
                </c:forEach>
							</ul>
</td>
</tr>
<tr>
<td class="col-md-2">Baseline packages:</td>
<td class="col-md-10"><ul class="list-inline">
              <c:forEach var="pkg" items="${module.baselineRpms}">
                <li>${pkg}</li>
                </c:forEach>
							</ul>
</td>
</tr>
</tbody>
</table>

      </div>

      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">Latest statistics</h3>
        </div>
      <table class="table">
        <thead>
          <tr>
            <th>Collection</th>
            <th>Version</th>
            <th>Install size</th>
            <th>Download size</th>
            <th>Dependency count</th>
            <th>File count</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="collectionInfo" items="${collectionInfos}">
            <tr>
              <td>${collectionInfo.collection.name}</td>
              <td>${collectionInfo.latestInstallation.version}</td>
              <td>${hrn.formatBytes(collectionInfo.latestInstallation.installSize)}</td>
              <td>${hrn.formatBytes(collectionInfo.latestInstallation.downloadSize)}</td>
              <td>${collectionInfo.latestInstallation.dependencyCount}</td>
              <td>${collectionInfo.latestInstallation.fileCount}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      </div>

      <ul class="list-unstyled">
        <li><input type="checkbox" checked="checked"
				onclick="$('.chart-isz').toggle(checked)" />install size</li>
        <li><input type="checkbox" checked="checked"
				onclick="$('.chart-dsz').toggle(checked)" />download size</li>
        <li><input type="checkbox" checked="checked"
				onclick="$('.chart-dcn').toggle(checked)" />dependency count</li>
        <li><input type="checkbox" checked="checked"
				onclick="$('.chart-fcn').toggle(checked)" />file count</li>
      </ul>

      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">Module information</h3>
        </div>
        <div class="panel-body">
          <div class="chart-isz">
            <h3 class="title">Install size</h3>
            <canvas id="bar-isz" height="400" />
          </div>
          <div class="chart-dsz">
            <h3 class="title">Download size</h3>
            <canvas id="bar-dsz" height="400" />
          </div>
          <div class="chart-dcn">
            <h3 class="title">Dependency count</h3>
            <canvas id="bar-dcn" height="400" />
          </div>
          <div class="chart-fcn">
            <h3 class="title">File count</h3>
            <canvas id="bar-fcn" height="400" />
          </div>
        </div>
      </div>

      <c:forEach var="collectionInfo" items="${collectionInfos}">
        <div class="coll-${collectionInfo.collection.name}">
          <h2>Collection ${collectionInfo.collection.name}</h2>
          <p>
            Latest version-release:
            <code>${collectionInfo.latestInstallation.version}-${collectionInfo.latestInstallation.release}</code>
          </p>
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Installation size</h3>
            </div>
            <div class="panel-body">
              <div class="chart-isz"></div>
            </div>
          </div>
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Download size</h3>
            </div>
            <div class="panel-body">
              <div class="chart-dsz"></div>
            </div>
          </div>
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Dependency count</h3>
            </div>
            <div class="panel-body">
              <div class="chart-dcn"></div>
            </div>
          </div>
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">File count</h3>
            </div>
            <div class="panel-body">
              <div class="chart-fcn"></div>
            </div>
          </div>
        </div>
      </c:forEach>

    <ul id="contextMenu" class="dropdown-menu" role="menu">
      <li id="details"><a tabindex="-1" href="#">Show details</a></li>
      <li class="divider"></li>
      <li id="select"><a tabindex="-1" href="#">Select for comparison</a></li>
      <li id="compare" class="disabled"><a tabindex="-1" href="#">Compare with selected</a></li>
    </ul>

</jsp:body>
</t:genericpage>
