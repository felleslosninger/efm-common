package no.difi.move.common.dokumentpakking.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Archive {

    private final byte[] bytes;

    public Archive(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
