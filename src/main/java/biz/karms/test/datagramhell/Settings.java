/*
 * Copyright (c) 2020 Contributors to the Datagram Hell project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package biz.karms.test.datagramhell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Settings {
    public static class Conf {
        public final int udpPort;
        public final String serverNic;
        public final InetAddress serverUdpGroup;
        public final InetAddress serverAddress;
        public final String clientAnic;
        public final InetAddress clientAudpGroup;
        public final InetAddress clientAaddress;
        public final String clientBnic;
        public final InetAddress clientBudpGroup;
        public final InetAddress clientBaddress;

        public Conf(int udpPort, String serverNic, InetAddress serverUdpGroup,
                    InetAddress serverAddress, String clientAnic, InetAddress clientAudpGroup,
                    InetAddress clientAaddress, String clientBnic, InetAddress clientBudpGroup,
                    InetAddress clientBaddress) {
            this.udpPort = udpPort;
            this.serverNic = serverNic;
            this.serverUdpGroup = serverUdpGroup;
            this.serverAddress = serverAddress;
            this.clientAnic = clientAnic;
            this.clientAudpGroup = clientAudpGroup;
            this.clientAaddress = clientAaddress;
            this.clientBnic = clientBnic;
            this.clientBudpGroup = clientBudpGroup;
            this.clientBaddress = clientBaddress;
        }

        @Override
        public String toString() {
            return "Configuration used: " + '\n' +
                    " udpPort         " + udpPort + '\n' +
                    " serverNic       " + serverNic + '\n' +
                    " serverUdpGroup  " + serverUdpGroup + '\n' +
                    " serverAddress   " + serverAddress + '\n' +
                    " clientAnic      " + clientAnic + '\n' +
                    " clientAudpGroup " + clientAudpGroup + '\n' +
                    " clientAaddress  " + clientAaddress + '\n' +
                    " clientBnic      " + clientBnic + '\n' +
                    " clientBudpGroup " + clientBudpGroup + '\n' +
                    " clientBAddress  " + clientBaddress + '\n';
        }
    }

    public static Conf getConf(File conf) throws IOException {
        // Default properties
        Properties p = new Properties();
        try (InputStream is = Main.class.getResourceAsStream("/settings.properties")) {
            p.load(is);
        }
        // Whatever we've got from a file
        if (conf != null && conf.exists()) {
            for (String l : Files.readAllLines(Paths.get(conf.getAbsolutePath()))) {
                String[] kv = l.split("=");
                if (isNotEmpty(kv[0]) && isNotEmpty(kv[1])) {
                    p.replace(kv[0].trim(), kv[1].trim());
                }
            }
        }
        // Env vars and Sys props
        for (String pn : p.stringPropertyNames()) {
            String env = System.getenv().get(pn.toUpperCase().replace('.', '_'));
            if (isNotEmpty(env)) {
                p.replace(pn, env);
            }
            String sys = System.getProperty(pn);
            if (isNotEmpty(sys)) {
                p.replace(pn, sys);
            }
        }
        return new Conf(
                Integer.parseInt(p.getProperty("udp.port")),
                p.getProperty("server.nic"),
                InetAddress.getByName(p.getProperty("server.udp.group")),
                InetAddress.getByName(p.getProperty("server.address")),
                p.getProperty("client_a.nic"),
                InetAddress.getByName(p.getProperty("client_a.udp.group")),
                InetAddress.getByName(p.getProperty("client_a.address")),
                p.getProperty("client_b.nic"),
                InetAddress.getByName(p.getProperty("client_b.udp.group")),
                InetAddress.getByName(p.getProperty("client_b.address"))
        );
    }

    public static boolean isNotEmpty(String s) {
        return (s != null && s.trim().length() != 0);
    }
}
