package tk.xxfatai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController("/")
public class UserController {

    @Autowired
    DiscoveryClient discoveryClient;

    @RequestMapping("/getUser/{id}")
    public String hello(@PathVariable String id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("USER-SERVICE");
        ServiceInstance selectedInstance = instances.get(new Random().nextInt(instances.size()));
        return "hello "+id+selectedInstance.getServiceId() + ":" + selectedInstance
                .getHost() + ":" + selectedInstance.getPort();
    }
}
