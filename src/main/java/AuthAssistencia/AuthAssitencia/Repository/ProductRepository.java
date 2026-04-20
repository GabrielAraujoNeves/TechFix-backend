package AuthAssistencia.AuthAssitencia.Repository;

import AuthAssistencia.AuthAssitencia.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscas existentes
    List<Product> findByMarca(String marca);
    List<Product> findByModelo(String modelo);
    List<Product> findByNomeContainingIgnoreCase(String nome);

    // NOVO: Verificar se produto já existe (mesmo nome, modelo e marca)
    boolean existsByNomeAndModeloAndMarca(String nome, String modelo, String marca);

    // NOVO: Buscar produto por nome, modelo e marca (para verificar duplicata)
    Optional<Product> findByNomeAndModeloAndMarca(String nome, String modelo, String marca);

    // NOVO: Buscar produtos com estoque baixo
    @Query("SELECT p FROM Product p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    List<Product> findProductsWithLowStock();

    // NOVO: Buscar produtos com estoque zerado
    @Query("SELECT p FROM Product p WHERE p.quantidadeEstoque = 0")
    List<Product> findProductsOutOfStock();

    // NOVO: Buscar produtos por faixa de preço
    List<Product> findByPrecoAtacadoBetween(Double min, Double max);

    // NOVO: Buscar produtos com estoque acima de um valor
    List<Product> findByQuantidadeEstoqueGreaterThan(Integer quantidade);
}