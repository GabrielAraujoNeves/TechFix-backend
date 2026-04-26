package AuthAssistencia.AuthAssitencia.Controller;

import AuthAssistencia.AuthAssitencia.Service.ProductService;
import AuthAssistencia.AuthAssitencia.dto.ProductRequest;
import AuthAssistencia.AuthAssitencia.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ========== ENDPOINTS ADMIN ==========

    // Criar produto
    @PostMapping("/admin/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Atualizar produto
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.updateProduct(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Deletar produto
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Produto deletado com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Adicionar estoque
    @PutMapping("/admin/add-stock/{id}")
    public ResponseEntity<?> addStock(@PathVariable Long id, @RequestParam Integer quantidade) {
        try {
            ProductResponse response = productService.addStock(id, quantidade);
            return ResponseEntity.ok(Map.of(
                    "message", "Estoque adicionado com sucesso!",
                    "product", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Remover estoque
    @PutMapping("/admin/remove-stock/{id}")
    public ResponseEntity<?> removeStock(@PathVariable Long id, @RequestParam Integer quantidade) {
        try {
            ProductResponse response = productService.removeStock(id, quantidade);
            return ResponseEntity.ok(Map.of(
                    "message", "Estoque removido com sucesso!",
                    "product", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ENDPOINTS USER E ADMIN ==========

    // Listar todos produtos
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> getProductsByMarca(@PathVariable String marca) {
        try {
            List<ProductResponse> products = productService.getProductsByMarca(marca);
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar por modelo
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<?> getProductsByModelo(@PathVariable String modelo) {
        try {
            List<ProductResponse> products = productService.getProductsByModelo(modelo);
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Buscar por nome
    @GetMapping("/search")
    public ResponseEntity<?> searchProductsByNome(@RequestParam String nome) {
        try {
            List<ProductResponse> products = productService.searchProductsByNome(nome);
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Listar produtos com estoque baixo
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts() {
        try {
            List<ProductResponse> products = productService.getLowStockProducts();
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "warning", "Produtos com estoque abaixo do mínimo",
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Listar produtos sem estoque
    @GetMapping("/out-of-stock")
    public ResponseEntity<?> getOutOfStockProducts() {
        try {
            List<ProductResponse> products = productService.getOutOfStockProducts();
            return ResponseEntity.ok(Map.of(
                    "total", products.size(),
                    "warning", "Produtos sem estoque!",
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // Último produto cadastrado
    @GetMapping("/last-created")
    public ResponseEntity<?> getLastCreatedProduct() {
        try {
            ProductResponse product = productService.getLastCreatedProduct();
            return ResponseEntity.ok(Map.of(
                    "message", "Último produto cadastrado",
                    "product", product,
                    "cadastradoEm", product.getCreatedAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Último produto atualizado
    @GetMapping("/last-updated")
    public ResponseEntity<?> getLastUpdatedProduct() {
        try {
            ProductResponse product = productService.getLastUpdatedProduct();
            return ResponseEntity.ok(Map.of(
                    "message", "Último produto atualizado",
                    "product", product,
                    "atualizadoEm", product.getUpdatedAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}