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
    index : 3,
    bytes : true
}, {
    id : 'dsz',
    title : 'Download size',
    index : 4,
    bytes : true
}, {
    id : 'dcn',
    title : 'Dependency count',
    index : 5,
    bytes : false
}, {
    id : 'fcn',
    title : 'File count',
    index : 6,
    bytes : false
} ];

var selectedId;
var clickedId;
var inhibitBodyClickHandler = false;

function findInstallationIdByTimestamp(collData, timestamp) {
    for (var i = 0; i < collData.length; i++) {
        var inst = collData[i];
        if (inst[1] == timestamp) {
            return inst[0];
        }
    }
    return undefined;
}

function graphClickHandler(e) {
    inhibitBodyClickHandler = true;

    $("#contextMenu").show().offset({
        left : $(window).scrollLeft() + e.clientX,
        top : $(window).scrollTop() + e.clientY
    });
}

function createGraph(domElement, collData, graphType) {
    var dataWidth = collData.length;
    var dataMin = Number.MAX_SAFE_INTEGER;
    var dataMax = 0;
    collData.forEach(function(inst) {
        var value = inst[graphType.index];
        dataMin = Math.min(dataMin, value);
        dataMax = Math.max(dataMax, value);
    });
    var maxWidth = $(domElement).parent().width();
    // XXX don't hardcode sizes here
    var width = Math.min(150 + 20 * dataWidth, maxWidth);
    var options = {
        width : width,
        labels : [ 'Timestamp', graphType.title ],
        clickCallback : function(e, x, pts) {
            clickedId = findInstallationIdByTimestamp(collData, x);
            if (clickedId) {
                graphClickHandler(e);
            }
        },
        axes : {
            y : {
                axisLabelWidth : 100,
                valueRange : [ Math.max(0, dataMin - 2), dataMax + 5 ]
            }
        }
    };
    if (graphType.bytes) {
        $.extend(true, options, {
            axes : {
                y : {
                    labelsKMG2 : true,
                    valueFormatter : humanReadableBytesLong,
                    axisLabelFormatter : humanReadableBytes,
                    valueRange : [ roundP2(dataMin - 3 * 1024 * 1024, 20),
                            roundP2(dataMax + 6 * 1024 * 1024, 20) ]
                }
            }
        });
    }
    var graphData = [];
    collData.forEach(function(inst) {
        var timestamp = new Date(inst[1]);
        var value = inst[graphType.index];
        graphData.push([ timestamp, value ]);
    });
    new Dygraph(domElement, graphData, options);
}

function loadGraphs(data) {
    var bar = {};
    for ( var collName in data) {
        if (data.hasOwnProperty(collName)) {
            var collData = data[collName];
            graphTypes
                    .forEach(function(graphType) {
                        $(".coll-" + collName).find(".chart-" + graphType.id)
                                .each(function() {
                                    createGraph(this, collData, graphType);
                                });
                        if (!bar[graphType.id]) {
                            bar[graphType.id] = {
                                labels : [],
                                datasets : [ {
                                    data : []
                                } ]
                            };
                        }
                        if (collData.length > 0) {
                            bar[graphType.id]['labels'].push(collName);
                            bar[graphType.id]['datasets'][0]['data']
                                    .push(collData[collData.length - 1][graphType.index]);
                        }
                    });
        }
    }
    graphTypes.forEach(function(graphType) {
        $("#bar-" + graphType.id).each(function() {
            var dataWidth = bar[graphType.id]['datasets'][0]['data'].length;
            // XXX don't hardcode sizes here
            $(this).width(150 + 40 * dataWidth);
            $(this).parent().width($(this).width());
            // FIXME this should be in CSS
            $(this).parent().css('float', 'left');
            var ctx = this.getContext('2d');
            var options = {
                tooltipTemplate : function(x) {
                    return x.value;
                }
            };
            if (graphType.bytes) {
                $.extend(true, options, {
                    scaleLabel : function(x) {
                        return humanReadableBytes(x.value);
                    },
                    tooltipTemplate : function(x) {
                        return humanReadableBytesLong(x.value);
                    }
                });
            }
            var chart = new Chart(ctx).Bar(bar[graphType.id], options);
            this.onclick = function(e) {
                var activeBars = chart.getBarsAtEvent(e);
                if (activeBars.length > 0) {
                    var collName = activeBars[0]['label'];
                    var collData = data[collName];
                    clickedId = collData[collData.length - 1][0];
                    graphClickHandler(e);
                }
            };
        });
    });
}

$(document).ready(function() {
    $.getJSON('/insim/api/data/installations/' + jsf.moduleName, loadGraphs)

    $("#contextMenu > #details > a").click(function(e) {
        $("#contextMenu").hide();
        location.href = "../installation/" + clickedId;
        return false;
    });

    $("#contextMenu > #select > a").click(function(e) {
        $("#contextMenu").hide();
        $("#contextMenu > #compare").removeClass("disabled");
        selectedId = clickedId;
        return false;
    });

    $("#contextMenu > #compare > a").click(function(e) {
        if (!selectedId)
            return false;
        $("#contextMenu").hide();
        location.href = "../installation/diff/" + selectedId + "/" + clickedId;
        return false;
    });

    $("body").click(function(e) {
        if (inhibitBodyClickHandler) {
            inhibitBodyClickHandler = false;
        } else {
            $("#contextMenu").hide();
        }
    });
});
