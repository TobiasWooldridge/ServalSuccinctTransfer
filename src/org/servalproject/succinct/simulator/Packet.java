package org.servalproject.succinct.simulator;

public class Packet {
    private final int sequenceNumber;
    private final byte[] data;

    public Packet(int sequenceNumber, byte[] data) {
        this.sequenceNumber = sequenceNumber;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "sequenceNumber=" + sequenceNumber +
                ", data=\"" + new String(data) +
                "\"}";
    }
}
