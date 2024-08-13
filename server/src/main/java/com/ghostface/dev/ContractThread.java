package com.ghostface.dev;

public abstract class ContractThread extends Thread {

    protected boolean isSuccessful;

    public ContractThread() {
    }

    public abstract boolean isSuccessful();
}
