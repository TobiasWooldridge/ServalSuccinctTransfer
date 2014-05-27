package org.servalproject.succinct.simulator;

public class Packet {
    public enum PacketType {
        DATA((byte)1),
        REQ((byte)2);

        private byte code;

        private PacketType(byte code) {
            this.code = code;
        }
    }

    private final PacketType type;
    private final int sequenceNumber;
    private final byte[] payload;

    public Packet(PacketType type, int sequenceNumber, byte[] payload) {
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.payload = payload;
    }


    public PacketType getType() {
        return type;
    }



    public int getSequenceNumber() {
        return sequenceNumber;

    }
    public byte[] getSequenceNumberBytes() {
        return new byte[] { (byte)(sequenceNumber >> 8), (byte)(sequenceNumber) };
    }





    public byte[] checkSum(byte[] data) {
        return new byte[] { (byte)(data.hashCode() >> 8), (byte)(data.hashCode()) };
    }

    public byte[] build() {
        ByteArrayBuilder builder = new ByteArrayBuilder();

        builder.add(getSequenceNumberBytes());
        builder.add(payload);
        builder.add(checkSum(builder.toByteArray()));

        return builder.toByteArray();
    }

    public boolean verifyChecksum() {
        return Math.random() > 0.2;
    }
}
