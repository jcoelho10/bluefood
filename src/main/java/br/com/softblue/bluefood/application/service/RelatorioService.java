package br.com.softblue.bluefood.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.domain.pedido.RelatorioItemFaturamento;
import br.com.softblue.bluefood.domain.pedido.RelatorioItemFilter;
import br.com.softblue.bluefood.domain.pedido.RelatorioPedidoFilter;

@Service
public class RelatorioService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	public List<Pedido> listPedido(Integer restauranteId, RelatorioPedidoFilter filter) {
		
		Integer pedidoId = filter.getPedidoId();
		
		if (pedidoId != null) {
			Pedido pedido = pedidoRepository.findByIdAndRestaurante_Id(pedidoId, restauranteId);
			return List.of(pedido);
		}
		
		LocalDate dataInicial = filter.getDataInicial();
		LocalDate dataFinal = filter.getDataFinal();
		
		if (dataInicial == null) {
			return List.of();
		}
		
		if (dataFinal == null) {
			dataFinal = LocalDate.now();
		}
		
		return pedidoRepository.findByDateInterval(restauranteId, dataInicial.atStartOfDay(), dataFinal.atTime(23, 59, 59));
	}
	
	public List<RelatorioItemFaturamento> calcularFaturamentoItens(Integer restauranteId, RelatorioItemFilter filter) {
		
		List<Object[]> itensObj;
		List<RelatorioItemFaturamento> itens = new ArrayList<>();
		
		Integer itemId = filter.getItemId();
		LocalDate dataInicial = filter.getDataInicial();
		LocalDate dataFinal = filter.getDataFinal();
		
		if (dataInicial == null) {
			return List.of();
		}
		
		if (dataFinal == null) {
			dataFinal = LocalDate.now();
		}
		
		if (itemId != 0) {		
			itensObj = pedidoRepository.findItensForFaturamento(restauranteId, itemId, dataInicial.atStartOfDay(), dataFinal.atTime(23, 59, 59));
		
		} else {
			itensObj = pedidoRepository.findItensForFaturamento(restauranteId, dataInicial.atStartOfDay(), dataFinal.atTime(23, 59, 59));
		}
		
		for (Object[] item : itensObj) {
			String nome = (String) item[0];
			Long quantidade = (Long) item[1];
			BigDecimal valor = (BigDecimal) item[2];
			itens.add(new RelatorioItemFaturamento(nome, quantidade, valor));
		}
		
		return itens;
	}
}
