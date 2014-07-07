package org.servalproject.succinct.intraSimulator;

public class Bundle {
    private BundleToken token;
    private BundleType bundleType;

    public class BundleToken {
    }

    public enum BundleType {
        PACKETS,
        KILL
    }
    public Bundle(BundleType bt) {
        this.bundleType = bt;
        this.token = new BundleToken();
    }

    public String getId() {
        return Math.random() + "";
    }

    public BundleType getBundleType() {
        return bundleType;
    }

    public BundleToken getToken() {
        return token;
    }
}
