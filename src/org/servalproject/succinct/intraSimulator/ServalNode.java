package org.servalproject.succinct.intraSimulator;

import org.servalproject.succinct.interSimulator.Packet;

import java.util.*;

public class ServalNode {
    public static enum Capability {
        INLINK_UPLINK,
        SMS_UPLINK
    }

    private ServalMesh mesh;

    private Collection<Capability> capabilities = new HashSet<>();

    public ServalNode() {
        mesh = new ServalMesh(this);
    }

    public ServalNode(Capability... capabilities) {
         this.capabilities.addAll(Arrays.asList(capabilities));
    }

    public void leaveMesh() {
        // Only bother leaving the current mesh if we're not alone in it
        if (mesh != null && mesh.getNodes().size() > 1) {
            mesh.removeNode(this);

            // Create a mesh with this node all by itself
            mesh = new ServalMesh(this);
        }
    }

    public ServalMesh getMesh() {
        return mesh;
    }

    void joinMesh(ServalMesh mesh) {
        // Leave the previous mesh
        if (mesh != null) {
            mesh.removeNode(this);
        }

        // Join the new mesh
        this.mesh = mesh;
        mesh.addNode(this);
    }


    public void transmitPacket(Packet packet) {
        if (capabilities.isEmpty()) {
            throw new IllegalStateException("This serval node cannot transmit a packet as it lacks any uplink capabilities");
        }

        System.out.println("Transmitting packet #" + packet.getSeqNum());
    }

    public void storeAndForwardPacket(Packet packet) {

    }

    public void broadcastPacket(Packet packet) {
        if (!capabilities.isEmpty()) {
            // Okay, we send it ourselves
            transmitPacket(packet);
        }
        else if (mesh.singular()) {
            storeAndForwardPacket(packet);
        }
        else {
            // Check if anybody in the mesh (i.e. neighbours) have the capability to transmit it
            List<ServalNode> capableNeighbours = new ArrayList<>();
            for (ServalNode neighbor : mesh.getNodes()) {
                if (!neighbor.capabilities.isEmpty()) {
                    capableNeighbours.add(neighbor);
                }
            }

            // Sunshine scenario: a neighbour can upload the packet immediately
            if (!capableNeighbours.isEmpty()) {
                // Choose a random neighbour
                ServalNode transmitNode = capableNeighbours.get(new Random().nextInt(capableNeighbours.size()));

                // Transmit it via that neighbour
                transmitNode.transmitPacket(packet);
            }

            // Otherwise, everybody can store and forward it
            else {
                for (ServalNode neighbour : mesh.getNodes()) {
                    neighbour.storeAndForwardPacket(packet);
                }
            }

        }
    }
}
