package org.servalproject.succinct.interSimulator;

import java.util.LinkedList;
import java.util.List;

public class ByteArrayBuilder {
    List<byte[]> data = new LinkedList<>();
    int size = 0;

    public void add(byte[] arr) {
        data.add(arr.clone());
        size += arr.length;
    }
    public void add(byte b) {
        data.add(new byte[] { b });
        size += 1;
    }

    public byte[] toByteArray() {
        byte[] compiled = new byte[size];

        int copiedBytes = 0;
        for (byte[] fragment : data) {
            System.arraycopy(fragment, 0, compiled, copiedBytes, fragment.length);
            copiedBytes += fragment.length;
        }

        data.clear();
        data.add(compiled);

        return compiled;
    }

    public int size() {
        return size;
    }
}
