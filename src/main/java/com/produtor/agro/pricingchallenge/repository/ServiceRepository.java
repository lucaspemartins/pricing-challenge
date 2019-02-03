package com.produtor.agro.pricingchallenge.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.produtor.agro.pricingchallenge.entity.Service;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Integer> {

}
