package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewType {

	private final String identifier;

	public static final ViewType CHEST = new ViewType("minecraft:chest");

}