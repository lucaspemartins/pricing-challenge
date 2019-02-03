package com.produtor.agro.pricingchallenge.service;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.produtor.agro.pricingchallenge.entity.Farm;
import com.produtor.agro.pricingchallenge.repository.FarmRepository;

@Service("FarmRepository")
public class FarmService {
	
	@Autowired
	private FarmRepository farmRepository;
	
	public Iterable<Farm> findAllFarms() throws SQLException {
		return farmRepository.findAll();
	}
	
	public Farm findFarmByFarmName(String farmName) throws SQLException {
		return farmRepository.findFarmByFarmName(farmName);
	}
	
	public Optional<Farm> findFarmByFarmId(Integer idFarm) throws SQLException {
		return farmRepository.findById(idFarm);
	}

}
