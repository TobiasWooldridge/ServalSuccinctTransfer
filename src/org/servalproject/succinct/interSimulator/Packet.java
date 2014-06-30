package org.servalproject.succinct.interSimulator;

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
    private final int seqNum;
    private final byte[] payload;

    public Packet(PacketType type, int seqNum, byte[] payload) {
        this.type = type;
        this.seqNum = seqNum;
        this.payload = payload;
    }

    public PacketType getType() {
        return type;
    }

    public int getSeqNum() {
        return seqNum;
    }
    public byte[] getSequenceNumberBytes() {
        return new byte[] { (byte)(seqNum >> 8), (byte)(seqNum) };
    }

    public byte[] checkSum(byte[] data) {
        return new byte[] { (byte)(data.hashCode() >> 8), (byte)(data.hashCode()) };
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] build() {
        ByteArrayBuilder builder = new ByteArrayBuilder();

        builder.add(getSequenceNumberBytes());
        builder.add(payload);
        builder.add(checkSum(builder.toByteArray()));

        return builder.toByteArray();
    }

    public boolean verifyChecksum() {
        return true;
    }
}
