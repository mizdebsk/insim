/*-
 * Copyright (c) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

colors = [ 'black', 'red', 'green' ];

function createDependencyGraph(data1, data2) {
    var s = '';
    var data = {};

    for ( var pkg in data1) {
        if (data1.hasOwnProperty(pkg)) {
            data[pkg] = data1[pkg];
        }
    }
    for ( var pkg in data2) {
        if (data2.hasOwnProperty(pkg)) {
            if (data.hasOwnProperty(pkg)) {
                var merged = data[pkg].concat(data2[pkg]).sort();
                data[pkg] = merged.filter(function(item, pos) {
                    return merged.indexOf(item) == pos
                })
            } else {
                data[pkg] = data2[pkg];
            }
        }
    }

    for ( var pkg in data) {
        if (data.hasOwnProperty(pkg)) {
            var oldHasNode = data1.hasOwnProperty(pkg);
            var newHasNode = data2.hasOwnProperty(pkg);
            var nodeUnique = oldHasNode ? newHasNode ? 0 : 1 : 2;

            s += '"' + pkg + '"';
            s += '[';
            s += 'color=' + colors[nodeUnique] + ';';
            s += 'fontcolor=' + colors[nodeUnique] + ';';
            s += 'tooltip="' + pkg + '";';
            s += '];';

            data[pkg].forEach(function(dep) {
                var oldHasEdge = oldHasNode && data1[pkg].indexOf(dep) >= 0;
                var newHasEdge = newHasNode && data2[pkg].indexOf(dep) >= 0;
                var edgeUnique = oldHasEdge ? newHasEdge ? 0 : 1 : 2;

                s += '"' + pkg + '" -> "' + dep + '"';
                s += '[';
                s += 'color=' + colors[edgeUnique] + ';';
                s += 'tooltip="' + pkg + ' requires ' + dep + '";';
                s += '];';
            });
        }
    }

    $("#jsviz").html(Viz('digraph {' + s + '}'));
}

function fetchGraphData(id, handler) {
    $.getJSON('/insim/api/data/graph/' + id, handler);
}

function depsOnload() {
    $('.datatable').dataTable({
        "order" : [ [ 0, "asc" ] ]
    });
    if (jsf.installationId) {
        fetchGraphData(jsf.installationId, function(data) {
            createDependencyGraph(data, data);
        });
    }
    if (jsf.oldInstallationId) {
        fetchGraphData(jsf.oldInstallationId, function(oldData) {
            fetchGraphData(jsf.newInstallationId, function(newData) {
                createDependencyGraph(oldData, newData);
            });
        });
    }
}

$(document).ready(depsOnload);
