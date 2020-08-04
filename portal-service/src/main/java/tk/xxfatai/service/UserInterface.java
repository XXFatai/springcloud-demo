package tk.xxfatai.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("USER-SERVICE")
public interface UserInterface {

    @RequestMapping("/getUser/{id}")
    String getUser(@PathVariable String id);
}
