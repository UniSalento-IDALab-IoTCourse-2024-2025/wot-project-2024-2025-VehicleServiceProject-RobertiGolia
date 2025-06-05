package it.unisalento.iot2425.vehicleserviceproject.restcontrollers;


import it.unisalento.iot2425.vehicleserviceproject.domain.Vehicle;
import it.unisalento.iot2425.vehicleserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.vehicleserviceproject.dto.VehicleDTO;
import it.unisalento.iot2425.vehicleserviceproject.repository.VehicleRepository;
import it.unisalento.iot2425.vehicleserviceproject.security.JwtUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController

@RequestMapping("/api/vehicle")
public class VehicleRestControllers {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @RequestMapping(value = "/registration",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO save (@RequestBody VehicleDTO vehicleDTO,
                           @RequestHeader("Authorization") String token) {
// Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }

        String username = jwtUtilities.extractUsername(jwtToken);

        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        System.out.println("Id dell'utente: " + userId);
        ResultDTO resultDTO = new ResultDTO();

        //ricerca del veicolo nel "globo"
        Optional<Vehicle> existingVehicleGlobal = vehicleRepository.findByTarga(vehicleDTO.getPlate());

        if (!existingVehicleGlobal.isEmpty()) {
            resultDTO.setResult(ResultDTO.TARGA_IN_USO);
            resultDTO.setMessage("Veicolo già registrato");
            return resultDTO;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setTarga(vehicleDTO.getPlate());
        vehicle.setModello(vehicleDTO.getModel());
        vehicle.setColore(vehicleDTO.getColor());
        vehicle.setUtente(userId);

        vehicle = vehicleRepository.save(vehicle);
        vehicleDTO.setPlate(vehicle.getTarga());
        vehicleDTO.setUser(vehicle.getUtente());
        vehicleDTO.setModel(vehicle.getModello());
        vehicleDTO.setColor(vehicle.getColore());

        resultDTO.setVehicle(vehicleDTO);
        resultDTO.setResult(ResultDTO.OK);
        resultDTO.setMessage("Veicolo registrato");

        return resultDTO;
    }

}
