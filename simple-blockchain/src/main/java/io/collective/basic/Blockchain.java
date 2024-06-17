package io.collective.basic;

import java.security.NoSuchAlgorithmException;

public class Blockchain {

    public boolean isEmpty() {
        return false;
    }

    public void add(Block block) {
    }

    public int size() {
        return 0;
    }

    public boolean isValid() throws NoSuchAlgorithmException {

        // todo - check mined

        // todo - check previous hash matches

        // todo - check hash is correctly calculated

        return false;
    }

    /// Supporting functions that you'll need.

    public static Block mine(Block block) throws NoSuchAlgorithmException {
        Block mined = new Block(block.getPreviousHash(), block.getTimestamp(), block.getNonce());

        while (!isMined(mined)) {
            mined = new Block(mined.getPreviousHash(), mined.getTimestamp(), mined.getNonce() + 1);
        }
        return mined;
    }

    public static boolean isMined(Block minedBlock) {
        return minedBlock.getHash().startsWith("00");
    }
}