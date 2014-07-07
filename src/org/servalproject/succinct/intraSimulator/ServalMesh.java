package org.servalproject.succinct.intraSimulator;

import java.util.*;

public class ServalMesh {
    Collection<ServalNode> nodes = new HashSet<>();

    public ServalMesh() {

    }

    public ServalMesh(ServalNode... nodes) {
        this.nodes.addAll(Arrays.asList(nodes));
    }

    public void addNode(ServalNode node) {
        if (node.getMesh() != this) {
            node.joinMesh(this);
        }
        else {
            nodes.add(node);
        }

        // Stimulate 'onNewMeshNodes' event on all mesh nodes
        for (ServalNode n : nodes) {
            n.onNewMeshNodes();
        }
    }

    public void removeNode(ServalNode node) {
        nodes.remove(node);
    }

    public List<ServalNode> getNodes() {
        return new ArrayList(nodes);
    }

    public boolean singular() {
        return nodes.size() == 1;
    }

    public void broadcast(Bundle bundle) {
        for (ServalNode node : nodes) {
            node.saveBundle(bundle);
        }
    }
}
