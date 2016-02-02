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

graphTypes = [ {
    id : 'isz',
    title : 'Install size',
    index : 3
}, {
    id : 'dsz',
    title : 'Download size',
    index : 4
}, {
    id : 'dcn',
    title : 'Dependency count',
    index : 5
}, {
    id : 'fcn',
    title : 'File count',
    index : 6
} ];

var selectedId;

function findInstallationIdByTimestamp(collData, timestamp) {
    for (var i = 0; i < collData.length; i++) {
        var inst = collData[i];
        if (inst[1] == timestamp) {
            return inst[0];
        }
    }
    return undefined;
}

function graphClickHandler(collData, e, id) {
    if (e.shiftKey && selectedId) {
        location.href = "installation/diff/" + selectedId + "/" + id;
    } else if (e.ctrlKey) {
        location.href = "installation/" + id;
    } else {
        selectedId = id;
    }
}

function createGraph(domElement, collData, graphType) {
    var options = {
        labels : [ 'Timestamp', graphType.title ],
        title : graphType.title,
        clickCallback : function(e, x, pts) {
            var id = findInstallationIdByTimestamp(collData, x);
            if (id) {
                graphClickHandler(collData, e, id);
            }
        }
    };
    var graphData = [];
    collData.forEach(function(inst) {
        var timestamp = new Date(inst[1]);
        var value = inst[graphType.index];
        graphData.push([ timestamp, value ]);
    });
    new Dygraph(domElement, graphData, options);
}

function loadGraphs(data) {
    for ( var collName in data) {
        if (data.hasOwnProperty(collName)) {
            graphTypes.forEach(function(graphType) {
                $(".coll-" + collName).find(".chart-" + graphType.id).each(
                        function() {
                            createGraph(this, data[collName], graphType);
                        });
            });
        }
    }
}

$(document).ready(function() {
    $.getJSON('/insim/api/data/installations/' + jsf.packageName, loadGraphs)
});
