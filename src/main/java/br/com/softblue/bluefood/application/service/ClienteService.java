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
 *é o que chamamos no DDD de ApplicationService
 *
 *é uma classe que criamos quando precisamos agrupar (informações/operações) que não fazem sentindo 
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
 *� muito importante separar as responsabilidades das coisas.
 *
 *Exemplo:
 *
 *A view/html ele só tem papel de mostrar as coisas.
 *	-Ele não tem que buscar inf no bd, processar dados, ele tem que receber tudo pronto pra mostrar.
 *
 * Controller
 * O papel do controller � receber o que a view/html/pagina mandar pra algu�m fazer e depois pegar
 * o resultado e direcionar para outra view.
 * -Ele não tem papel de acessar o BD, de fazer gravação, processamento de informação...nada disso.
 * 
 * Quando você quer fazer essas tarefas, agrupar um conjunto de tarefas que tem que ser realizados
 * criamos um applicationservice pra fazer isso.
 */

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;

	@Transactional
	public void saveCliente(Cliente cliente) throws ValidationException {
		if(!validateEmail(cliente.getEmail(), cliente.getId())) {
			throw new ValidationException("O e-mail está duplicado");
		}
		
		if (cliente.getId() != null ) {
			Cliente clienteDB = clienteRepository.findById(cliente.getId()).orElseThrow();
			cliente.setSenha(clienteDB.getSenha());
		} else {
			cliente.encryptPassword();
		}
		
		clienteRepository.save(cliente);
	}
	
	private boolean validateEmail(String email, Integer id) {
		Restaurante restaurante = restauranteRepository.findByEmail(email);
		
		if (restaurante != null) {
			return false;
		}
		
		Cliente cliente = clienteRepository.findByEmail(email);
		
		if (cliente != null) {
			if (id == null) {
				return false;
			}
			
			if (!cliente.getId().equals(id)) {
				return false;
			}
		}
		
		return true;
	}
}
