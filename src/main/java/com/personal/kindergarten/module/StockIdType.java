package com.personal.kindergarten.module;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class StockIdType {
    private String stockId;
    private String name;
}
