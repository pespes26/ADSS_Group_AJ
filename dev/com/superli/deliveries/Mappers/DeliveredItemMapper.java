package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.DeliveredItemDTO;

public class DeliveredItemMapper {

    public static DeliveredItem fromDTO(DeliveredItemDTO dto) {
        if (dto == null) return null;
        return new DeliveredItem(
                String.valueOf(dto.getProductId()), // productId is String in Entity
                dto.getQuantity()
        );
    }

    public static DeliveredItemDTO toDTO(DeliveredItem item, int id, int destinationDocId) {
        if (item == null) return null;
        return new DeliveredItemDTO(
                id,
                destinationDocId,
                Integer.parseInt(item.getProductId()), // converting back to int
                item.getQuantity()
        );
    }
}
