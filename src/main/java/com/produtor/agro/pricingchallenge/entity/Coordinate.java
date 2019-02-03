package com.produtor.agro.pricingchallenge.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Coordinate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idCoordinate;
	
	private Double latitude; 
	
	private Double longitude;
	
	private String coordinateType;
	
	@ManyToOne
    @JoinColumn
    private Farm farm;
	
	@OneToOne(mappedBy = "coordinate")
    private Service service;

	public Coordinate() {	}
	
	public Coordinate(Double latitude, Double longitude, String coordinateType, Farm farm) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.coordinateType = coordinateType;
		this.farm = farm;
	}

	public Coordinate(Double latitude, Double longitude, String coordinateType) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.coordinateType = coordinateType;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getcoordinateType() {
		return coordinateType;
	}

	public void setcoordinateType(String coordinateType) {
		this.coordinateType = coordinateType;
	}

	public Farm getFarm() {
		return farm;
	}

	public void setCoordinate(Farm farm) {
		this.farm = farm;
	}

	public void setService(Service service) {
		this.service = service;
	}
	
}
