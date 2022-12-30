package me.devnatan.inventoryframework.examples.shop;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
class Shop {

	UUID owner;

}

public interface ShopManager {

	List<Shop> getAllShops();

	Shop getShop(UUID owner);

}
