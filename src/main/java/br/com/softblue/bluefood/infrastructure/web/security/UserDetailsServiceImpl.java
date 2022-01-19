package br.com.softblue.bluefood.infrastructure.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.domain.usuario.Usuario;

/**
 * 
 * @author Nido
 * 
 * Classe para carregar (UserDetailsService) o usu�rio e o
 * Spring verificar se esse usu�rio pode ser autenticado ou n�o.
 *
 * O Spring identifica o @Service e o UserDetailsService, com isso,
 * ele sabe que � essa Classe que ele usa para carregar o usu�rio que
 * ele tem que autenticar.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = clienteRepository.findByEmail(username);
		
		if (usuario == null) {
			usuario = restauranteRepository.findByEmail(username);
			
			if (usuario == null) {
				throw new UsernameNotFoundException(username);
			}
		}
		
		return new LoggedUser(usuario);
	}

}
