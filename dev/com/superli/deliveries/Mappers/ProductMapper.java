package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.del.ProductDTO;

public class ProductMapper {

    public static Product fromDTO(ProductDTO dto) {
        if (dto == null) return null;
        return new Product(
                dto.getId(),
                dto.getName(),
                dto.getWeight()
        );
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO(
                product.getProductId(),
                product.getName(),
                product.getWeight()
        );
    }
}