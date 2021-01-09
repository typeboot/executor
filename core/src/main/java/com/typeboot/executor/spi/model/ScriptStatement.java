package com.typeboot.executor.spi.model;

import java.io.Serializable;

public final class ScriptStatement implements Serializable {
    private final int serialNumber;
    private final String name;
    private final String content;

    public ScriptStatement(int serialNumber, String name, String content) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.content = content;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
