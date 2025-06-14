package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.dto.del.DeliveredItemDTO;

public class DeliveredItemMapper {

    public static DeliveredItem fromDTO(DeliveredItemDTO dto) {
        if (dto == null) return null;
        return new DeliveredItem(
                dto.getId(),
                dto.getDestinationDocId(),
                dto.getProductId(),
                dto.getQuantity()
        );
    }

    public static DeliveredItemDTO toDTO(DeliveredItem item) {
        if (item == null) return null;
        return new DeliveredItemDTO(
                item.getItemId(),
                item.getDestinationDocId(),
                item.getProductId(),
                item.getQuantity()
        );
    }
}
