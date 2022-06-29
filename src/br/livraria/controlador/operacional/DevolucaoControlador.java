package br.livraria.controlador.operacional;

import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.livraria.controlador.menu.LoginControlador;
import br.livraria.dao.ItemDAO;
import br.livraria.dao.LivroDAO;
import br.livraria.dao.PedidoDAO;
import br.livraria.model.Item;
import br.livraria.model.Livro;
import br.livraria.model.Pedido;

public class DevolucaoControlador {
	
	private static Pedido pedidoDevolucao;
	private static Pedido pedidoVenda;

	private DevolucaoControlador() {}
	
	public static boolean abrirDevolucao() {
		
		String idPedido = JOptionPane.showInputDialog(new JFrame(), "Informe o ID do Pedido");
		
		/*
		 * Verificar se é um inteiro
		 */
		
		int id = 0;
		try {
			id = Integer.parseInt(idPedido);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
		/*
		 * Verificar se é vazio
		 */
		
		if(idPedido.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Pedido nao pode ser nulo");
			return false;
		}
		
		/*
		 * Verificar se existe
		 */
		
		Pedido pedidoRecuperado = PedidoDAO.getPedidoById(id);
		if(pedidoRecuperado == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Pedido nao consta na base de dados");
			return false;
		}
		
		if(pedidoRecuperado.getOperacao().equals("DEVOLUCAO")) {
			JOptionPane.showMessageDialog(new JFrame(), "Pedido Venda nao consta na base de dados");
			return false;
		}
		
		Pedido pedido = new Pedido(LoginControlador.getFuncionarioLogado(), new Date(), "DEVOLUCAO");
		PedidoDAO.open(pedido);
		
		pedidoDevolucao = PedidoDAO.getLastPedido();
		pedidoVenda = pedidoRecuperado;
		
		return true;
	}

	public static Pedido getPedidoDevolucao() {
		return pedidoDevolucao;
	}
	
	public static Pedido getPedidoVenda() {
		return pedidoVenda;
	}

	public static boolean removerItem(String idLivro, String qtdDevolvida) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		int id = 0;
		int qtd = 0;
		try {
			id = Integer.parseInt(idLivro);
			qtd = Integer.parseInt(qtdDevolvida);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Numeros invalidos");
			return false;
		}
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(idLivro.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Livro nao pode ser nulo");
			return false;
		}
		
		if(qtdDevolvida.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade nao pode ser nulo");
			return false;
		}

		/*
		 * Identificar validacao dos campos unicos
		 */

		/*
		 * Identificar outras validacoes
		 */
		
		if(qtd <= 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade precisa ser maior que 0");
			return false;
		}
		
		Livro livro = LivroDAO.getLivroById(id);
		if(livro == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Livro nao consta na base de dados");
			return false;
		}
		
		/*
		 * Criar um Item
		 * Atualizar o Item antigo
		 */
		
		Item item = new Item(livro, pedidoDevolucao, qtd);
		ItemDAO.save(item);
		
		item = ItemDAO.getItemByIdLivro(id, pedidoVenda.getIdPedido());
		
		if(item == null) {
			JOptionPane.showMessageDialog(new JFrame(), "O ID informado nao consta no Pedido");
			return false;
		}
		
		if(qtd > item.getQtdVendida()) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade a ser devolvida é maior que a quantidade vendida");
			return false;
		}
		
		item.removeQtdVendida(qtd);
		item.setPrecoTotal();
		ItemDAO.update(item);
		
		livro.addEstoque(qtd);
		LivroDAO.update(livro);
		
		pedidoVenda = PedidoDAO.getPedidoById(pedidoVenda.getIdPedido());
		pedidoDevolucao = PedidoDAO.getPedidoById(pedidoDevolucao.getIdPedido());
		
		return true;
		
	}
	
	private static Object[][] get() {
		
		Object[][] tabela;
		
		Vector<Item> itens = ItemDAO.getItensByPedido(pedidoVenda.getIdPedido());
		
		tabela = new Object[itens.size()][5];
		
		for(int i = 0; i < itens.size(); i++) {
			
			tabela[i][0] = itens.get(i).getLivro().getIdLivro();
			tabela[i][1] = itens.get(i).getLivro().getTitulo();
			tabela[i][2] = itens.get(i).getLivro().getPrecoUnit();
			tabela[i][3] = itens.get(i).getQtdVendida();
			tabela[i][4] = itens.get(i).getPrecoTotal();
			
		}
		
		return tabela;
		
	}
	
	public static DefaultTableModel updateTable() {
		
		return new DefaultTableModel(
			DevolucaoControlador.get(),
			new String[] {
				"ID", "Livro", "Preco", "Quantidade", "Subtotal"
			}
		);
		
	}
	
	public static void cancelarDevolucao() {
	
		Vector<Item> itensDevolucao = ItemDAO.getItensByPedido(pedidoDevolucao.getIdPedido());
		for(int i = 0; i < itensDevolucao.size(); i++) {
			Item item = ItemDAO.getItemByIdLivro(itensDevolucao.get(i).getLivro().getIdLivro(), pedidoVenda.getIdPedido());
			item.addQtdVendida(itensDevolucao.get(i).getQtdVendida());
			ItemDAO.update(item);
			
			ItemDAO.deleteByID(itensDevolucao.get(i).getIdItem());
		}
		
		PedidoDAO.deleteById(pedidoDevolucao.getIdPedido());
		
	}
	
	public static void finalizarDevolucao() {
		
		pedidoDevolucao.setCliente(pedidoVenda.getCliente());
		pedidoDevolucao.setFormaPagamento("DINHEIRO");
		
		PedidoDAO.close(pedidoDevolucao);
	}
	
}
