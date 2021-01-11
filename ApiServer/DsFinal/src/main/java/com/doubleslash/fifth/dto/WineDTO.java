package com.doubleslash.fifth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WineDTO {
	
	private int aid;
	
	private String name;
	
	private String category;
	
	private String image;
	
	private int lowestPrice;
	
	private int highestPrice;
	
	private int ml;
	
	private double abv;
	
	private String description;
	
	private String kind;
	
	private String country;
	
	private String area;
	
	private String town;
	
	private String wineKind;
	
	private int flavor;
	
	private int body;
	
	private double starAvg;
	
	private Long starCnt;
}
