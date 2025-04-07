package com.didan.archetype.filter.cachehttp;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("all")
@Slf4j
public class CachedBodyServletInputStream extends ServletInputStream {
    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    public boolean isFinished() {
        try {
            return this.cachedBodyInputStream.available() == 0;
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean isReady() {
        return true;
    }

    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    public int read() throws IOException {
        return this.cachedBodyInputStream.read();
    }
}

