package br.com.softblue.bluefood.application.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteComparator;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.SearchFilter;
import br.com.softblue.bluefood.domain.restaurante.SearchFilter.SearchType;
import br.com.softblue.bluefood.util.SecurityUtils;

/**
 * 
 * @author Nido
 *
 *Essa classe � uma esp�cie de Servi�o de aplica��o
 *� o que chamamos no DDD de ApplicationService
 *
 *� uma classe que criamos quando precisamos agrupar (informa��es/opera��es) que n�o fazem sentindo 
 *est� dentro da classe do dom�nio.
 *
 *Por exemplo:
 *Na hora de salvar um cliente, temos uma serie de coisas pra fazer...ainda temos que validar se j� n�o "existe" outro e-mail, temos que 
 *fazer a criptrografia da senha do cliente. 
 *
 *Tudo isso s�o opera��es de neg�cio que tem que ser feito durante o processo de salvamento.
 *
 *E n�o � interessante deixarmos o Controller fazer isso.
 *
 *� muito importante separar as responsabilidades das coisas.
 *
 *Exemplo:
 *
 *A view/html ele s� tem papel de mostrar as coisas.
 *	-Ele n�o tem que buscar inf no bd, processar dados, ele tem que receber tudo pronto pra mostrar.
 *
 * Controller
 * O papel do controller � receber o que a view/html/pagina mandar pra algu�m fazer e depois pegar
 * o resultado e direcionar para outra view.
 * -Ele n�o tem papel de acessar o BD, de fazer grava��o, processamento de informa��o...nada disso.
 * 
 * Quando voc� quer fazer essas tarefas, agrupar um conjunto de tarefas que tem que ser realizados
 * criamos um applicationservice pra fazer isso.
 */

@Service
public class RestauranteService {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ImageService imageService;

	@Transactional
	public void saveRestaurante(Restaurante restaurante) throws ValidationException {
		if(!validateEmail(restaurante.getEmail(), restaurante.getId())) {
			throw new ValidationException("O e-mail est� duplicado");
		}				
		
		if (restaurante.getId() != null) {
			Restaurante restauranteDB = restauranteRepository.findById(restaurante.getId()).orElseThrow();
			restaurante.setSenha(restauranteDB.getSenha());
		} else {
			restaurante.encryptPassword();
			restaurante = restauranteRepository.save(restaurante);
			restaurante.setLogotipoFileName();
			
			/**
			 * Orientação MVC - Importante
			 * 
			 * Service - Dentro dos seus services, você pode chamar o REPOSITORY ou outros SERVICES
			 * 		- VOCÊ NÃO DEVE CHAMAR DENTRO DO SERVICE, UM CONTROLLER, porque foje a lógica(modelo) do MVC
			 * 
			 * 		- MVC - O controler que faz o "meio de campo"
			 * 			  - Você nuca faz a parte de NEGÓCIO chamar o CONTROLLER 
			 * 			  - É o CONTROLLER que chama a parte de NEGÓCIO
			 * 
			 * Controller - Dentro do controller, você pode chamar o REPOSITORY ou SERVICES
			 * 		- CONTROLLER É SEMPRE CHAMADO A PARTIR DE UMA REQUISIÇÃO WEB
			 */
			imageService.uploadLogotipo(restaurante.getLogotipoFile(), restaurante.getLogotipo());
		}
	}
	
	private boolean validateEmail(String email, Integer id) {
		Cliente cliente = clienteRepository.findByEmail(email);
		
		if (cliente != null) {
			return false;
		}
		
		Restaurante restaurante = restauranteRepository.findByEmail(email);
		
		if (restaurante != null) {
			if (id == null) {
				return false;
			}
			
			if (!restaurante.getId().equals(id)) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<Restaurante> search(SearchFilter filter) {
		List<Restaurante> restaurantes;
		
		if (filter.getSearchType() == SearchType.Texto) {
			restaurantes = restauranteRepository.findByNomeIgnoreCaseContaining(filter.getTexto());
		
		} else if (filter.getSearchType() == SearchType.Categoria) {
			restaurantes = restauranteRepository.findByCategorias_Id(filter.getCategoriaId());
			
		} else {
			throw new IllegalArgumentException("O tipo de busca " + filter.getSearchType() + " n�o � suportado");
		}
		
		Iterator<Restaurante> it = restaurantes.iterator();
		
		while(it.hasNext()) {
			Restaurante restaurante = it.next();
			double taxaEntrega = restaurante.getTaxaEntrega().doubleValue();
			
			if (filter.isEntregaGratis() && taxaEntrega > 0
					|| !filter.isEntregaGratis() && taxaEntrega == 0) {
				it.remove();
			}
		}
		
		RestauranteComparator comparator = new RestauranteComparator(filter, SecurityUtils.loggedCliente().getCep());
		restaurantes.sort(comparator);
		
		return restaurantes;
	}
}
