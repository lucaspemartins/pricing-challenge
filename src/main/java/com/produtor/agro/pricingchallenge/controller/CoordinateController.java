package com.produtor.agro.pricingchallenge.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.produtor.agro.pricingchallenge.entity.Coordinate;
import com.produtor.agro.pricingchallenge.service.CoordinateService;

@RestController
public class CoordinateController {
	
	@Autowired
	CoordinateService coordinateService;
    
	@RequestMapping("/coordinates")
    public ResponseEntity<List<Coordinate>> farmCoordinates() throws SQLException {
		List<Coordinate> coordinatesList = coordinateService.findAllFarmCoordinates();

		if (coordinatesList.isEmpty()) {
			return new ResponseEntity<List<Coordinate>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Coordinate>>(coordinatesList, HttpStatus.OK);
    }
}
