package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class ProductMapper {

    public static Product fromDTO(ProductDTO dto) {
        if (dto == null) return null;
        return new Product(
                String.valueOf(dto.getId()), // productId is String in Entity
                dto.getName(),
                0f // default weight (not in DTO) – ניתן לעדכן לפי צורך
        );
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO(
                Integer.parseInt(product.getProductId()),
                product.getName()
        );
    }
}