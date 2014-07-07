package org.servalproject.succinct.intraSimulator;

import org.junit.Before;
import org.junit.Test;

public class RhizomeTest {
    private Bundle bundle;

    @Before
    public void setUp() throws Exception {
        bundle = new Bundle(Bundle.BundleType.PACKETS);
    }

    @Test
    public void sameMeshTest() {
        ServalMesh mesh = new ServalMesh();

        ServalNode origin = new ServalNode();
        mesh.addNode(origin);

        ServalNode uplink = new ServalNode(ServalNode.Capability.INLINK_UPLINK);
        mesh.addNode(uplink);

        origin.sendBundle(bundle);
    }

    @Test
    public void joinMeshTest() {
        ServalMesh mesh = new ServalMesh();

        ServalNode origin = new ServalNode();
        mesh.addNode(origin);

        origin.sendBundle(bundle);

        ServalNode uplink = new ServalNode(ServalNode.Capability.INLINK_UPLINK);
        mesh.addNode(uplink);
    }
}