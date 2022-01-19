package br.com.softblue.bluefood.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;

/**
 * 
 * @author Nido
 *
 *Essa classe é uma espécie de Serviço de aplicação
 *É o que chamamos no DDD de ApplicationService
 *
 *É uma classe que criamos quando precisamos agrupar (informações/operações) que não fazem sentindo 
 *está dentro da classe do domínio.
 *
 *Por exemplo:
 *Na hora de salvar um cliente, temos uma serie de coisas pra fazer...ainda temos que validar se já não "existe" outro e-mail, temos que 
 *fazer a criptrografia da senha do cliente. 
 *
 *Tudo isso são operações de negócio que tem que ser feito durante o processo de salvamento.
 *
 *E não é interessante deixarmos o Controller fazer isso.
 *
 *É muito importante separar as responsabilidades das coisas.
 *
 *Exemplo:
 *
 *A view/html ele só tem papel de mostrar as coisas.
 *	-Ele não tem que buscar inf no bd, processar dados, ele tem que receber tudo pronto pra mostrar.
 *
 * Controller
 * O papel do controller é receber o que a view/html/pagina mandar pra alguém fazer e depois pegar
 * o resultado e direcionar para outra view.
 * -Ele não tem papel de acessar o BD, de fazer gravação, processamento de informação...nada disso.
 * 
 * Quando você quer fazer essas tarefas, agrupar um conjunto de tarefas que tem que ser realizados
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
			throw new ValidationException("O e-mail está duplicado");
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
}
