package tukano.impl.java;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>A class interface to perform service discovery based on periodic
 * announcements over multicast communication.</p>
 *
 */

public interface Discovery {

    /**
     * Used to announce the URI of the given service name.
     * @param serviceName - the name of the service
     * @param serviceURI - the uri of the service
     */
    public void announce(String serviceName, String serviceURI);

    /**
     * Get discovered URIs for a given service name
     * @param serviceName - name of the service
     * @param minReplies - minimum number of requested URIs. Blocks until the number is satisfied.
     * @return array with the discovered URIs for the given service name.
     */
    public URI[] knownUrisOf(String serviceName, int minReplies) throws InterruptedException;

    /**
     * Get the instance of the Discovery service
     * @return the singleton instance of the Discovery service
     */
    public static Discovery getInstance() {
        return DiscoveryImpl.getInstance();
    }
}

/**
 * Implementation of the multicast discovery service
 */
class DiscoveryImpl implements Discovery {

    private static Logger Log = Logger.getLogger(Discovery.class.getName());

    // The pre-aggreed multicast endpoint assigned to perform discovery.

    static final int DISCOVERY_RETRY_TIMEOUT = 5000;
    static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;

    // Replace with appropriate values: allowed IP Multicast range: 224.0.0.1 - 239.255.255.255
    static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("224.5.26.3", 5555);

    // Used to separate the two fields that make up a service announcement.
    private static final String DELIMITER = "\t";

    private static final int MAX_DATAGRAM_SIZE = 65536;

    private static Discovery singleton;

    private final Map<String, Set<URI>> knownUrisMap = new ConcurrentHashMap<>();

    synchronized static Discovery getInstance() {
        if (singleton == null) {
            singleton = new DiscoveryImpl();
        }
        return singleton;
    }

    private DiscoveryImpl() {
        this.startListener();
    }

    @Override
    public void announce(String serviceName, String serviceURI) {
        Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName,
                serviceURI));

        var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
        var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);

        // start thread to send periodic announcements
        new Thread(() -> {
            try (var ds = new DatagramSocket()) {
                while (true) {
                    try {
                        ds.send(pkt);
                        Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    synchronized public URI[] knownUrisOf(String serviceName, int minEntries) throws InterruptedException {
            // TODO: use already stored annoucements,
            // or wait if necessary to have the number of entries requested...
            while (this.knownUrisMap.get(serviceName) == null || this.knownUrisMap.get(serviceName).size() < minEntries) {
                Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
            }
            Set<URI> knownUris = knownUrisMap.get(serviceName);
            return knownUris.toArray(new URI[knownUris.size()]);

    }
    private void startListener() {
        Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(),
                DISCOVERY_ADDR.getPort()));

        new Thread(() -> {
            try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
                ms.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                for (;;) {
                    try {
                        var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
                        ms.receive(pkt);

                        var msg = new String(pkt.getData(), 0, pkt.getLength());
                        Log.info(String.format("Received: %s", msg));

                        var parts = msg.split(DELIMITER);
                        if (parts.length == 2) {
                            // TODO: complete by storing the decoded announcements...
                            var serviceName = parts[0];
                            var uri = URI.create(parts[1]);
                            if(!knownUrisMap.containsKey(serviceName))
                                knownUrisMap.put(serviceName, new HashSet<URI>());
                            knownUrisMap.get(serviceName).add(uri);
                        }

                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }).start();
    }
}