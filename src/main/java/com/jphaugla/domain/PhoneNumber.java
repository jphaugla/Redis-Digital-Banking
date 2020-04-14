package com.jphaugla.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PhoneNumber {

        private @Indexed String phone_number;
        private String phone_label;
}
