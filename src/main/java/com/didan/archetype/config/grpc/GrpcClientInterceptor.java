package com.didan.archetype.config.grpc;

import com.didan.archetype.config.properties.GRpcServerPropertiesCustom;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
Không thêm @GRpcGlobalInterceptor và @Configuration vì chỉ cần sử dụng trong class kế thừa
VD:
@GRpcGlobalInterceptor
@Configuration
public class InterceptorClient extends GrpcClientInterceptor {
    public InterceptorClient() {
        super("localhost", 9090, new GRpcServerPropertiesCustom());
    }
}
=> Vậy có thể sử dụng InterceptorClient trong các class khác mà không cần phải thêm @GrpcGlobalClientInterceptor và @Configuration
 */

@Slf4j
public class GrpcClientInterceptor implements ClientInterceptor {

  private final GRpcServerPropertiesCustom gRpcServerPropertiesCustom;
  private final String host;
  private final int port;

  public GrpcClientInterceptor(final String host, final int port, final GRpcServerPropertiesCustom gRpcServerPropertiesCustom) {
    this.host = host;
    this.port = port;
    this.gRpcServerPropertiesCustom = gRpcServerPropertiesCustom;
  }


  @Override
  public <Q, S> ClientCall<Q, S> interceptCall(MethodDescriptor<Q, S> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions.withDeadlineAfter(this.gRpcServerPropertiesCustom.getClientRequestTimeoutMs(),
        TimeUnit.MILLISECONDS))) { // Tạo một client call mới với thời gian timeout được cấu hình trong gRpcServerPropertiesCustom
      @Override
      public void sendMessage(Q message) {
        if (GrpcClientInterceptor.this.gRpcServerPropertiesCustom.isClientRequestLog()) { // Kiểm tra xem có cần log không
          GrpcClientInterceptor.log.info("Server request [:{}:{}] with method [{}] and body:\n{}",
              GrpcClientInterceptor.this.host, GrpcClientInterceptor.this.port, methodDescriptor.getFullMethodName(), message);
        }

        super.sendMessage(message);
      }

      @Override
      public void start(ClientCall.Listener<S> responseListener, Metadata headers) { // Ghi đè phương thức start để thêm thông tin vào headers
        GrpcClientListener<S> grpcClientListener = new GrpcClientListener<>(GrpcClientInterceptor.this.host, GrpcClientInterceptor.this.port, methodDescriptor.getFullMethodName(),
            System.currentTimeMillis(), GrpcClientInterceptor.this.gRpcServerPropertiesCustom, responseListener); // Tạo một listener mới
        super.start(grpcClientListener, headers);
      }
    };
  }

  @Slf4j
  public static class GrpcClientListener<S> extends ClientCall.Listener<S> {

    String methodName;
    ClientCall.Listener<S> responseListener;
    private final String host;
    private final int port;
    private final Long timeStartRequestMs;
    private final GRpcServerPropertiesCustom gRpcServerPropertiesCustom;

    protected GrpcClientListener(String host, int port, String methodName, Long timeStartRequestMs, GRpcServerPropertiesCustom gRpcServerPropertiesCustom,
        ClientCall.Listener<S> responseListener) {
      this.host = host;
      this.port = port;
      this.methodName = methodName;
      this.timeStartRequestMs = timeStartRequestMs;
      this.responseListener = responseListener;
      this.gRpcServerPropertiesCustom = gRpcServerPropertiesCustom;
    }

    @Override
    public void onMessage(S message) { // Ghi đè phương thức onMessage để log thông tin khi nhận được phản hồi từ server
      if (this.gRpcServerPropertiesCustom.isClientRequestLog()) {
        log.info("Server Response [:{}:{}] with method [{}] with response ms: {} Response:\n{}",
            this.host, this.port, this.methodName, System.currentTimeMillis() - this.timeStartRequestMs, message);
      }

      this.responseListener.onMessage(message);
    }

    @Override
    public void onHeaders(Metadata headers) { // Ghi đè phương thức onHeaders để log thông tin khi nhận được headers từ server
      this.responseListener.onHeaders(headers);
    }

    @Override
    public void onClose(Status status, Metadata trailers) { // Ghi đè phương thức onClose để log thông tin khi kết thúc cuộc gọi
      this.responseListener.onClose(status, trailers);
    }

    @Override
    public void onReady() { // Ghi đè phương thức onReady để log thông tin khi cuộc gọi đã sẵn sàng
      this.responseListener.onReady();
    }
  }
}
