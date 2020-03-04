# Datagram Hell
A toy for experimenting with UDP multicast on Solaris/Windows/Linux.
Some combinations of datagram destination and MulticastSocket configuration
are invalid, some are valid on Linux/Unix and are not on Windows.
It is deliberate to observe the stability of the behaviour.

## Usage

1. Prepare [settings.properties](./src/main/resources/settings.properties) for your system. You can override those with sys props and env vars (all caps, underscores) too.

1. Build and run
    ```
    mvn clean package
    java -Djava.net.preferIPv4Stack=true \
         -jar target/datagram-hell-1.0-SNAPSHOT-jar-with-dependencies.jar \
         -c=./settings.properties | tee log.log
    ```

## Example runs

What follows are example runs for [Solaris 11](#solaris-11), [Fedora 25](#fedora-25) and [Windows 2019](#windows-2019) with various JDKs (OpenJDK HotSpot 8, J9 11, GraalVM 11 v20.0).

### Solaris 11

```
SunOS 5.11 11.3 sun4v sparc sun4v

ifconfig -a
lo0: flags=2001000849<UP,LOOPBACK,RUNNING,MULTICAST,IPv4,VIRTUAL> mtu 8232 index 1
        inet 127.0.0.1 netmask ff000000
net0: flags=100001000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4,PHYSRUNNING> mtu 1500 index 2
        inet 10.16.88.16 netmask fffff800 broadcast 10.16.95.255
        ether 2:8:20:f1:6a:5f
net0:1: flags=100001000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4,PHYSRUNNING> mtu 1500 index 2
        inet 10.16.179.112 netmask fffff800 broadcast 10.16.183.255
net0:2: flags=100001000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4,PHYSRUNNING> mtu 1500 index 2
        inet 10.16.179.113 netmask fffff800 broadcast 10.16.183.255
net0:3: flags=100001000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4,PHYSRUNNING> mtu 1500 index 2
        inet 10.16.179.114 netmask fffff800 broadcast 10.16.183.255
net0:4: flags=100001000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4,PHYSRUNNING> mtu 1500 index 2
        inet 10.16.179.115 netmask fffff800 broadcast 10.16.183.255
lo0: flags=2002000849<UP,LOOPBACK,RUNNING,MULTICAST,IPv6,VIRTUAL> mtu 8252 index 1
        inet6 ::1/128
net0: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 fe80::8:20ff:fef1:6a60/10
        ether 2:8:20:f1:6a:5f
net0:1: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 fe80::8:20ff:fef1:6a61/10
net0:2: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 fe80::8:20ff:fef1:6a5f/10
net0:3: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 2620:52:0:105f::ffff:208/64
net0:4: flags=120002080841<UP,RUNNING,MULTICAST,ADDRCONF,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 2620:52:0:105f:8:20ff:fef1:6a5f/64
net0:5: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 2620:52:0:105f::ffff:209/64
net0:6: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 2620:52:0:105f::ffff:210/64
net0:7: flags=120002000841<UP,RUNNING,MULTICAST,IPv6,PHYSRUNNING> mtu 1500 index 2
        inet6 2620:52:0:105f::ffff:211/64

java -version
java version "1.8.0_112"
Java(TM) SE Runtime Environment (build 1.8.0_112-b15)
Java HotSpot(TM) 64-Bit Server VM (build 25.112-b15, mixed mode)

java -Djava.net.preferIPv4Stack=true -jar datagram-hell.jar -c=./solaris.settings.properties | tee log.log
Using configuration:
Configuration used:
 udpPort         23364
 serverNic       net0
 serverUdpGroup  /224.0.1.105
 serverAddress   /10.16.88.16
 clientAnic      net0
 clientAudpGroup /224.0.1.105
 clientAaddress  /10.16.88.16
 clientBnic      net0
 clientBudpGroup /224.0.1.105
 clientBAddress  /10.16.88.16


=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
CLIENT_A received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.16.88.16:23364
SERVER terminated.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
CLIENT_B received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.16.88.16:23364
SERVER terminated.

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
CLIENT_B received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #1 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.16.88.16:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.16.88.16:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.16.88.16:23364
SERVER terminated.

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT_0 ===

CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
SERVER terminated.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.

=== RUNNING MODE: NIC, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC net0
SERVER: Selected -> /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER:            /10.16.179.115
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC net0
CLIENT_B: Selected -> /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B:            /10.16.179.115
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC net0
CLIENT_A: Selected -> /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A:            /10.16.179.115
CLIENT_A is ready.
```

### Fedora 25

```
Linux x86_64

ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host
       valid_lft forever preferred_lft forever
2: enp0s31f6: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc fq_codel state UP group default qlen 1000
    link/ether c8:5b:76:33:37:b5 brd ff:ff:ff:ff:ff:ff
    inet 10.40.4.208/23 brd 10.40.5.255 scope global dynamic enp0s31f6
       valid_lft 43796sec preferred_lft 43796sec
    inet6 2620:52:0:2804:aa70:f7bd:38fa:59ac/64 scope global noprefixroute dynamic
       valid_lft 2591861sec preferred_lft 604661sec
    inet6 fe80::be7:2eb5:a573:8083/64 scope link
       valid_lft forever preferred_lft forever
3: wlp3s0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 1000
    link/ether f0:d5:bf:68:4a:2f brd ff:ff:ff:ff:ff:ff
    inet 10.200.154.147/21 brd 10.200.159.255 scope global dynamic wlp3s0
       valid_lft 2685sec preferred_lft 2685sec
    inet6 fe80::a4f6:d899:2f3c:45cc/64 scope link
       valid_lft forever preferred_lft forever
4: virbr0: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN group default qlen 1000
    link/ether 52:54:00:b8:3b:22 brd ff:ff:ff:ff:ff:ff
    inet 192.168.122.1/24 brd 192.168.122.255 scope global virbr0
       valid_lft forever preferred_lft forever
5: virbr0-nic: <BROADCAST,MULTICAST> mtu 1500 qdisc fq_codel master virbr0 state DOWN group default qlen 1000
    link/ether 52:54:00:b8:3b:22 brd ff:ff:ff:ff:ff:ff
6: virbr1: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN group default qlen 1000
    link/ether 52:54:00:36:d9:ad brd ff:ff:ff:ff:ff:ff
    inet 192.168.42.1/24 brd 192.168.42.255 scope global virbr1
       valid_lft forever preferred_lft forever
7: virbr1-nic: <BROADCAST,MULTICAST> mtu 1500 qdisc fq_codel master virbr1 state DOWN group default qlen 1000
    link/ether 52:54:00:36:d9:ad brd ff:ff:ff:ff:ff:ff

java -version
openjdk version "11.0.5" 2019-10-15
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.5+10)
Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.17.0, JRE 11 Linux amd64-64-Bit 20191016_322 (JIT enabled, AOT enabled)
OpenJ9   - 77c1cf708
OMR      - 20db4fbc
JCL      - 2a7af5674b based on jdk-11.0.5+10)

java -Djava.net.preferIPv4Stack=true -jar datagram-hell.jar -c=./settings.properties | tee log.log
Using configuration:
Configuration used:
 udpPort         23364
 serverNic       enp0s31f6
 serverUdpGroup  /224.0.1.105
 serverAddress   /10.40.4.208
 clientAnic      enp0s31f6
 clientAudpGroup /224.0.1.105
 clientAaddress  /10.40.4.208
 clientBnic      enp0s31f6
 clientBudpGroup /224.0.1.105
 clientBAddress  /10.40.4.208


=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_B received data: Hello #0 from SERVER from: /10.40.4.208:23364
CLIENT_A is ready.
CLIENT_B received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.40.4.208:23364
SERVER terminated.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
SERVER terminated.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
SERVER terminated.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
CLIENT_A received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.40.4.208:23364
SERVER terminated.

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #1 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #2 from SERVER from: /10.40.4.208:23364
CLIENT_A received data: Hello #3 from SERVER from: /10.40.4.208:23364
CLIENT_B received data: Hello #3 from SERVER from: /10.40.4.208:23364
SERVER terminated.

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT_0 ===

CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.

=== RUNNING MODE: NIC, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC enp0s31f6
SERVER: Selected -> /10.40.4.208
SERVER is ready.
CLIENT_A: Enumerating addresses for NIC enp0s31f6
CLIENT_A: Selected -> /10.40.4.208
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC enp0s31f6
CLIENT_B: Selected -> /10.40.4.208
CLIENT_B is ready.
```

### Windows 2019

```
Windows 2019

C:\Users\Administrator
λ java -version
openjdk version "11.0.6" 2020-01-14
OpenJDK Runtime Environment GraalVM CE 20.0.0 (build 11.0.6+9-jvmci-20.0-b02)
OpenJDK 64-Bit Server VM GraalVM CE 20.0.0 (build 11.0.6+9-jvmci-20.0-b02, mixed mode, sharing)

λ ipconfig /all

Windows IP Configuration

   Host Name . . . . . . . . . . . . : WIN-113T3VF8H0I
   Primary Dns Suffix  . . . . . . . :
   Node Type . . . . . . . . . . . . : Hybrid
   IP Routing Enabled. . . . . . . . : No
   WINS Proxy Enabled. . . . . . . . : No

Ethernet adapter Ethernet:

   Connection-specific DNS Suffix  . :
   Description . . . . . . . . . . . : Realtek RTL8139C+ Fast Ethernet NIC
   Physical Address. . . . . . . . . : 52-54-00-EE-BF-82
   DHCP Enabled. . . . . . . . . . . : Yes
   Autoconfiguration Enabled . . . . : Yes
   Link-local IPv6 Address . . . . . : fe80::296b:69b3:3869:3f78%6(Preferred)
   IPv4 Address. . . . . . . . . . . : 192.168.122.107(Preferred)
   Subnet Mask . . . . . . . . . . . : 255.255.255.0
   Lease Obtained. . . . . . . . . . : Sunday, January 27, 1884 3:56:15 PM
   Lease Expires . . . . . . . . . . : Wednesday, March 4, 2020 2:24:32 PM
   Default Gateway . . . . . . . . . : 192.168.122.1
   DHCP Server . . . . . . . . . . . : 192.168.122.1
   DHCPv6 IAID . . . . . . . . . . . : 72504320
   DHCPv6 Client DUID. . . . . . . . : 00-01-00-01-23-EA-40-CD-52-54-00-EE-BF-82
   DNS Servers . . . . . . . . . . . : 192.168.122.1
   NetBIOS over Tcpip. . . . . . . . : Enabled

Ethernet adapter Ethernet 2:

   Connection-specific DNS Suffix  . :
   Description . . . . . . . . . . . : Realtek RTL8139C+ Fast Ethernet NIC #2
   Physical Address. . . . . . . . . : 52-54-00-46-7F-3B
   DHCP Enabled. . . . . . . . . . . : Yes
   Autoconfiguration Enabled . . . . : Yes
   Link-local IPv6 Address . . . . . : fe80::99ec:4ebf:4d1e:80af%11(Preferred)
   IPv4 Address. . . . . . . . . . . : 192.168.42.95(Preferred)
   Subnet Mask . . . . . . . . . . . : 255.255.255.0
   Lease Obtained. . . . . . . . . . : Sunday, January 27, 1884 3:56:15 PM
   Lease Expires . . . . . . . . . . : Wednesday, March 4, 2020 2:24:32 PM
   Default Gateway . . . . . . . . . :
   DHCP Server . . . . . . . . . . . : 192.168.42.1
   DHCPv6 IAID . . . . . . . . . . . : 324162560
   DHCPv6 Client DUID. . . . . . . . : 00-01-00-01-23-EA-40-CD-52-54-00-EE-BF-82
   DNS Servers . . . . . . . . . . . : 192.168.42.1
   NetBIOS over Tcpip. . . . . . . . : Enabled

Ethernet adapter Ethernet 3:

   Connection-specific DNS Suffix  . :
   Description . . . . . . . . . . . : Realtek RTL8139C+ Fast Ethernet NIC #3
   Physical Address. . . . . . . . . : 52-54-00-A2-30-F5
   DHCP Enabled. . . . . . . . . . . : Yes
   Autoconfiguration Enabled . . . . : Yes
   Link-local IPv6 Address . . . . . : fe80::953d:8d6e:867e:4f5c%10(Preferred)
   IPv4 Address. . . . . . . . . . . : 192.168.122.67(Preferred)
   Subnet Mask . . . . . . . . . . . : 255.255.255.0
   Lease Obtained. . . . . . . . . . : Sunday, January 27, 1884 3:56:15 PM
   Lease Expires . . . . . . . . . . : Wednesday, March 4, 2020 2:24:32 PM
   Default Gateway . . . . . . . . . : 192.168.122.1
   DHCP Server . . . . . . . . . . . : 192.168.122.1
   DHCPv6 IAID . . . . . . . . . . . : 391271424
   DHCPv6 Client DUID. . . . . . . . : 00-01-00-01-23-EA-40-CD-52-54-00-EE-BF-82
   DNS Servers . . . . . . . . . . . : 192.168.122.1
   NetBIOS over Tcpip. . . . . . . . : Enabled



Using configuration:
Configuration used:
 udpPort         23364
 serverNic       eth3
 serverUdpGroup  /224.0.1.105
 serverAddress   /192.168.122.67
 clientAnic      eth3
 clientAudpGroup /224.0.1.105
 clientAaddress  /192.168.122.67
 clientBnic      eth3
 clientBudpGroup /224.0.1.105
 clientBAddress  /192.168.122.67


=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
CLIENT_A received data: Hello #1 from SERVER from: /192.168.122.107:23364
CLIENT_B received data: Hello #1 from SERVER from: /192.168.122.107:23364
CLIENT_A received data: Hello #2 from SERVER from: /192.168.122.107:23364
CLIENT_B received data: Hello #2 from SERVER from: /192.168.122.107:23364
CLIENT_A received data: Hello #3 from SERVER from: /192.168.122.107:23364
CLIENT_B received data: Hello #3 from SERVER from: /192.168.122.107:23364
SERVER terminated.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT ===

CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: GROUP_PORT_0 ===

CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
SERVER terminated.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: NIC, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
SERVER terminated.

=== RUNNING MODE: GROUP_AND_ADDRESS, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95

=== RUNNING MODE: GROUP, DATAGRAM_MODE: GROUP_PORT_0 ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95

=== RUNNING MODE: PORT_ONLY, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.

=== RUNNING MODE: NIC_NO_PORT, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.

=== RUNNING MODE: NIC, DATAGRAM_MODE: PLAIN ===

SERVER: Enumerating addresses for NIC eth3
SERVER: Selected -> /192.168.42.95
SERVER is ready.
CLIENT_B: Enumerating addresses for NIC eth3
CLIENT_B: Selected -> /192.168.42.95
CLIENT_B is ready.
CLIENT_A: Enumerating addresses for NIC eth3
CLIENT_A: Selected -> /192.168.42.95
CLIENT_A is ready.
```