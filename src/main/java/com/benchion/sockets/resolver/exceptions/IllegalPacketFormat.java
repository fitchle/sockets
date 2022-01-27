package com.benchion.sockets.resolver.exceptions;

/**
 * triggered when specified packet is in illegal format
 */
public class IllegalPacketFormat extends Exception {
    public IllegalPacketFormat(String errorMessage) {
        super(errorMessage);
    }
}
