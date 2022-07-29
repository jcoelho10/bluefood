package br.com.softblue.bluefood.domain.restaurante;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Integer> {

	/**
	 * JPQL da JPA
	 * Trabalha apenas com elementos de objetos - CLASSES e ATRIBUTOS
	 * 
	 * A JPQL n�o trabalha com tabelas, colunas. Esque�am os nomes das tabelas e colunas
	 * do Banco de Dados
	 * 
	 * @param restauranteId
	 * @return lista de categorias do card�pio
	 *  
	 */
	
	//DISTINCT = Para n�o pegar duplicidade
	//      ?1 = Pegar o primeiro parametro passado no m�todo e coloca a�
	@Query("SELECT DISTINCT ic.categoria FROM ItemCardapio ic WHERE ic.restaurante.id = ?1 ORDER BY ic.categoria")
	public List<String> findCategorias(Integer restauranteId);	
	
	public List<ItemCardapio> findByRestaurante_IdOrderByNome(Integer restauranteId);
	
	public List<ItemCardapio> findByRestaurante_IdAndDestaqueOrderByNome(Integer restauranteId, boolean destaque);
	
	public List<ItemCardapio> findByRestaurante_IdAndDestaqueAndCategoriaOrderByNome(Integer restauranteId, boolean destaque, String categoria);
}
