package com.produtor.agro.pricingchallenge.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.produtor.agro.pricingchallenge.entity.Farm;
import com.produtor.agro.pricingchallenge.service.FarmService;

@RestController
@RequestMapping("/farms")
public class FarmController {
	
	@Autowired
	FarmService farmService;
		
    public ResponseEntity<List<Farm>> farms() throws SQLException {
		Iterable<Farm> farms = farmService.findAllFarms();
		List<Farm> farmsList = new ArrayList<>();
		farms.forEach(farmsList::add);
		
		if (farmsList.isEmpty()) {
			return new ResponseEntity<List<Farm>>(farmsList, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Farm>>(farmsList, HttpStatus.OK);
    }
	
	@RequestMapping("/farmByName")
    public ResponseEntity<Farm> farmByName(@RequestParam(value="name") String farmName) throws SQLException {
		Farm farm = farmService.findFarmByFarmName(farmName);
		if (farm == null) {
			return new ResponseEntity<Farm>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Farm>(farm, HttpStatus.OK);
    }
	
	@RequestMapping("/farmById")
    public ResponseEntity<Optional<Farm>> farmByName(@RequestParam(value="id") Integer idFarm) throws SQLException {
		Optional<Farm> farm = farmService.findFarmByFarmId(idFarm);

		if (farm.isPresent()) {
			return new ResponseEntity<Optional<Farm>>(farm, HttpStatus.OK);
		}
		return new ResponseEntity<Optional<Farm>>(HttpStatus.NOT_FOUND);
    }

}
