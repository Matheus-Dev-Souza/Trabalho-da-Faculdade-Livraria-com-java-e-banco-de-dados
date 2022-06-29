package br.livraria.controlador.cadastros;

import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.livraria.dao.EditoraDAO;
import br.livraria.dao.LivroDAO;
import br.livraria.model.Editora;
import br.livraria.model.Livro;
import br.livraria.util.Convert;

public class LivroControlador {

	private LivroControlador() {}
	
	private static Object[][] get(String busca) {
		
		Object[][] tabela;
		
		Vector<Livro> livros = LivroDAO.getLivros();
		
		if (busca != "") {
			
			Vector<Livro> livrosSelecionados = new Vector<Livro>();
			
			for(int i = 0; i < livros.size(); i++) {
				
				if(livros.get(i).getTitulo().toLowerCase().contains(busca.toLowerCase()))
					livrosSelecionados.add(livros.get(i));
				
			}
			
			livros = livrosSelecionados;
			
		}
		
		tabela = new Object[livros.size()][8];
		
		for(int i = 0; i < livros.size(); i++) {
			
			tabela[i][0] = livros.get(i).getIdLivro();
			tabela[i][1] = livros.get(i).getPrecoUnit();
			tabela[i][2] = livros.get(i).getTitulo();
			tabela[i][3] = livros.get(i).getGenero();
			tabela[i][4] = livros.get(i).getAutor();
			tabela[i][5] = livros.get(i).getDataPublicada();
			tabela[i][6] = livros.get(i).getQtdEstoque();
			tabela[i][7] = livros.get(i).getEditora().getNome();
			
		}
		
		return tabela;
		
	}
	
	public static DefaultTableModel updateTable(String busca) {
		
		return new DefaultTableModel(
			LivroControlador.get(busca),
			new String[] {
				"ID", "Preco", "Titulo", "Genero", "Autor", "Publicacao", "Estoque", "Editora"
			}
		);
		
	}
	
	public static boolean delete(String idLivro) {
		
		int id;
		try {
			id = Integer.parseInt(idLivro);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
		if(LivroDAO.getLivroById(id) != null) {
			LivroDAO.deleteByID(id);
			return true;
		}
		
		JOptionPane.showMessageDialog(new JFrame(), "Livro nao consta na base de dados");
		return false;
		
	}
	
	public static Livro search(String idLivro) {
		
		int id;
		try {
			id = Integer.parseInt(idLivro);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return null;
		}
		
		Livro livro = LivroDAO.getLivroById(id);
		if(livro == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Livro nao consta na base de dados");
			return null;
		}
		
		return livro;
	}
	
	public static boolean save(
			String campoTitulo, String campoGenero, String campoData, 
			String campoAutor, String campoQtdEstoque, String campoPreco, 
			String campoIdEditora) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		Date dataPublicada = Convert.parseDate(campoData);
		
		int qtdEstoque = 0;
		double preco = 0;
		int idEditora = 0;
		try {
			qtdEstoque = Integer.parseInt(campoQtdEstoque);
			preco = Double.parseDouble(campoPreco);
			idEditora = Integer.parseInt(campoIdEditora);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira numeros validos");
			return false;
		}
		
		/*
		 * Identificar preenchimento do campos nulos
		 */

		if(campoTitulo.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Titulo nao pode ser nulo");
			return false;
		}
		
		if(campoGenero.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Genero nao pode ser nulo");
			return false;
		}
		
		if(campoQtdEstoque.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade Estoque nao pode ser nulo");
			return false;
		}
		
		if(campoPreco.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Preco nao pode ser nulo");
			return false;
		}
		
		if(campoIdEditora.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Editora nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */

		/*
		 * Identificar outras validacoes
		 */
		
		if(qtdEstoque < 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade Estoque precisa ser maior ou igual a 0");
			return false;
		}
		
		if(preco <= 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Preco precisa ser maior que 0");
			return false;
		}
		
		Editora editora = EditoraDAO.getEditoraById(idEditora);
		if(editora == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Editora nao consta na base de dados");
			return false;
		}
		
		Livro livro = new Livro(campoTitulo, campoGenero, dataPublicada, campoAutor, qtdEstoque, preco, editora);	
		LivroDAO.save(livro);
		
		return true;
		
	}

	public static boolean update(String campoTitulo, String campoGenero, String campoData, 
			String campoAutor, String campoQtdEstoque, String campoPreco, 
			String campoIdEditora, String campoIdLivro) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		Date dataPublicada = Convert.parseDate(campoData);
		
		int qtdEstoque = 0;
		double preco = 0;
		int idEditora = 0;
		int idLivro = 0;
		try {
			qtdEstoque = Integer.parseInt(campoQtdEstoque);
			preco = Double.parseDouble(campoPreco);
			idEditora = Integer.parseInt(campoIdEditora);
			idLivro = Integer.parseInt(campoIdLivro);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira numeros validos");
			return false;
		}
		
		/*
		 * Identificar preenchimento do campos nulos
		 */

		if(campoTitulo.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Titulo nao pode ser nulo");
			return false;
		}
		
		if(campoGenero.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Genero nao pode ser nulo");
			return false;
		}
		
		if(campoQtdEstoque.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade Estoque nao pode ser nulo");
			return false;
		}
		
		if(campoPreco.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Preco nao pode ser nulo");
			return false;
		}
		
		if(campoIdEditora.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Editora nao pode ser nulo");
			return false;
		}
		
		if(campoIdLivro.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Livro nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */

		/*
		 * Identificar outras validacoes
		 */
		
		if(qtdEstoque < 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Quantidade Estoque precisa ser maior ou igual a 0");
			return false;
		}
		
		if(preco <= 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Preco precisa ser maior que 0");
			return false;
		}
		
		Editora editora = EditoraDAO.getEditoraById(idEditora);
		if(editora == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Editora nao consta na base de dados");
			return false;
		}
		
		if(LivroDAO.getLivroById(idLivro) == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Livro nao consta na base de dados");
			return false;
		}
		
		Livro livro = new Livro(campoTitulo, campoGenero, dataPublicada, campoAutor, qtdEstoque, preco, editora);
		livro.setIdLivro(idLivro);
		LivroDAO.update(livro);
		
		return true;
		
	}
	
}
