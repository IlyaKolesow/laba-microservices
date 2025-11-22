package com.example.inventoryservice.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateQuantityDto {

    private int inventoryId;
    private int productId;
    private int delta;

}
