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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Main {

    public enum Mode {
        PORT_ONLY,
        NIC,
        NIC_NO_PORT,
        GROUP,
        GROUP_AND_ADDRESS
    }

    public enum DatagramMode {
        PLAIN,
        GROUP_PORT,
        GROUP_PORT_0
    }

    public enum Name {
        SERVER, CLIENT_A, CLIENT_B
    }

    public static class TestMode {
        public final Mode mode;
        public final DatagramMode datagramMode;

        public TestMode(Mode mode, DatagramMode datagramMode) {
            this.mode = mode;
            this.datagramMode = datagramMode;
        }
    }

    public static AtomicBoolean serverIsRunning = new AtomicBoolean(true);
    public static AtomicBoolean clientAIsRunning = new AtomicBoolean(true);
    public static AtomicBoolean clientBIsRunning = new AtomicBoolean(true);

    public static synchronized MulticastSocket getSocket(Mode mode, Name name, Settings.Conf conf) throws IOException {
        String nic;
        InetAddress udpGroup;
        InetAddress address;
        if (name == Name.SERVER) {
            nic = conf.serverNic;
            udpGroup = conf.serverUdpGroup;
            address = conf.serverAddress;
        } else if (name == Name.CLIENT_A) {
            nic = conf.clientAnic;
            udpGroup = conf.clientAudpGroup;
            address = conf.clientAaddress;
        } else if (name == Name.CLIENT_B) {
            nic = conf.clientBnic;
            udpGroup = conf.clientBudpGroup;
            address = conf.clientBaddress;
        } else {
            throw new IllegalArgumentException("Unknown name.");
        }
        NetworkInterface nif = NetworkInterface.getByName(nic);
        if (nif == null) {
            System.out.println("Cannot find NIC " + nic + ", make sure the name is correct, e.g. net0:1 vs. net1 vs net0 etc. Terminating...");
            System.exit(1);
        }
        InetAddress addressFromNic = null;
        System.out.println(name + ": Enumerating addresses for NIC " + nic);
        for (InterfaceAddress ia : nif.getInterfaceAddresses()) {
            if (addressFromNic == null) {
                addressFromNic = ia.getAddress();
                System.out.println(name + ": Selected -> " + addressFromNic.toString());
                continue;
            }
            System.out.println(name + ":            " + addressFromNic.toString());
        }
        System.out.flush();
        MulticastSocket s;
        if (mode == Mode.PORT_ONLY) {
            s = new MulticastSocket(conf.udpPort);
        } else if (mode == Mode.NIC) {
            s = new MulticastSocket(new InetSocketAddress(addressFromNic, conf.udpPort));
        } else if (mode == Mode.NIC_NO_PORT) {
            s = new MulticastSocket(new InetSocketAddress(addressFromNic, 0));
        } else if (mode == Mode.GROUP) {
            s = new MulticastSocket(new InetSocketAddress(udpGroup, conf.udpPort));
        } else if (mode == Mode.GROUP_AND_ADDRESS) {
            s = new MulticastSocket(new InetSocketAddress(udpGroup, conf.udpPort));
            s.setInterface(address);
        } else {
            throw new IllegalArgumentException("Unknown mode.");
        }
        s.setTimeToLive(0);
        s.joinGroup(udpGroup);
        return s;
    }

    public static class Server implements Runnable {
        private final Settings.Conf conf;
        private final Mode mode;
        private final DatagramMode datagramMode;

        public Server(Settings.Conf conf, Mode mode, DatagramMode datagramMode) {
            this.conf = conf;
            this.mode = mode;
            this.datagramMode = datagramMode;
        }

        @Override
        public void run() {
            try (MulticastSocket s = getSocket(mode, Name.SERVER, conf)) {
                System.out.println(Name.SERVER + " is ready.");
                System.out.flush();
                long c = 0;
                while (serverIsRunning.get()) {
                    byte[] buf = ("Hello #" + c + " from " + Name.SERVER).getBytes(StandardCharsets.US_ASCII);
                    DatagramPacket packet;
                    if (datagramMode == DatagramMode.PLAIN) {
                        packet = new DatagramPacket(buf, buf.length);
                    } else if (datagramMode == DatagramMode.GROUP_PORT) {
                        packet = new DatagramPacket(buf, buf.length, conf.serverUdpGroup, conf.udpPort);
                    } else if (datagramMode == DatagramMode.GROUP_PORT_0) {
                        packet = new DatagramPacket(buf, buf.length, conf.serverUdpGroup, 0);
                    } else {
                        throw new IllegalArgumentException("Unknown datagram mode.");
                    }
                    try {
                        s.send(packet);
                    } catch (Exception e) {
                        System.out.println(Name.SERVER + " error: " + e.getMessage() + ", " + e.getCause().getMessage());
                    }
                    try {
                        sleep(800);
                    } catch (Exception e) {
                        System.out.println(Name.SERVER + " error: " + e.getMessage());
                    }
                    c++;
                }
            } catch (Exception e) {
                System.out.println(Name.SERVER + " terminated due to " + e.getMessage() + ", " + e.getCause().getMessage());
            }
            System.out.println(Name.SERVER + " terminated.");
            System.out.flush();
        }
    }

    public static class Client implements Runnable {
        private final Settings.Conf conf;
        private final Mode mode;
        private final Name name;
        private final DatagramMode datagramMode;

        public Client(Settings.Conf conf, Mode mode, Name name, DatagramMode datagramMode) {
            this.conf = conf;
            this.mode = mode;
            this.name = name;
            this.datagramMode = datagramMode;
        }

        @Override
        public void run() {
            try (MulticastSocket s = getSocket(mode, name, conf)) {
                System.out.println(name + " is ready.");
                System.out.flush();
                while (((name == Name.CLIENT_A) ? clientAIsRunning : clientBIsRunning).get()) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet;
                    if (datagramMode == DatagramMode.PLAIN) {
                        packet = new DatagramPacket(buf, buf.length);
                    } else if (datagramMode == DatagramMode.GROUP_PORT) {
                        packet = new DatagramPacket(buf, buf.length, conf.serverUdpGroup, conf.udpPort);
                    } else if (datagramMode == DatagramMode.GROUP_PORT_0) {
                        packet = new DatagramPacket(buf, buf.length, conf.serverUdpGroup, 0);
                    } else {
                        throw new IllegalArgumentException("Unknown datagram mode.");
                    }
                    try {
                        s.setSoTimeout(1000);
                        s.receive(packet);
                    } catch (Exception e) {
                        System.out.println(name + " error: " + e.getMessage() + ", " + e.getCause().getMessage());
                    }
                    System.out.println(name + " received data: " + new String(buf) + " from: " + packet.getSocketAddress());
                    System.out.flush();
                }
                s.leaveGroup((name == Name.CLIENT_A) ? conf.clientAudpGroup : conf.clientBudpGroup);
            } catch (Exception e) {
                System.out.println(name + " terminated due to " + e.getMessage() + ", " + e.getCause().getMessage());
            }
            System.out.println(name + " terminated.");
            System.out.flush();
        }
    }


    public static void printUsage() {
        System.out.println("Usage: \n" +
                "-h (--help)\tPrints this help\n" +
                "-c=<path> (--config=<path>)\tTakes path to a settings.properties file.\n" +
                "\n" +
                "Properties could also be overridden by env vars (all caps, underscores,\n" +
                " e.g. CLIENT_B_NIC=eth1) and sys props, e.g. -Dclient_b.nic=eth1");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File confFile = null;
        if (args.length > 0) {
            for (String s : args) {
                if (s.equalsIgnoreCase("-h") || s.equalsIgnoreCase("--help")) {
                    printUsage();
                    System.exit(0);
                }
                if (s.startsWith("-c=") || s.startsWith("--config=")) {
                    confFile = new File(s.split("=")[1].trim());
                    if (!confFile.exists()) {
                        System.out.println("Config file " + confFile.getAbsolutePath() + " does not exist.");
                        System.exit(1);
                    }
                }
            }
        }
        Settings.Conf conf = Settings.getConf(confFile);

        System.out.println("Using configuration:\n" + conf.toString());

        TestMode[] testModes = new TestMode[]{
                new TestMode(Mode.PORT_ONLY, DatagramMode.GROUP_PORT),
                new TestMode(Mode.NIC_NO_PORT, DatagramMode.GROUP_PORT),
                new TestMode(Mode.NIC, DatagramMode.GROUP_PORT),
                new TestMode(Mode.GROUP_AND_ADDRESS, DatagramMode.GROUP_PORT),
                new TestMode(Mode.GROUP, DatagramMode.GROUP_PORT),

                new TestMode(Mode.PORT_ONLY, DatagramMode.GROUP_PORT_0),
                new TestMode(Mode.NIC_NO_PORT, DatagramMode.GROUP_PORT_0),
                new TestMode(Mode.NIC, DatagramMode.GROUP_PORT_0),
                new TestMode(Mode.GROUP_AND_ADDRESS, DatagramMode.GROUP_PORT_0),
                new TestMode(Mode.GROUP, DatagramMode.GROUP_PORT_0),

                new TestMode(Mode.PORT_ONLY, DatagramMode.PLAIN),
                new TestMode(Mode.NIC_NO_PORT, DatagramMode.PLAIN),
                new TestMode(Mode.NIC, DatagramMode.PLAIN),

                // Violates API
                //new TestMode(Mode.GROUP_AND_ADDRESS, DatagramMode.PLAIN),
                //new TestMode(Mode.GROUP, DatagramMode.PLAIN),
        };

        for (TestMode tm : testModes) {
            clientAIsRunning.set(true);
            clientBIsRunning.set(true);
            serverIsRunning.set(true);
            System.out.println("\n=== RUNNING MODE: " + tm.mode + ", DATAGRAM_MODE: " + tm.datagramMode + " ===\n");
            System.out.flush();
            ExecutorService buildService = Executors.newFixedThreadPool(3);
            buildService.submit(new Server(conf, tm.mode, tm.datagramMode));
            buildService.submit(new Client(conf, tm.mode, Name.CLIENT_A, tm.datagramMode));
            buildService.submit(new Client(conf, tm.mode, Name.CLIENT_B, tm.datagramMode));
            buildService.shutdown();
            sleep(2500);
            clientAIsRunning.set(false);
            clientBIsRunning.set(false);
            serverIsRunning.set(false);
            buildService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
