package io.collective.basic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private final String previousHash;
    private final long timestamp;
    private final int nonce;
    private final String hash;

    public Block(String previousHash, long timestamp, int nonce) throws NoSuchAlgorithmException {
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.hash = calculatedHash();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }

    public String calculatedHash() throws NoSuchAlgorithmException {
        String data = previousHash + Long.toString(timestamp) + Integer.toString(nonce);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes());
        BigInteger hashNum = new BigInteger(1, hashBytes);
        StringBuilder hashString = new StringBuilder(hashNum.toString(16));
        while (hashString.length() < 64) {
            hashString.insert(0, "0");
        }
        return hashString.toString();
    }
}
