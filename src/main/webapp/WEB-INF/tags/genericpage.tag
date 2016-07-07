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
<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="params" fragment="true" %>
<%@attribute name="title" fragment="true" %>
<%@attribute name="resources" fragment="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/webjars/bootstrap/3.3.6/css/bootstrap.min.css"/>" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/koschei.css"/>" />
    <script type="application/javascript">
      jsf = { <jsp:invoke fragment="params"/> }
    </script>
    <script type="application/javascript" src="<c:url value="/resources/js/util.js"/>"></script>
    <jsp:invoke fragment="resources"/>
    <title><jsp:invoke fragment="title"/></title>
  </head>

  <body>
    <div class="container">
      <div class="header clearfix">
        <div class="navigation">
          <ul class="nav nav-pills pull-right">
            <li><a href="<c:url value="/"/>">Modules</a></li>
            <li><a href="<c:url value="/collections"/>">Collections</a></li>
          </ul>
        </div>
        <h3 class="text-muted"><a href="<c:url value="/"/>">Insim</a></h3>
      </div>
      <div id="content">
        <h1><jsp:invoke fragment="title"/></h1>
        <jsp:doBody/>
      </div>
    </div>
  </body>

</html>
