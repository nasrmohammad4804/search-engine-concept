package com.nasr.crawlerservice.util;

import java.math.BigInteger;

public class SynchronizedBigInteger {
    private BigInteger value;


    public SynchronizedBigInteger(BigInteger initialValue) {
        this.value = initialValue;
    }

    public synchronized BigInteger get() {
        return value;
    }

    public synchronized void set(BigInteger newValue) {
        this.value = newValue;
    }

    public synchronized BigInteger addAndGet(BigInteger delta) {
        this.value = this.value.add(delta);
        return this.value;
    }

    public synchronized BigInteger incrementAndGet() {
        this.value = this.value.add(BigInteger.ONE);
        return this.value;
    }

    public synchronized boolean compareAndSet(BigInteger expectedValue, BigInteger newValue) {
        if (this.value.equals(expectedValue)) {
            this.value = newValue;
            return true;
        }
        return false;
    }
}
