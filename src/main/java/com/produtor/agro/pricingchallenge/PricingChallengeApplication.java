package com.produtor.agro.pricingchallenge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Area;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.h2.jdbc.JdbcSQLException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

@SpringBootApplication
@EntityScan(basePackages = { "com.produtor.agro.pricingchallenge.entity" })
@EnableJpaRepositories(basePackages = { "com.produtor.agro.pricingchallenge.repository" })
public class PricingChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingChallengeApplication.class, args);
	}

	private static final String FAZENDAS_CSV_PATH = "src/main/resources/static/csv/fazendas.csv";
	private static final String SERVICOS_CSV_PATH = "src/main/resources/static/csv/servicos.csv";
	private static final String POLYGON = "polygon";
	private static final String POINT = "point";
	private static final String PRAGUE_SERVICE = "Contagem de pragas";
	private static final String SOIL_QUALITY_ANALYSIS_SERVICE = "Analise de qualidade de solo";

	@Bean
	public CommandLineRunner demo(FarmRepository farmRepository, ServiceRepository serviceRepository,
			CoordinateRepository coordinateRepository) {
		return (args) -> {
			try {

				handleCsvFiles(farmRepository, serviceRepository, coordinateRepository);
				calculatePaymentAmount(farmRepository, serviceRepository, coordinateRepository);

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

	private void calculatePaymentAmount(FarmRepository farmRepository, ServiceRepository serviceRepository,
			CoordinateRepository coordinateRepository)
			throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
		List<Coordinate> coordinatesList = coordinateRepository.findAllCoordinates(POLYGON);

		Iterable<Farm> farms = farmRepository.findAll();
		List<Farm> farmsList = new ArrayList<>();
		farms.forEach(farmsList::add);

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		for (Farm farm : farmsList) {
			ArrayList<com.vividsolutions.jts.geom.Coordinate> coords = new ArrayList<com.vividsolutions.jts.geom.Coordinate>();

			for (Coordinate coordinates : coordinatesList) {
				if (coordinates.getFarm().getIdFarm().equals(farm.getIdFarm())) {
					coords.add(new com.vividsolutions.jts.geom.Coordinate(coordinates.getLatitude(),
							coordinates.getLongitude()));
				}
			}

			LinearRing ring = geometryFactory
					.createLinearRing(coords.toArray(new com.vividsolutions.jts.geom.Coordinate[coords.size()]));
			LinearRing holes[] = null;
			Polygon polygon = geometryFactory.createPolygon(ring, holes);
			
			javax.measure.Measure<Double, Area> polygonArea = calculateArea(polygon);
			javax.measure.Measure<Double, Area> hectare = polygonArea.to(NonSI.HECTARE);
			Double pricePerHectare = calculatePricePerHectare(hectare);
			
			List<Coordinate> pointCoordinatesList = coordinateRepository.findAllCoordinates(POINT);
			
			int praguesServiceCounter = 0;
			int soilQualityAnalysisServiceCounter = 0;
			for (Coordinate pointCoordinate : pointCoordinatesList) {
				com.vividsolutions.jts.geom.Coordinate coordinate = new com.vividsolutions.jts.geom.Coordinate(pointCoordinate.getLatitude(), pointCoordinate.getLongitude());
				Geometry point = geometryFactory.createPoint(coordinate);
				if (polygon.intersects(point)) {
					Service service = serviceRepository.findServiceByCoordinateId(pointCoordinate.getIdCoordinate());
					if (service.getServiceName().equals(PRAGUE_SERVICE)) {
						praguesServiceCounter++;
					}
					else if (service.getServiceName().equals(SOIL_QUALITY_ANALYSIS_SERVICE)) {
						soilQualityAnalysisServiceCounter++;
					}
					coordinateRepository.save(new Coordinate(pointCoordinate.getLatitude(), pointCoordinate.getLongitude(), pointCoordinate.getcoordinateType(), farm));
				}	
			}
			
			double praguesServicePayment = (praguesServiceCounter > 10) ? 249.90 * praguesServiceCounter: 279.90 * praguesServiceCounter;
			double soilQualityAnalysisServicePayment = (soilQualityAnalysisServiceCounter > 10) ? 379.90 * soilQualityAnalysisServiceCounter: 399.90 * soilQualityAnalysisServiceCounter;
			
			Double payment = pricePerHectare + praguesServicePayment + soilQualityAnalysisServicePayment;
			farm.setPaymentAmount(payment);
			farmRepository.save(farm);
		}

	}

	private Double calculatePricePerHectare(javax.measure.Measure<Double, Area> hectare) {
		Double hectareValue = hectare.getValue();
		if (hectareValue < 50) {
			return Double.valueOf(1.25) * hectareValue;
		} 
		else if (hectareValue > 1000) {
			return Double.valueOf(1.02) * hectareValue;
		}
		return Double.valueOf(1.15) * hectareValue;
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

	private javax.measure.Measure<Double, Area> calculateArea(Polygon polygon) {
		Point centroid = polygon.getCentroid();
		try {
			String autoCode = "AUTO:42001," + centroid.getX() + "," + centroid.getY();
			CoordinateReferenceSystem auto = CRS.decode(autoCode);
			MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, auto);
			Polygon projed = (Polygon) JTS.transform(polygon, transform);
			
			return javax.measure.Measure.valueOf(projed.getArea(), SI.SQUARE_METRE);
		} catch (MismatchedDimensionException | TransformException | FactoryException e) {
			e.printStackTrace();
		}
		return javax.measure.Measure.valueOf(0.0, SI.SQUARE_METRE);
	}

}
