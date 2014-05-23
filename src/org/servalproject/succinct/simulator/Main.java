package org.servalproject.succinct.simulator;

import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        Packetizer packetMagic = new Packetizer();

        byte[] message = "Hello, world! This is a message. Split into 10 char long packets.".getBytes(Charset.forName("UTF-8"));

        byte[][] fragments = packetMagic.split(message, 10);

        Packet[] packets = packetMagic.packetize(message, 10);

        for (Packet p : packets) {
            System.out.println(p);
        }
    }
}
