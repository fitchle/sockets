package com.benchion.sockets.packet.exceptions;

/**
 * That function is triggered when an illegal packet is sent or received.
 */
public final class IllegalPacket extends Exception {
    /**
     * @param errorMessage Error message for illegal packet usage
     */
    public IllegalPacket(String errorMessage) {
        super(errorMessage);
    }
}
