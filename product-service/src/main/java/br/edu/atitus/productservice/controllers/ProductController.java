package br.edu.atitus.productservice.controllers;

import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductRepository productRepository;
    private final Environment environment;
    public ProductController(ProductRepository productRepository, Environment environment) {
        this.productRepository = productRepository;
        this.environment = environment;
    }


    @GetMapping("/{idproduct}")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable Long idproduct,
            @RequestParam String targetCurrency
            ) {

        ProductEntity product = productRepository.findById(idproduct).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        String port = environment.getProperty("local.server.port");

        String environmentMsg = "Product-service running on port " + port;

        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getDescription(),
                product.getBrand(),
                product.getModel(),
                product.getPrice(),
                product.getCurrency(),
                product.getStock(),
                environmentMsg,
                null,
                targetCurrency
        );

        return ResponseEntity.ok(dto);
    }
}
