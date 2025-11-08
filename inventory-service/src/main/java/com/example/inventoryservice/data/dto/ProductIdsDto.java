package com.example.inventoryservice.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductIdsDto {

    private List<Integer> productIds = new ArrayList<>();

}
