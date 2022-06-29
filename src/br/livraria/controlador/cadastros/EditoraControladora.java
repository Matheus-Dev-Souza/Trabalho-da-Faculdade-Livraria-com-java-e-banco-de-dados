package br.livraria.controlador.cadastros;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.livraria.dao.EditoraDAO;
import br.livraria.dao.LivroDAO;
import br.livraria.model.Editora;
import br.livraria.model.Livro;

public class EditoraControladora {

	private EditoraControladora() {}
	
	private static Object[][] get(String busca) {
		
		Object[][] tabela;
		
		Vector<Editora> editoras = EditoraDAO.getEditoras();
		
		if (busca != "") {
			
			Vector<Editora> editorasSelecionadas = new Vector<Editora>();
			
			for(int i = 0; i < editoras.size(); i++) {
				
				if(editoras.get(i).getNome().toLowerCase().contains(busca.toLowerCase()))
					editorasSelecionadas.add(editoras.get(i));
				
			}
			
			editoras = editorasSelecionadas;
			
		}
		
		tabela = new Object[editoras.size()][4];
		
		for(int i = 0; i < editoras.size(); i++) {
			
			tabela[i][0] = editoras.get(i).getIdEditora();
			tabela[i][1] = editoras.get(i).getNome();
			tabela[i][2] = editoras.get(i).getCnpj();
			tabela[i][3] = editoras.get(i).getEndereco();
			
		}
		
		return tabela;
		
	}
	
	public static DefaultTableModel updateTable(String busca) {
		
		return new DefaultTableModel(
			EditoraControladora.get(busca),
			new String[] {
				"ID", "Editora", "CNPJ", "Endereco"
			}
		);
		
	}
	
	public static boolean delete(String idEditora) {
		
		int id;
		try {
			id = Integer.parseInt(idEditora);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
		Editora editora = EditoraDAO.getEditoraById(id);
		if(editora != null) {
			
			Vector<Livro> livros = LivroDAO.getLivros();
			for(int i = 0; i < livros.size(); i++) {
				if(livros.get(i).getEditora().getIdEditora() == editora.getIdEditora()) LivroDAO.deleteByID(livros.get(i).getIdLivro());
			}
			
			EditoraDAO.deleteByID(id);
			return true;
		}
		
		JOptionPane.showMessageDialog(new JFrame(), "Editora nao consta na base de dados");
		return false;
		
	}
	
	public static Editora search(String idEditora) {
		
		int id;
		try {
			id = Integer.parseInt(idEditora);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return null;
		}
		
		Editora editora = EditoraDAO.getEditoraById(id);
		if(editora == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Editora nao consta na base de dados");
			return null;
		}
		
		return editora;
	}
	
	public static boolean save(String campoNome, String campoCNPJ, String campoEndereco) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(campoNome.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Nome nao pode ser nulo");
			return false;
		}
		
		if(campoCNPJ.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CNPJ nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */
		
		Vector<Editora> editoras = EditoraDAO.getEditoras();
		for(int i = 0; i < editoras.size(); i++) {
			if(campoCNPJ.equals(editoras.get(i).getCnpj())) {
				JOptionPane.showMessageDialog(new JFrame(), "CNPJ ja consta na base de dados");
				return false;
			}
		}
		
		/*
		 * Identificar outras validacoes
		 */
		
		if(campoCNPJ.length() != 14) {
			JOptionPane.showMessageDialog(new JFrame(), "CNPJ precisa conter 14 caracteres");
			return false;
		}
		
		Editora editora = new Editora(campoNome, campoCNPJ, campoEndereco);
		EditoraDAO.save(editora);
		
		return true;
		
	}

	public static boolean update(String campoNome, String campoCNPJ, String campoEndereco, String campoIdEditora) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		int idEditora = 0;
		try {
			idEditora = Integer.parseInt(campoIdEditora);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(campoNome.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Nome nao pode ser nulo");
			return false;
		}
		
		if(campoCNPJ.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CNPJ nao pode ser nulo");
			return false;
		}
		
		if(idEditora == 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Editora nao pode ser nula");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */
		
		Vector<Editora> editoras = EditoraDAO.getEditoras();
		for(int i = 0; i < editoras.size(); i++) {
			if(campoCNPJ.equals(editoras.get(i).getCnpj())) {
				JOptionPane.showMessageDialog(new JFrame(), "CNPJ ja consta na base de dados");
				return false;
			}
		}
		
		/*
		 * Identificar outras validacoes
		 */
		
		if(campoCNPJ.length() != 14) {
			JOptionPane.showMessageDialog(new JFrame(), "CNPJ precisa conter 14 caracteres");
			return false;
		}
		
		Editora editora = new Editora(campoNome, campoCNPJ, campoEndereco);
		editora.setIdEditora(idEditora);
		EditoraDAO.update(editora);
		
		return true;
		
	}
	
}
