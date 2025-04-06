package com.didan.archetype.annotation;

import com.didan.archetype.ApplicationArchetype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE}) // Đánh dấu @EnableArchetype lên lớp
@Retention(RetentionPolicy.RUNTIME) // Đánh dấu @EnableArchetype có thể được sử dụng tại runtime
@Import({ApplicationArchetype.class}) // Nhập lớp ApplicationArchetype
public @interface EnableArchetype {
}

// Sử dụng @EnableArchetype trong lớp chính của ứng dụng để kích hoạt cấu hình từ ApplicationArchetype