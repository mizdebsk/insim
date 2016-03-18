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

function formatBytes(x, precision) {
    var units = [ "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB" ];
    var e = 0;
    while (x >= 1024 && e < units.length - 1) {
        x /= 1024;
        e++;
    }
    return Number(x).toFixed(precision) + " " + units[e];
}

function humanReadableBytes(x) {
    return formatBytes(x, 0);
}

function humanReadableBytesLong(x) {
    return formatBytes(x, 2);
}

function roundP2(x, n) {
    return x < 0 ? 0 : Math.round(x >> n) << n;
}
