package br.com.softblue.bluefood.infrastructure.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.softblue.bluefood.application.service.ClienteService;
import br.com.softblue.bluefood.application.service.RestauranteService;
import br.com.softblue.bluefood.application.service.ValidationException;
import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestaurante;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.SearchFilter;
import br.com.softblue.bluefood.util.SecurityUtils;

@Controller
@RequestMapping(path = "/cliente")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;
	
	@Autowired	
	private ClienteService clienteService;
	
	@Autowired
	private RestauranteService restauranteService;
	
	@GetMapping(path = "/home")
	public String home(Model model) {		
		List<CategoriaRestaurante> categorias = categoriaRestauranteRepository.findAll(Sort.by("nome"));
		model.addAttribute("categorias", categorias);
		model.addAttribute("searchFilter", new SearchFilter());
		
		return "cliente-home";
	}
	
	@GetMapping(path = "/edit")
	public String edit(Model model) {
		Integer clienteId = SecurityUtils.loggedCliente().getId();
		Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();		
		model.addAttribute("cliente", cliente);
		ControllerHelper.setEditMode(model, true);
		
		return "cliente-cadastro";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute("cliente") @Valid Cliente cliente,
		Errors errors,
		Model model) {
	
	if (!errors.hasErrors()) {
		try {
			clienteService.saveCliente(cliente);
			model.addAttribute("msg", "Cliente gravado com sucesso!");
		
		} catch (ValidationException e) {
			errors.rejectValue("email", null, e.getMessage());
		}
	}
	
		ControllerHelper.setEditMode(model, true);
		return "cliente-cadastro";
	}
	
	@GetMapping(path = "/search")
	public String search(
			@ModelAttribute("searchFilter") SearchFilter filter,
			@RequestParam(value = "cmd", required = false) String command,
			Model model) {
		
		filter.processFilter(command);
		
		List<Restaurante> restaurantes = restauranteService.search(filter);
		model.addAttribute("restaurantes", restaurantes);
		
		model.addAttribute("searchType", filter);
		
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		
		
		return "cliente-busca";
	}
}
