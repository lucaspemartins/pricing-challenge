package com.produtor.agro.pricingchallenge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.h2.jdbc.JdbcSQLException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.produtor.agro.pricingchallenge.entity.Coordinate;
import com.produtor.agro.pricingchallenge.entity.Farm;
import com.produtor.agro.pricingchallenge.entity.Service;
import com.produtor.agro.pricingchallenge.repository.CoordinateRepository;
import com.produtor.agro.pricingchallenge.repository.FarmRepository;
import com.produtor.agro.pricingchallenge.repository.ServiceRepository;

@SpringBootApplication
@EntityScan(basePackages = { "com.produtor.agro.pricingchallenge.entity" })
@EnableJpaRepositories(basePackages = { "com.produtor.agro.pricingchallenge.repository" })
public class PricingChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingChallengeApplication.class, args);
	}

	private static final String FAZENDAS_CSV_PATH = "src/main/resources/static/csv/fazendas.csv";
	private static final String SERVICOS_CSV_PATH = "src/main/resources/static/csv/servicos.csv";

	@Bean
	public CommandLineRunner demo(FarmRepository farmRepository, ServiceRepository serviceRepository,
			CoordinateRepository coordinateRepository) {
		return (args) -> {
			try {

				// insertTestData(farmRepository, serviceRepository, coordinateRepository);
				handleCsvFiles(farmRepository, serviceRepository, coordinateRepository);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JdbcSQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	private void handleCsvFiles(FarmRepository farmRepository, ServiceRepository serviceRepository,
			CoordinateRepository coordinateRepository) throws IOException, FileNotFoundException, JdbcSQLException {

		handleFazendasFile(farmRepository, coordinateRepository, FAZENDAS_CSV_PATH);
		handleServicosFile(serviceRepository, coordinateRepository, SERVICOS_CSV_PATH);

	}

	private void handleServicosFile(ServiceRepository serviceRepository, CoordinateRepository coordinateRepository,
			String servicosCsvPath) throws IOException, FileNotFoundException, JdbcSQLException {

		List<String[]> serviceRows = readCsvFile(servicosCsvPath);

		Iterator<String[]> serviceRowsIterator = serviceRows.iterator();
		while (serviceRowsIterator.hasNext()) {
			String[] serviceRow = serviceRowsIterator.next();

			String coordinatesInfo = serviceRow[2];
			// Retrieve only coordinates
			int firstBracketIndex = coordinatesInfo.indexOf("[");
			int lastBracketIndex = coordinatesInfo.lastIndexOf("]");
			String coordinates = coordinatesInfo.substring(firstBracketIndex + 1, lastBracketIndex);

			String[] cleanCoordinatesArray = coordinates.split(",");
			Coordinate coordinate = new Coordinate(Double.parseDouble(cleanCoordinatesArray[0]),
					Double.parseDouble(cleanCoordinatesArray[1]), "point");
			coordinateRepository.save(coordinate);
			Integer serviceId = Integer.valueOf(Integer.parseInt(serviceRow[0]));
			serviceRepository.save(new Service(serviceId, serviceRow[1], coordinate));
		}

	}

	private void handleFazendasFile(FarmRepository farmRepository, CoordinateRepository coordinateRepository,
			String fazendasCsvPath) throws IOException, FileNotFoundException, JdbcSQLException {

		List<String[]> farmRows = readCsvFile(fazendasCsvPath);

		Iterator<String[]> farmRowsIterator = farmRows.iterator();
		while (farmRowsIterator.hasNext()) {
			String[] farmRow = farmRowsIterator.next();

			// Persists farm
			Integer idFarm = Integer.valueOf(Integer.parseInt(farmRow[0]));
			Farm farm = new Farm(idFarm, farmRow[1], farmRow[2]);
			farmRepository.save(farm);

			String coordinatesInfo = farmRow[3];
			// Retrieve only coordinates
			int firstBracketIndex = coordinatesInfo.indexOf("[");
			int lastBracketIndex = coordinatesInfo.lastIndexOf("]");
			String coordinates = coordinatesInfo.substring(firstBracketIndex, lastBracketIndex + 1);

			// Remove exceeding brackets
			lastBracketIndex = coordinates.lastIndexOf("]");
			coordinates = coordinates.substring(2, lastBracketIndex);

			while (coordinates.length() > 0) {
				firstBracketIndex = coordinates.indexOf("[");
				int firstClosedBracketIndex = coordinates.indexOf("]");
				if (firstBracketIndex > -1 && firstClosedBracketIndex > -1) {
					String cleanCoordinates = coordinates.substring(firstBracketIndex + 1, firstClosedBracketIndex);
					String[] cleanCoordinatesArray = cleanCoordinates.split(",");
					coordinateRepository.save(new Coordinate(Double.parseDouble(cleanCoordinatesArray[0]),
							Double.parseDouble(cleanCoordinatesArray[1]), "polygon", farm));
					coordinates = coordinates.substring(firstClosedBracketIndex + 1);
				} else {
					break;
				}

			}

		}

	}

	private List<String[]> readCsvFile(String path) throws IOException, FileNotFoundException {
		Reader reader = Files.newBufferedReader(Paths.get(path));
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(csvParser).build();
		return csvReader.readAll();
	}

	private void insertTestData(FarmRepository farmRepository, ServiceRepository serviceRepository,
			CoordinateRepository coordinateRepository) throws JdbcSQLException {
		/*
		 * Test data
		 */

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
		coordinateRepository.save(new Coordinate(-50.9216308, -17.0252726, "polygon", farm1));
		coordinateRepository.save(new Coordinate(-50.9216315, -17.0252730, "polygon", farm1));
		coordinateRepository.save(new Coordinate(-50.9216320, -17.0252735, "polygon", farm1));
		coordinateRepository.save(new Coordinate(-50.9216322, -17.0252740, "polygon", farm1));
		coordinateRepository.save(new Coordinate(-50.9216324, -17.0252745, "polygon", farm1));
		coordinateRepository.save(new Coordinate(51.509, -0.08, "polygon", farm2));
		coordinateRepository.save(new Coordinate(51.503, -0.06, "polygon", farm2));
		coordinateRepository.save(new Coordinate(51.51, -0.047, "polygon", farm2));
		serviceRepository.save(new Service(1, "Contagem de pragas", coordinate3));

	}

}
