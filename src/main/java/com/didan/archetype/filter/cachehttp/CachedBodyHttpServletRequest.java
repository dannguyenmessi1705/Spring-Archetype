package com.didan.archetype.filter.cachehttp;

import com.didan.archetype.utils.UtilsCommon;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

@SuppressWarnings("all")
public class CachedBodyHttpServletRequest extends ContentCachingRequestWrapper {
    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(ContentCachingRequestWrapper request) throws IOException {
        super(request);
        if (UtilsCommon.isMultipart(request)) {
            this.cachedBody = null;
        } else {
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
        }
    }

    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    public void setCachedBody(byte[] cachedBody) {
        this.cachedBody = cachedBody;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headersName = this.getHeaderNames();

        while(headersName.hasMoreElements()) {
            String name = headersName.nextElement();
            map.put(name, this.getHeader(name));
        }

        return map;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.cachedBody == null) {
            return super.getInputStream();
        }
        return new CachedBodyServletInputStream(this.cachedBody);
    }
}