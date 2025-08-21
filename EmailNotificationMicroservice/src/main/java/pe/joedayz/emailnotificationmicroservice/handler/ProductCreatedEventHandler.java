package pe.joedayz.emailnotificationmicroservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pe.joedayz.core.ProductCreatedEvent;
import pe.joedayz.emailnotificationmicroservice.error.NotRetryableException;
import pe.joedayz.emailnotificationmicroservice.error.RetryableException;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent){
        //NOTA: Con esto se va al DLQ en una.
        //if(true) throw new NotRetryableException("Not Retryable. No need to consume this message again");
        LOGGER.info("Received a new event: " +  productCreatedEvent.getTitle());

        String requestUrl = "http://localhost:8082/response/200";
        try {
            var response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if(response.getStatusCode().value() == HttpStatus.OK.value()){
                LOGGER.info("Received response from a remote service: " + response.getBody());
            }
        }catch (ResourceAccessException ex){
            //AQUI ENTRA CUANDO PARES EL SERVICIO mockingservice
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        }catch (HttpServerErrorException ex){
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }catch (Exception ex){
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }


    }
}
