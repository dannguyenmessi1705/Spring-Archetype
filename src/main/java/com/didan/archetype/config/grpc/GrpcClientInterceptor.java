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

/*
Không thêm @GrpcGlobalClientInterceptor và @Configuration vì chỉ cần sử dụng trong class kế thừa
VD:
@GrpcGlobalClientInterceptor
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
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions.withDeadlineAfter(this.gRpcServerPropertiesCustom.getClientRequestTimeoutMs(),
        TimeUnit.MILLISECONDS))) { // Tạo một client call mới với thời gian timeout được cấu hình trong gRpcServerPropertiesCustom
      public void sendMessage(ReqT message) {
        if (GrpcClientInterceptor.this.gRpcServerPropertiesCustom.isClientRequestLog()) { // Kiểm tra xem có cần log không
          GrpcClientInterceptor.log.info("Server request [:{}:{}] with method [{}] and body:\n{}",
              GrpcClientInterceptor.this.host, GrpcClientInterceptor.this.port, methodDescriptor.getFullMethodName(), message);
        }

        super.sendMessage(message);
      }

      public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) { // Ghi đè phương thức start để thêm thông tin vào headers
        GrpcClientListener<RespT> grpcClientListener = new GrpcClientListener<>(GrpcClientInterceptor.this.host, GrpcClientInterceptor.this.port, methodDescriptor.getFullMethodName(),
            System.currentTimeMillis(), GrpcClientInterceptor.this.gRpcServerPropertiesCustom, responseListener); // Tạo một listener mới
        super.start(grpcClientListener, headers);
      }
    };
  }

  @Slf4j
  public static class GrpcClientListener<RespT> extends ClientCall.Listener<RespT> {

    String methodName;
    ClientCall.Listener<RespT> responseListener;
    private final String host;
    private final int port;
    private final Long timeStartRequestMs;
    private final GRpcServerPropertiesCustom gRpcServerPropertiesCustom;

    protected GrpcClientListener(String host, int port, String methodName, Long timeStartRequestMs, GRpcServerPropertiesCustom gRpcServerPropertiesCustom,
        ClientCall.Listener<RespT> responseListener) {
      this.host = host;
      this.port = port;
      this.methodName = methodName;
      this.timeStartRequestMs = timeStartRequestMs;
      this.responseListener = responseListener;
      this.gRpcServerPropertiesCustom = gRpcServerPropertiesCustom;
    }

    public void onMessage(RespT message) { // Ghi đè phương thức onMessage để log thông tin khi nhận được phản hồi từ server
      if (this.gRpcServerPropertiesCustom.isClientRequestLog()) {
        log.info("Server Response [:{}:{}] with method [{}] with response ms: {} Response:\n{}",
            this.host, this.port, this.methodName, System.currentTimeMillis() - this.timeStartRequestMs, message);
      }

      this.responseListener.onMessage(message);
    }

    public void onHeaders(Metadata headers) { // Ghi đè phương thức onHeaders để log thông tin khi nhận được headers từ server
      this.responseListener.onHeaders(headers);
    }

    public void onClose(Status status, Metadata trailers) { // Ghi đè phương thức onClose để log thông tin khi kết thúc cuộc gọi
      this.responseListener.onClose(status, trailers);
    }

    public void onReady() { // Ghi đè phương thức onReady để log thông tin khi cuộc gọi đã sẵn sàng
      this.responseListener.onReady();
    }
  }
}
