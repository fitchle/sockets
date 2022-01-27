package com.benchion.sockets.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Packet ID is stored here
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketID {
    /**
     * @return Packet ID
     */
    int value();
}
