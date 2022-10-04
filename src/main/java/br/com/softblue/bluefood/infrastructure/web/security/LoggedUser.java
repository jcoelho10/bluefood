package br.com.softblue.bluefood.infrastructure.web.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.usuario.Usuario;

/**
 * 
 * @author Nido
 * 
 * Classe que o Spring utiliza pra representar um 
 * usu�rio logado.
 *
 *
 *OBS: Quem vai chamar esses m�todos, � o SPRING e ele se vira.
 */

@SuppressWarnings("serial")
public class LoggedUser implements UserDetails {
	
	private Usuario usuario;
	private Role role;
	private Collection<? extends GrantedAuthority> roles;
	
	public LoggedUser(Usuario usuario) {
		this.usuario = usuario;
		
		Role role;
		
		if (usuario instanceof Cliente) {
			role = Role.CLIENTE;
		} else if (usuario instanceof Restaurante) {
			role = Role.RESTAURANTE;
		} else {
			throw new IllegalStateException("O tipo de usuário não é válido");
		}
		
		this.role = role;
		this.roles = List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}
	

	/**
	 * getAuthorities
	 * -Para pegar os Roles
	 * � o termo que o Spring usa para referenciar perfis de acesso
	 * 
	 * No nosso caso, o usu�rio s� ter� 1 perfil.
	 * 
	 * Mas, a API � gen�rica o suficiente para suportar sistemas
	 * que tenham (para 1 usu�rio) v�rios perfis de acessos.
	 * Por isso ele retorna uma Collection<GrantedAuthority>
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		
		return roles;
	}

	@Override
	public String getPassword() {		
		return usuario.getSenha();
	}

	@Override
	public String getUsername() {
		return usuario.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {		
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Role getRole() {
		return role;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	
}
