package org.servalproject.succinct.intraSimulator;

import java.util.*;

public class ServalNode {
    public static enum Capability {
        INLINK_UPLINK,
        SMS_UPLINK
    }

    private ServalMesh mesh;

    private Collection<Capability> capabilities = new HashSet<>();

    private Map<Bundle.BundleToken, Bundle> storeAndForwardBuffer = new HashMap<>();
    private Collection<Bundle.BundleToken> killTokens = new HashSet<>();

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


    public void sendBundleOverUplink(Bundle bundle) {
        if (capabilities.isEmpty()) {
            throw new IllegalStateException("This serval node cannot transmit a packet as it lacks any uplink capabilities");
        }

        System.out.println("Transmitting packet #" + bundle.getId());
    }

    void saveBundle(Bundle bundle) {
        if (bundle.getBundleType() == Bundle.BundleType.KILL || !storeAndForwardBuffer.containsKey(bundle.getToken())) {
            storeAndForwardBuffer.put(bundle.getToken(), bundle);
        }
    }   

    public void sendBundle(Bundle bundle) {
        ServalNode uplinkNode = null;

        if (!capabilities.isEmpty()) {
            // Elect ourselves to send it
            uplinkNode = this;
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
                uplinkNode = capableNeighbours.get(new Random().nextInt(capableNeighbours.size()));
            }
        }

        // Upload the packet immediately
        if (uplinkNode != null) {
            sendBundleOverUplink(bundle);
        }
        // Otherwise try store-and-forwarding it to an uplink
        else {
            mesh.broadcast(bundle);
        }
    }

    public void onNewMeshNodes() {
        for (Bundle bundle : storeAndForwardBuffer.values()) {
            sendBundle(bundle);
        }
    }
}
