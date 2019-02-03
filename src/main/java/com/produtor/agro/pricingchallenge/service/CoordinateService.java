package com.produtor.agro.pricingchallenge.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.produtor.agro.pricingchallenge.entity.Coordinate;
import com.produtor.agro.pricingchallenge.repository.CoordinateRepository;

@Service("CoordinateRepository")
public class CoordinateService {
	
	@Autowired
	private CoordinateRepository coordinateRepository;
	
	private static final String POLYGON = "polygon";
	
	public List<Coordinate> findAllCoordinates() throws SQLException {
		return coordinateRepository.findAllCoordinates(POLYGON);
	}
	
	public List<Coordinate> findAllFarmCoordinates() throws SQLException {
		return coordinateRepository.findAllFarmCoordinates(POLYGON);
	}
}
