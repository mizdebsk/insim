/*-
 * Copyright (c) 2015 Red Hat, Inc.
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
package org.fedoraproject.insim.util;

/**
 * @author Mikolaj Izdebski
 */
public class Hrn {
    public static String formatBytes(double x) {
        String[] units = { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB" };
        int e = 0;
        while (x >= 1024 && e < units.length - 1) {
            x /= 1024;
            e++;
        }
        return String.format("%.2f", x) + " " + units[e];
    }

}
