package com.doubleslash.fifth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
	
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
	
	private String areas;
		
	private double starAvg;
	
	private int starCnt;
}
