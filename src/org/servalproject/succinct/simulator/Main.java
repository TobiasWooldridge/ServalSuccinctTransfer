package org.servalproject.succinct.simulator;

import java.nio.charset.Charset;
import java.util.*;

public class Main {
    public static class PacketEndPoint {
        private PacketEndPoint endPoint;

        int nextSeqNumber = 0;

        Map<Integer, Packet> receivedPackets = new HashMap<>();

        public void connectTo(PacketEndPoint pep) {
            endPoint = pep;
            pep.endPoint = endPoint;
        }

        public void sendPacket(Packet packet) {
            endPoint.receivePacket(packet);
        }

        public void receivePacket(Packet packet) {
            // Verify the hash of the packet to make sure it arrived correctly
            // -> if hash is invalid, drop packet
            if (!packet.verifyChecksum()) {
                System.err.println("Dropped illegal packet " + packet.getSequenceNumber());
                return;
            }

            if (packet.getType() == Packet.PacketType.DATA) {
                // If it has already been received, do nothing
                int newSeqNumber = packet.getSequenceNumber();
                if (receivedPackets.containsKey(newSeqNumber) || newSeqNumber < nextSeqNumber) {
                    System.err.println("Received packet #" + newSeqNumber + " a second time");
                    return;
                }

                // Add the packet to our buffer
                receivedPackets.put(newSeqNumber, packet);

                // Don't process further if it's not the next packet
                if (newSeqNumber > nextSeqNumber) {
                    System.out.println("Received packet #" + newSeqNumber + " early, waiting for packet " + nextSeqNumber);
                    return;
                }

                // While we have a valid "next packet" to process, process it
                while (receivedPackets.containsKey(nextSeqNumber)) {
                    Packet p = receivedPackets.remove(nextSeqNumber);

                    System.out.println("Done with packet " + p.getSequenceNumber());

                    nextSeqNumber++;
                }
            }
        }
    }

    public static void main(String[] args) {
        PacketSplitter packetMagic = new PacketSplitter();

        byte[] message = "Hello, world! This is a message. Split into 10 char long packets.".getBytes(Charset.forName("UTF-8"));

        Packet[] packets = packetMagic.packetize(message, 10, 0);

        PacketEndPoint from = new PacketEndPoint();
        PacketEndPoint to = new PacketEndPoint();

        from.connectTo(to);

        Random rand = new Random();

        for (int i = 0; i < 20; i++) {
            from.sendPacket(packets[rand.nextInt(packets.length)]);
        }
    }
}
