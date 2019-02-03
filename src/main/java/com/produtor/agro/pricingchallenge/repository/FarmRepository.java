package com.produtor.agro.pricingchallenge.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.produtor.agro.pricingchallenge.entity.Farm;

@Repository
public interface FarmRepository extends CrudRepository<Farm, Integer>{
	
	@Query("select f from Farm f where f.farmName = ?1")
    public Farm findFarmByFarmName(String farmName);
	
}
