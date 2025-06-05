package it.unisalento.iot2425.vehicleserviceproject.repository;

import it.unisalento.iot2425.vehicleserviceproject.domain.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    Optional<Vehicle> findByTarga(String type);
    Optional<Vehicle> findByTargaAndUtente(String targa, String utente);
    List<Vehicle> findByUtente(String idUtente);
}
