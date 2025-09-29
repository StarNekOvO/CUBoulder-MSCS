package io.collective.basic;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    public void add(Block block) {
        chain.add(block);
    }

    public int size() {
        return chain.size();
    }

    public boolean isValid() throws NoSuchAlgorithmException {
        if (chain.size() < 2) {
            return true; // Genesis block case
        }

        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Check if the hash of the block is correct
            if (!currentBlock.getHash().equals(currentBlock.calculatedHash())) {
                return false;
            }

            // Check if the previousHash matches
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            // Check if the block is mined correctly
            if (!isMined(currentBlock)) {
                return false;
            }
        }
        return true;
    }

    public static Block mine(Block block) throws NoSuchAlgorithmException {
        Block mined = new Block(block.getPreviousHash(), block.getTimestamp(), block.getNonce());

        while (!isMined(mined)) {
            mined = new Block(mined.getPreviousHash(), mined.getTimestamp(), mined.getNonce() + 1);
        }
        return mined;
    }

    public static boolean isMined(Block minedBlock) throws NoSuchAlgorithmException {
        return minedBlock.getHash().startsWith("00"); // Example of simple proof of work condition
    }
}
