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
			 * Orienta��o MVC - Importante
			 * 
			 * Service - Dentro dos seus services, voc� pode chamar o REPOSITORY ou outros SERVICES
			 * 		- VOC� N�O DEVE CHAMAR DENTRO DO SERVICE, UM CONTROLLER, porque foje a l�gica(modelo) do MVC
			 * 
			 * 		- MVC - O controler que faz o "meio de campo"
			 * 			  - Voc� nuca faz a parte de NEG�CIO chamar o CONTROLLER 
			 * 			  - � o CONTROLLER que chama a parte de NEG�CIO
			 * 
			 * Controller - Dentro do controller, voc� pode chamar o REPOSITORY ou SERVICES
			 * 		- CONTROLLER � SEMPRE CHAMADO A PARTIR DE UMA REQUISI��O WEB
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
