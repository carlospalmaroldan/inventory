package inventory.controller;

import inventory.model.Order;
import inventory.model.Product;
import inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class InventoryController {

    @Autowired
    ProductService productService;

    @GetMapping(value= "getAll")
    public Flux<Product> getAllProducts() {
        return productService.getProducts();
    }

    @PostMapping
    public Mono<Order> processOrder(@RequestBody Order order) {
        return productService.handleOrder(order);
    }

    @DeleteMapping
    public Mono<Order> revertOrder(@RequestBody Order order) {
        return productService.revertOrder(order);
    }

    @RequestMapping(value = { "/create"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Product e) {
        productService.create(e);
    }
}
