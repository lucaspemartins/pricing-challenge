package com.produtor.agro.pricingchallenge.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Service {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idService;
	
	private String serviceName;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
	private Coordinate coordinate;
	
	public Service() {	}

	public Service(Integer idService, String serviceName) {
		super();
		this.idService = idService;
		this.serviceName = serviceName;
	}
	
	public Service(Integer idService, String serviceName, Coordinate coordinate) {
		super();
		this.idService = idService;
		this.serviceName = serviceName;
		this.coordinate = coordinate;
		this.coordinate.setService(this);
	}

	public Integer getIdService() {
		return idService;
	}

	public void setIdService(Integer idService) {
		this.idService = idService;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
