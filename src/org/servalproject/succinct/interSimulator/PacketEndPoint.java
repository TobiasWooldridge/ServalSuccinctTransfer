package org.servalproject.succinct.interSimulator;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PacketEndPoint {
    private PacketEndPoint endPoint;

    private String name;

    private int nextSeqNumber = 0;

    private Map<Integer, Packet> sentPackets = new HashMap<>();
    private Map<Integer, Packet> receivedPackets = new HashMap<>();

    private ByteArrayOutputStream receivedData = new ByteArrayOutputStream();

    public PacketEndPoint(String name) {
        this.name = name;
    }

    public void connectTo(PacketEndPoint pep) {
        endPoint = pep;
        pep.endPoint = this;
    }

    public void sendPacket(Packet packet) {
        sentPackets.put(packet.getSeqNum(), packet);
        endPoint.receivePacket(packet);
    }

    private void requestRetransmission(int sequenceNumber) {
        System.out.println(name + ": Requesting retransmission of " + sequenceNumber);
        Packet packet = new Packet(Packet.PacketType.REQ, sequenceNumber, new byte[] {});
        endPoint.receivePacket(packet);
    }

    public ByteArrayOutputStream getReceivedStream() {
        return receivedData;
    }

    public void receivePacket(Packet packet) {
        // Verify the hash of the packet to make sure it arrived correctly
        // -> if hash is invalid, drop packet
        if (!packet.verifyChecksum()) {
            System.out.println(name + ": Dropped packet due to invalid hash data");

            // If the packet isn't absurdly far in the future, or in the past, request it is retransmitted
            if (packet.getSeqNum() - nextSeqNumber < 10 && !receivedPackets.containsKey(packet.getSeqNum())) {
                requestRetransmission(packet.getSeqNum());
            }

            return;
        }


        if (packet.getType() == Packet.PacketType.REQ) {
            if (sentPackets.containsKey(packet.getSeqNum())) {
                System.out.println(name + ": Retransmitting packet #" + packet.getSeqNum() + " per request");
                sendPacket(sentPackets.get(packet.getSeqNum()));
            }
            else {
                System.out.println(name + ": Other endpoint requested I retransmit packet #" + packet.getSeqNum() + ", but I don't have it!");
            }

        }
        else if (packet.getType() == Packet.PacketType.DATA) {
            // If it has already been received, do nothing
            int newSeqNumber = packet.getSeqNum();
            if (receivedPackets.containsKey(newSeqNumber) || newSeqNumber < nextSeqNumber) {
                System.out.println(name + ": Received packet #" + newSeqNumber + " a second time");
                return;
            }

            // Add the packet to our buffer
            receivedPackets.put(newSeqNumber, packet);

            // Don't process further if it's not the next packet
            if (newSeqNumber > nextSeqNumber) {
                System.out.println(name + ": Received packet #" + newSeqNumber + " early, waiting for packet " + nextSeqNumber);
                return;
            }

            // While we have a valid "next packet" to process, process it
            while (receivedPackets.containsKey(nextSeqNumber)) {
                Packet p = receivedPackets.get(nextSeqNumber);
                byte payload[] = p.getPayload();

                receivedData.write(payload, 0, payload.length);

                System.out.println(name + ": Done with packet " + p.getSeqNum());

                nextSeqNumber++;
            }
        }
    }
}
