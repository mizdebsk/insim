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

function loadDependencyGraph(data) {
    var s = '';
    for ( var pkg in data) {
        if (data.hasOwnProperty(pkg)) {
            s += '"' + pkg + '";';
            deps = data[pkg];
            deps.forEach(function(dep) {
                s += '"' + pkg + '" -> "' + dep + '";';
            });
        }
    }
    $("#jsviz").html(Viz('digraph {' + s + '}'));
}

function createDependencyGraphDiff(data1, data2) {
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
            s += '"' + pkg + '"';
            if (!data1.hasOwnProperty(pkg)) {
                s += "[color=green;fontcolor=green]";
            } else if (!data2.hasOwnProperty(pkg)) {
                s += "[color=red;fontcolor=red]";
            }
            s += ";";
            deps = data[pkg];
            deps.forEach(function(dep) {
                s += '"' + pkg + '" -> "' + dep + '"';
                if (!data1.hasOwnProperty(pkg) || !data1.hasOwnProperty(dep)
                        || data1[pkg].indexOf(dep) == -1) {
                    s += "[color=green]";
                } else if (!data2.hasOwnProperty(pkg)
                        || !data2.hasOwnProperty(dep)
                        || data2[pkg].indexOf(dep) == -1) {
                    s += "[color=red]";
                }
                s += ";";
            });
        }
    }
    $("#jsviz").html(Viz('digraph {' + s + '}'));
}

function loadDependencyGraphDiff(oldId, newId) {
    $.getJSON("api/data/graph/" + oldId, function(oldData) {
        $.getJSON("api/data/graph/" + newId, function(newData) {
            createDependencyGraphDiff(oldData, newData);
        });
    });
}
