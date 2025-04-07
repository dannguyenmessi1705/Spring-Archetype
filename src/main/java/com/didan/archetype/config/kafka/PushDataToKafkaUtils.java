package com.didan.archetype.config.kafka;

import com.didan.archetype.utils.UtilsCommon;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@ConditionalOnProperty(
    value = {"spring.kafka.enabled", "spring.kafka.producer.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Slf4j
public class PushDataToKafkaUtils {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public PushDataToKafkaUtils(
      @Qualifier("kafkaEventTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void push(String message, String topic) {
    sendMessageAsync(message, topic);
  }

  public <T> void push(T message, String topic) {
    sendMessageAsync(UtilsCommon.GSON.toJson(message), topic);
  }

  public void sendMessageAsync(final Object message, final String topic) { // Gửi tin nhắn không đồng bộ
    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message); // Gửi tin nhắn đến topic
    future.whenComplete((result, ex) -> { // Khi hoàn thành, thực hiện các hành động sau
      if (ex == null) { // Nếu không có lỗi xảy ra
        log.info("===> Sent message=[{}] with offset=[{}] to topic: {} SUCCESS !!!",
            message,
            result.getRecordMetadata().offset(),
            topic);
      } else { // Nếu có lỗi xảy ra
        log.info("xxxx> Unable to send message=[{}] to topic: {} FAIL !!! \n Due to: {}",
            message,
            topic,
            ex.getMessage(),
            ex);
      }
    });
  }
}
