package org.servalproject.succinct.interSimulator;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConversationTest {

    private byte[] message;
    private List<Packet> packets;
    private PacketEndPoint to, from;

    @Before
    public void setUp() throws Exception {
        PacketSplitter packetMagic = new PacketSplitter();

        message = "Hello, world! This is a test message. Split into fairly short packets.".getBytes(Charset.forName("UTF-8"));

        packets = Arrays.asList(packetMagic.packetize(message, 10, 0));

        to = new PacketEndPoint("Destination");
        from = new PacketEndPoint("Origin");
    }

    @Test
    public void idealConversationWithPacketObjects() {
        from.connectTo(to);

        for (Packet p : packets) {
            from.sendPacket(p);
        }

        org.junit.Assert.assertArrayEquals(message, to.getReceivedStream().toByteArray());
    }

    @Test
    public void shuffledConversationWithPacketObjects() {
        from.connectTo(to);

        Collections.shuffle(packets);
        for (Packet p : packets) {
            from.sendPacket(p);
        }

        org.junit.Assert.assertArrayEquals(message, to.getReceivedStream().toByteArray());
    }

}

