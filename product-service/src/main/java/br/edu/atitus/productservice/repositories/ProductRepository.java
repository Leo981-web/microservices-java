package br.edu.atitus.productservice.repositories;

import br.edu.atitus.productservice.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
