package com.produtor.agro.pricingchallenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.produtor.agro.pricingchallenge.entity.Coordinate;

@Repository
public interface CoordinateRepository extends CrudRepository<Coordinate, Integer> {
	
	@Query("select latitude, longitude from Coordinate where coordinateType = ?1")
    public List<Coordinate> findAllCoordinates(String type);
	
	@Query("select latitude, longitude, farm.idFarm from Coordinate where coordinateType = ?1")
    public List<Coordinate> findAllFarmCoordinates(String type);

}
