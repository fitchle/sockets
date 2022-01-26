package com.benchion.sockets.packet.exceptions;

public final class IllegalPacket extends Exception {
    public IllegalPacket(String errorMessage) {
        super(errorMessage);
    }
}
