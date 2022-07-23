package inventory.service;

import inventory.model.Order;
import inventory.model.OrderStatus;
import inventory.model.Product;
import inventory.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Mono<Order> handleOrder(Order order) {
        log.info("Handle order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(l -> productRepository.findById(l.getProductId()))
                .flatMap(p -> {
                    int q = order.getLineItems()
                            .stream()
                            .filter(l -> l.getProductId()
                                    .equals(p.getId()))
                            .findAny()
                            .get()
                            .getQuantity();
                    if (p.getStock() >= q) {
                        p.setStock(p.getStock() - q);
                        return productRepository.save(p);
                    } else {
                        return Mono.error(new RuntimeException("Product is out of stock: " + p.getId()));
                    }
                })
                .then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
    }

    @Transactional
    public Mono<Order> revertOrder(Order order) {
        log.info("Revert order invoked with: {}", order);
        return Flux.fromIterable(order.getLineItems())
                .flatMap(l -> productRepository.findById(l.getProductId()))
                .flatMap(p -> {
                    int q = order.getLineItems()
                            .stream()
                            .filter(l -> l.getProductId()
                                    .equals(p.getId()))
                            .collect(Collectors.toList())
                            .get(0)
                            .getQuantity();

                    p.setStock(p.getStock() + q);
                    return productRepository.save(p);
                })
                .then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll().onErrorResume( e -> {
            log.error("Error while reading all records");
            return Mono.error(e);
        });

    }

    public void create(Product order){
        productRepository.save(order)
                .onErrorResume(e-> {
                    log.error("Error while saving");
                    throw new RuntimeException();})
                .subscribe( result -> log.info(String.valueOf(result)));
    }
}
