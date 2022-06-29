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
import br.livraria.dao.PessoaDAO;
import br.livraria.model.Item;
import br.livraria.model.Livro;
import br.livraria.model.Pedido;
import br.livraria.model.Pessoa;

public class VendaControlador {
	
	private static Pedido pedidoAberto;
	
	private VendaControlador() {}
	
	public static void criarPedido() {
		
		Pedido novoPedido = new Pedido(LoginControlador.getFuncionarioLogado(), new Date(), "VENDA");
		PedidoDAO.open(novoPedido);
		
		pedidoAberto = PedidoDAO.getLastPedido();
		
	}

	public static Pedido getPedidoAberto() {
		
		return pedidoAberto;
		
	}
	
	private static Object[][] get() {
		
		Object[][] tabela;
		
		Vector<Item> itens = ItemDAO.getItensByPedido(pedidoAberto.getIdPedido());
		
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
			VendaControlador.get(),
			new String[] {
				"ID", "Livro", "Preco", "Quantidade", "Subtotal"
			}
		);
		
	}
	
	public static boolean addItem(String idLivro, String quantidade) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		int id = 0;
		int qtd = 0;
		try {
			id = Integer.parseInt(idLivro);
			qtd = Integer.parseInt(quantidade);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Numeros invalidos");
		}
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(idLivro.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Livro nao pode ser nulo");
			return false;
		}
		
		if(quantidade.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade nao pode ser nulo");
			return false;
		}

		/*
		 * Identificar validacao dos campos unicos
		 */

		/*
		 * Identificar outras validacoes
		 */
		
		Livro livro = LivroDAO.getLivroById(id);
		if(livro == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Livro nao consta na base de dados");
			return false;
		}
		
		if(qtd <= 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade deve ser maior que 0");
			return false;
		}
		
		if(livro.getQtdEstoque() - qtd < 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Nao ha livros suficientes em estoque");
			return false;
		}
		
		/*
		 * Verifica se ha naquele pedido um livro ja lancado
		 */
		Item item = ItemDAO.getItemByIdLivro(id, pedidoAberto.getIdPedido());
		if(item != null) {
			item.addQtdVendida(qtd);
			ItemDAO.update(item);
		} else {
			item = new Item(livro, pedidoAberto, qtd);
			ItemDAO.save(item);
		}
		
		/*
		 * Atualizacao do estoque
		 */
		item.getLivro().removeEstoque(qtd);
		LivroDAO.update(item.getLivro());
		
		pedidoAberto = PedidoDAO.getPedidoById(pedidoAberto.getIdPedido());
		return true;
	}
	
	public static void cancelarVenda() {
		
		Vector<Item> itens = ItemDAO.getItensByPedido(pedidoAberto.getIdPedido());
		for(int i = 0; i < itens.size(); i++) {
			
			Livro livro = itens.get(i).getLivro();
			livro.addEstoque(itens.get(i).getQtdVendida());
			LivroDAO.update(livro);
			
			ItemDAO.deleteByID(itens.get(i).getIdItem());
		}
		
		PedidoDAO.deleteById(pedidoAberto.getIdPedido());
	}
	
	public static boolean finalizarVenda(String formaPagamento, String valorPago, String cpf) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		int valor = 0;
		try {
			valor = Integer.parseInt(valorPago);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um valor valido");
			return false;
		}
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(valorPago.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Valor Pago nao pode ser nulo");
			return false;
		}
		
		if(cpf.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */
		
		Pessoa pessoa = PessoaDAO.getPessoaByCpf(cpf);
		if(pessoa == null) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao consta na base de dados");
			return false;
		}
		
		/*
		 * Identificar outras validacoes
		 */
		
		if(valor < pedidoAberto.getPrecoTotal()) {
			JOptionPane.showMessageDialog(new JFrame(), "Dinheiro insuficiente");
			return false;
		}
		
		if(valor > pedidoAberto.getPrecoTotal()) {
			double troco = valor - pedidoAberto.getPrecoTotal();
			JOptionPane.showMessageDialog(new JFrame(), "Venda Finalizada com sucesso para " + pessoa.getNome() + "! Troco: " + troco);
		}
		
		/*
		 * Operacao
		 */
		
		pedidoAberto.setCliente(pessoa);
		pedidoAberto.setFormaPagamento(formaPagamento);
		
		PedidoDAO.close(pedidoAberto);
		return true;
		
	}
	
}
