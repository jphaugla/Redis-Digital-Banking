package com.jphaugla.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Email {
        private @Indexed String email_address;
        private String email_label;
}
