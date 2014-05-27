package org.servalproject.succinct.simulator;

import java.nio.charset.Charset;
import java.util.*;

public class Main {
    public static class PacketEndPoint {
        private PacketEndPoint endPoint;

        private String name;

        int nextSeqNumber = 0;

        Map<Integer, Packet> sentPackets = new HashMap<>();
        Map<Integer, Packet> receivedPackets = new HashMap<>();

        public PacketEndPoint(String name) {
            this.name = name;
        }


        public void connectTo(PacketEndPoint pep) {
            endPoint = pep;
            pep.endPoint = this;
        }

        public void sendPacket(Packet packet) {
            sentPackets.put(packet.getSequenceNumber(), packet);
            endPoint.receivePacket(packet);
        }


        private void requestRetransmission(int sequenceNumber) {
            System.out.println(name + ": " + "Requesting retransmission of " + sequenceNumber);
            Packet packet = new Packet(Packet.PacketType.REQ, sequenceNumber, new byte[] {});
            endPoint.receivePacket(packet);
        }

        public void receivePacket(Packet packet) {
            // Verify the hash of the packet to make sure it arrived correctly
            // -> if hash is invalid, drop packet
            if (!packet.verifyChecksum()) {
                System.out.println(name + ": " + "Dropped packet due to invalid hash data");

                // If the packet isn't absurdly far in the future, or in the past, request it is retransmitted
                if (packet.getSequenceNumber() - nextSeqNumber < 10 && !receivedPackets.containsKey(packet.getSequenceNumber())) {
                    requestRetransmission(packet.getSequenceNumber());
                }

                return;
            }


            if (packet.getType() == Packet.PacketType.REQ) {
                if (sentPackets.containsKey(packet.getSequenceNumber())) {
                    sendPacket(sentPackets.get(packet.getSequenceNumber()));
                }
                else {
                    System.out.println(name + ": " + "Other endpoint requested I retransmit packet #" + packet.getSequenceNumber() + ", but I don't have it!");
                }

            }
            else if (packet.getType() == Packet.PacketType.DATA) {
                // If it has already been received, do nothing
                int newSeqNumber = packet.getSequenceNumber();
                if (receivedPackets.containsKey(newSeqNumber) || newSeqNumber < nextSeqNumber) {
                    System.out.println(name + ": " + "Received packet #" + newSeqNumber + " a second time");
                    return;
                }

                // Add the packet to our buffer
                receivedPackets.put(newSeqNumber, packet);

                // Don't process further if it's not the next packet
                if (newSeqNumber > nextSeqNumber) {
                    System.out.println(name + ": " + "Received packet #" + newSeqNumber + " early, waiting for packet " + nextSeqNumber);
                    return;
                }

                // While we have a valid "next packet" to process, process it
                while (receivedPackets.containsKey(nextSeqNumber)) {
                    Packet p = receivedPackets.remove(nextSeqNumber);

                    System.out.println(name + ": " + "Done with packet " + p.getSequenceNumber());

                    nextSeqNumber++;
                }
            }
        }
    }

    public static void main(String[] args) {
        PacketSplitter packetMagic = new PacketSplitter();

        byte[] message = "Hello, world! This is a message. Split into 10 char long packets.".getBytes(Charset.forName("UTF-8"));

        Packet[] packets = packetMagic.packetize(message, 10, 0);

        PacketEndPoint from = new PacketEndPoint("Origin");
        PacketEndPoint to = new PacketEndPoint("Destination");

        from.connectTo(to);

        Random rand = new Random();

        for (int i = 0; i < 20; i++) {
            Packet packet = packets[rand.nextInt(packets.length)];
            System.out.println("NOW SENDING PACKET " + packet.getSequenceNumber());
            from.sendPacket(packet);
        }
    }
}
