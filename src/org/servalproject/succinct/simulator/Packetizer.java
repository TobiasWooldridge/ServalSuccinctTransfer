package org.servalproject.succinct.simulator;

public class Packetizer {
    public byte[][] split(byte[] data, int fragmentSize) {
        int numFragments = (int)Math.ceil((double)data.length / fragmentSize);

        byte[][] fragments = new byte[numFragments][];

        for (int fragmentIndex = 0; fragmentIndex < numFragments; fragmentIndex++) {
            int fragmentStart = fragmentIndex * fragmentSize;
            int fragmentEnd = Math.min(data.length, fragmentStart + fragmentSize);

            byte[] partition = new byte[fragmentEnd - fragmentStart];

            for (int j = fragmentStart; j < fragmentEnd; j++) {
                partition[j - fragmentStart] = data[j];
            }

            fragments[fragmentIndex] = partition;
        }

        return fragments;
    }

    public Packet[] packetize(byte[] data, int packetSize) {
        int numPackets = (int)Math.ceil((double)data.length / packetSize);

        Packet[] packets = new Packet[numPackets];

        byte[][] textFragments = split(data, packetSize);

        for (int i = 0; i < numPackets; i++) {
            packets[i] = new Packet(packetSize, textFragments[i]);
        }

        return packets;
    }
}
