package tk.xxfatai.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tk.xxfatai.service.UserInterface;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController("/")
public class LoginController {

    @Autowired
    private RestTemplate lbcRestTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserInterface userInterface;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @RequestMapping("/login/v1/{id}")
    public String getUserListLbcRest(@PathVariable String id) {
        String user = lbcRestTemplate.getForObject("http://USER-SERVICE/getUser/"+id, String.class);
        return user;
    }

    @RequestMapping("/login/v2/{id}")
    public String getUserListFeign(@PathVariable String id) {
        String user = userInterface.getUser(id);
        return user;
    }

    @RequestMapping("/login/v3/{id}")
    public String getUserListLoadBalance(@PathVariable String id) {
        String user = "";
        try {
            user = loadBalancerClient.execute("USER-SERVICE", new LoadBalancerRequest<String>() {
                @Override
                public String apply(ServiceInstance instance) throws Exception {
                    System.out.println(instance.getUri());
                    URI uri = URI.create(String.format("%s/getUser/%s", instance.getUri(), id));
                    String forObject = restTemplate.getForObject(uri, String.class);
                    return forObject;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    private String getDefaultFallBack(String id){
        return "annotation fall back" + id;
    }

    @RequestMapping("/login/v4/{id}")
    @HystrixCommand(fallbackMethod = "getDefaultFallBack")
    public String getUserListHystrix(@PathVariable String id) {
        String user = lbcRestTemplate.getForObject("http://USER-SERVICE/getUser/"+id, String.class);
        return user;
    }

    @RequestMapping("/login/v5/{id}")
    @HystrixCommand(fallbackMethod = "getDefaultFallBack")
    public String getUserListZuul(@PathVariable String id) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String user = lbcRestTemplate.getForObject("http://USER-SERVICE/getUser/"+id, String.class);
        return user;
    }
}
