package com.produtor.agro.pricingchallenge.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.produtor.agro.pricingchallenge.entity.Service;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Integer> {
	
	@Query("select s from Service s where coordinate.idCoordinate = ?1")
    public Service findServiceByCoordinateId(Integer idCoordinate);
}
