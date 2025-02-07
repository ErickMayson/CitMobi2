package neo.com.br.CitMobi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8085");
        server.setDescription("Homolog");

        Contact myContact = new Contact();
        myContact.setName("Erick Ferreira");
        myContact.setEmail("erick.ferreira@CitMobi2.com");

        Info information = new Info()
                .title("Public Transport Management System API")
                .version("1.0")
                .description("This API exposes endpoints to manage and access public transports systems.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
