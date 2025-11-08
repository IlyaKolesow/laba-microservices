package com.example.inventoryservice.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityDto {

    private int productId;
    private int delta;

}
