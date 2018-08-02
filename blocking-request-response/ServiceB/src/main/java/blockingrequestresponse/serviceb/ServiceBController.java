package blockingrequestresponse.serviceb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Controller
@SpringBootApplication
public class ServiceBController {

    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity publishEvent(@RequestBody Map<String, Object> payload){
        return new ResponseEntity(new HttpHeaders(), HttpStatus.ACCEPTED);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplicationBuilder(ServiceBController.class)
                .build();

        application.run(args);
    }
}