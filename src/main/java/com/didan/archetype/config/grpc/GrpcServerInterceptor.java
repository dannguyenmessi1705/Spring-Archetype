package com.didan.archetype.config.grpc;

import com.didan.archetype.config.properties.GRpcServerPropertiesCustom;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
/*
Không thêm @GrpcGlobalServerInterceptor và @Configuration vì chỉ cần sử dụng trong class kế thừa
VD:
@GrpcGlobalServerInterceptor
@Configuration
public class InterceptorServer extends GrpcServerInterceptor {
    public InterceptorServer() {
        super(new GRpcServerPropertiesCustom());
    }
}
=> Vậy có thể sử dụng InterceptorClient trong các class khác mà không cần phải thêm @GrpcGlobalClientInterceptor và @Configuration
 */
public class GrpcServerInterceptor implements ServerInterceptor {

  private static final Metadata.Key<String> REQUEST_ID_KEY;

  GRpcServerPropertiesCustom gRpcServerPropertiesCustom;

  static {
    REQUEST_ID_KEY = Key.of("X-Request-Id", Metadata.ASCII_STRING_MARSHALLER); // Khai báo key cho request id
  }

  public GrpcServerInterceptor(GRpcServerPropertiesCustom gRpcServerPropertiesCustom) {
    this.gRpcServerPropertiesCustom = gRpcServerPropertiesCustom;
  }

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    final String requestId = metadata.get(REQUEST_ID_KEY);
    ThreadContext.put("X-Request-Id", requestId); // Đưa request id vào ThreadContext để có thể sử dụng trong các log khác
    GrpcServerCall<ReqT, RespT> grpcServerCall = new GrpcServerCall<>(serverCall, this.gRpcServerPropertiesCustom); // Tạo một server call mới
    ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(grpcServerCall, metadata); // Bắt đầu server call
    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) { // Ghi đè phương thức onMessage để log lại request
      public void onMessage(ReqT message) { // Ghi đè phương thức onMessage để log lại request
        if (GrpcServerInterceptor.this.gRpcServerPropertiesCustom.isServerLog()) { // Kiểm tra xem có cần log không
          GrpcServerInterceptor.log.info("[{}] Request (Request id {}) : body request: \n{}", serverCall.getMethodDescriptor().getFullMethodName(), requestId, message);
        }

        super.onMessage(message); // Gọi phương thức onMessage của listener cha
      }
    };
  }

  @Slf4j
  private static class GrpcServerCall<ReqT, RespT> extends ServerCall<ReqT, RespT> {

    GRpcServerPropertiesCustom gRpcServerPropertiesCustom;
    ServerCall<ReqT, RespT> serverCall;

    protected GrpcServerCall(ServerCall<ReqT, RespT> serverCall, GRpcServerPropertiesCustom gRpcServerPropertiesCustom) {
      this.gRpcServerPropertiesCustom = gRpcServerPropertiesCustom;
      this.serverCall = serverCall;
    }

    @Override
    public void request(int numMessages) { // Ghi đè phương thức request để log lại số lượng message
      this.serverCall.request(numMessages);
    }

    @Override
    public void sendHeaders(Metadata headers) { // Ghi đè phương thức sendHeaders để log lại headers
      this.serverCall.sendHeaders(headers);

    }

    @Override
    public void sendMessage(RespT message) { // Ghi đè phương thức sendMessage để log lại message
      if (this.gRpcServerPropertiesCustom.isServerLog()) {
        log.info("[{}] Response form server to client:\n{}", this.serverCall.getMethodDescriptor().getFullMethodName(), message);
      }

      this.serverCall.sendMessage(message);
    }

    @Override
    public void close(Status status, Metadata metadata) { // Ghi đè phương thức close để log lại status
      this.serverCall.close(status, metadata);
    }

    @Override
    public boolean isCancelled() {  // Ghi đè phương thức isCancelled để kiểm tra xem cuộc gọi có bị hủy không
      return this.serverCall.isCancelled();
    }

    @Override
    public MethodDescriptor<ReqT, RespT> getMethodDescriptor() { // Ghi đè phương thức getMethodDescriptor để lấy thông tin về method descriptor
      return this.serverCall.getMethodDescriptor();
    }
  }
}
