package com.produtor.agro.pricingchallenge.entity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Farm {
	
	@Id
	private Integer idFarm;
	
	private String farmName;
	
	private String ownerName;
	
	private String paymentAmount;
	
	@OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private Set<Coordinate> coordinates;

	public Farm() {	}

	public Farm(Integer idFarm, String farmName, String ownerName, String paymentAmount, Coordinate... coordinates) {
		super();
		this.idFarm = idFarm;
		this.farmName = farmName;
		this.ownerName = ownerName;
		this.paymentAmount = paymentAmount;
		this.coordinates = Stream.of(coordinates).collect(Collectors.toSet());
        this.coordinates.forEach(x -> x.setCoordinate(this));
	}

	public Farm(Integer idFarm, String farmName, Coordinate... coordinates) {
		super();
		this.idFarm = idFarm;
		this.farmName = farmName;
		this.coordinates = Stream.of(coordinates).collect(Collectors.toSet());
        this.coordinates.forEach(x -> x.setCoordinate(this));
	}

	public Farm(Integer idFarm, String farmName, String ownerName, String paymentAmount) {
		super();
		this.idFarm = idFarm;
		this.farmName = farmName;
		this.ownerName = ownerName;
		this.paymentAmount = paymentAmount;
	}

	public Farm(Integer idFarm, String farmName, String ownerName) {
		super();
		this.idFarm = idFarm;
		this.farmName = farmName;
		this.ownerName = ownerName;
	}

	public Integer getIdFarm() {
		return idFarm;
	}

	public void setIdFarm(Integer idFarm) {
		this.idFarm = idFarm;
	}

	public String getFarmName() {
		return farmName;
	}

	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
}
