package com.produtor.agro.pricingchallenge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.produtor.agro.pricingchallenge.entity.Coordinate;
import com.produtor.agro.pricingchallenge.entity.Farm;
import com.produtor.agro.pricingchallenge.entity.Service;
import com.produtor.agro.pricingchallenge.repository.CoordinateRepository;
import com.produtor.agro.pricingchallenge.repository.FarmRepository;
import com.produtor.agro.pricingchallenge.repository.ServiceRepository;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.produtor.agro.pricingchallenge.entity"
        })
@EnableJpaRepositories(basePackages = {
        "com.produtor.agro.pricingchallenge.repository"
        })
public class PricingChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingChallengeApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner demo(FarmRepository farmRepository, ServiceRepository serviceRepository, CoordinateRepository coordinateRepository) {
		return (args) -> {
			try {

				Farm farm1 = new Farm(1, "fazendinha", "julio", "200");
				farmRepository.save(farm1);
				Farm farm2 = new Farm(2, "recanto dos passaros", "afonso", "400");
				farmRepository.save(farm2);
				Coordinate coordinate = new Coordinate(-50.91296195983887, -17.11946438990011, "polygon", farm1);
				Coordinate coordinate2 = new Coordinate(-50.898799896240234, -17.105683210161953, "polygon", farm1);
				Coordinate coordinate3 = new Coordinate(-50.89879989624456, -17.105683210167345, "point");
				coordinateRepository.save(coordinate);
				coordinateRepository.save(coordinate2);
				coordinateRepository.save(coordinate3);
				coordinateRepository.save(new Coordinate(-50.9216308,-17.0252726, "polygon", farm1));
				coordinateRepository.save(new Coordinate(-50.9216315,-17.0252730, "polygon", farm1));
				coordinateRepository.save(new Coordinate(-50.9216320,-17.0252735, "polygon", farm1));
				coordinateRepository.save(new Coordinate(-50.9216322,-17.0252740, "polygon", farm1));
				coordinateRepository.save(new Coordinate(-50.9216324,-17.0252745, "polygon", farm1));
				coordinateRepository.save(new Coordinate(51.509, -0.08, "polygon", farm2));
				coordinateRepository.save(new Coordinate(51.503, -0.06, "polygon", farm2));
				coordinateRepository.save(new Coordinate(51.51, -0.047, "polygon", farm2));
				serviceRepository.save(new Service(1, "Contagem de pragas", coordinate3));

				// Read fazendas.csv and persists on database 
				Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/static/csv/fazendas.csv"));

				CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

				List<String[]> farmRows = csvReader.readAll();
				
				Iterator<String[]> farmRowsIterator = farmRows.iterator();
				while (farmRowsIterator.hasNext()) {  
				   String[] farmRow = farmRowsIterator.next();
//				   farmRepository.save(new Farm(Integer.valueOf(Integer.parseInt(farmRow[0])), farmRow[1], farmRow[2], farmRow[3], farmRow[4]));

				}
//				
//				// Read servicos.csv and persists on database 
//				reader = Files.newBufferedReader(Paths.get("src/main/resources/static/csv/servicos.csv"));
//
//				csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
//
//				List<String[]> serviceRows = csvReader.readAll();
//				
//				Iterator<String[]> serviceRowsIterator = serviceRows.iterator();
//				while (serviceRowsIterator.hasNext()) {  
//				   String[] serviceRow = serviceRowsIterator.next();
//				   serviceRepository.save(new Service(Integer.valueOf(Integer.parseInt(serviceRow[0])), serviceRow[1], serviceRow[2]));
//				}

//			} catch (FileNotFoundException e) {
//		        e.printStackTrace();
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		    }
			}catch (Exception e) {
				
			}
		};
	}

}

