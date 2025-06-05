package it.unisalento.iot2425.vehicleserviceproject.restcontrollers;

import it.unisalento.iot2425.vehicleserviceproject.domain.Vehicle;
import it.unisalento.iot2425.vehicleserviceproject.dto.ResultDTO;
import it.unisalento.iot2425.vehicleserviceproject.dto.VehicleDTO;
import it.unisalento.iot2425.vehicleserviceproject.dto.VehicleListDTO;
import it.unisalento.iot2425.vehicleserviceproject.exceptions.VehicleNotFoundException;
import it.unisalento.iot2425.vehicleserviceproject.repository.VehicleRepository;
import it.unisalento.iot2425.vehicleserviceproject.security.JwtUtilities;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@RestController

@RequestMapping("/api/vehicle")
public class VehicleRestController {


    //TO DO: fare il metodo per eliminare e modificare il veicolo
    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; //scambia i messaggi

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public VehicleListDTO getAll() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleDTO> list = new ArrayList<VehicleDTO>();

        for (Vehicle vehicle : vehicles) {
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setPlate(vehicle.getTarga());
            vehicleDTO.setModel(vehicle.getModello());
            vehicleDTO.setColor(vehicle.getColore());
            vehicleDTO.setUser(vehicle.getUtente());
            vehicleDTO.setCategory(vehicle.getCategoria());
            list.add(vehicleDTO);
        }

        VehicleListDTO listDTO = new VehicleListDTO();
        listDTO.setVehicles(list);
        return listDTO;
    }

    @RequestMapping(value="/{idUser}", //in base all'id dell'utente
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public VehicleListDTO findByUser(@PathVariable String idUser) throws VehicleNotFoundException {

        System.out.println("id utente: " + idUser);

        List<Vehicle> vehicles = vehicleRepository.findByUtente(idUser);
        List<VehicleDTO> list = new ArrayList<>();

        for (Vehicle veicolo : vehicles){
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setPlate(veicolo.getTarga());
            vehicleDTO.setModel(veicolo.getModello());
            vehicleDTO.setColor(veicolo.getColore());
            vehicleDTO.setUser(veicolo.getUtente());
            vehicleDTO.setCategory(veicolo.getCategoria());
            list.add(vehicleDTO);
        }

        VehicleListDTO listDTO = new VehicleListDTO();
        listDTO.setVehicles(list);
        return listDTO;
    }

    @RequestMapping(value = "/remove/{targa}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO removeVehicle(@PathVariable String targa, @RequestHeader("Authorization") String token) throws VehicleNotFoundException {
        ResultDTO resultDTO = new ResultDTO();
        // Rimuovi "Bearer " dalla stringa dell'header per ottenere solo il token JWT.
        String jwtToken = token.substring(7);
        // Verifica e decodifica il token JWT utilizzando il segreto condiviso.
        Date expirationDate = jwtUtilities.extractExpiration(jwtToken);

        if (expirationDate.before(new Date())) {
            // Token scaduto
            return null;
        }

        //prende l'id dell'utente per collegarlo ad un mezzo
        String userId = jwtUtilities.extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        Optional<Vehicle> vehicle = vehicleRepository.findByTargaAndUtente(targa, userId);
        if (!vehicle.isEmpty()) {
            vehicleRepository.delete(vehicle.get());
            resultDTO.setVehicle(null);
            resultDTO.setMessage("Veicolo eliminato");
            resultDTO.setResult(ResultDTO.ELIMINATO);
        } else {
            resultDTO.setVehicle(null);
            resultDTO.setMessage("Veicolo non trovato");
            resultDTO.setResult(ResultDTO.NON_TROVATO);
        }
        return resultDTO;
    }


}
