package cl.duoc.medimapa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class BioequivalenciaService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Esta es la llamada real al otro microservicio (Puerto 8084)
    public List<Object> obtenerBioequivalentes(String principioActivo) {
        try {
            return webClientBuilder.build()
                    .get()
                    // Asegúrate de que esta URL coincida con el endpoint real que programaste en tu ms-bioequivalencia
                    .uri("http://localhost:8084/api/bioequivalencia/buscar?principioActivo=" + principioActivo)
                    .retrieve()
                    .bodyToFlux(Object.class) // Convierte la respuesta JSON en una lista de objetos de Java
                    .collectList()
                    .block(); // .block() hace que espere la respuesta antes de seguir (síncrono)
        } catch (Exception e) {
            // Si el microservicio 8084 está apagado o falla, devolvemos null en vez de romper todo
            System.out.println("Error al contactar Bioequivalencia: " + e.getMessage());
            return null;
        }
    }
}