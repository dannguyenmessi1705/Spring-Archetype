package com.didan.archetype.filter;

import com.didan.archetype.utils.UtilsCommon;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SuppressWarnings("all")
public class RequestWrapper extends HttpServletRequestWrapper {

  private final String body;
  private final String isFile = "FILE";

  public RequestWrapper(HttpServletRequest request) throws IOException {
    super(request);

    if (UtilsCommon.isMultipart(request)) {
      body = isFile;
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      try (
          InputStream inputStream = request.getInputStream();
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
      ) {
        char[] charBuffer = new char[128];
        int bytesRead;
        while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      }
      body = stringBuilder.toString();
    }
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (body.equals(isFile)) {
      return super.getInputStream();
    }

    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {

      }

      @Override
      public int read() throws IOException {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() throws IOException {
    if (body.equals(isFile)) {
      return super.getReader();
    }
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }

  public String getBody() {
    return this.body;
  }
}
