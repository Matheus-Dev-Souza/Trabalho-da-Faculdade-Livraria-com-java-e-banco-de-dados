package br.livraria.controlador.cadastros;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.livraria.dao.PessoaDAO;
import br.livraria.model.Pessoa;

public class PessoaControladora {

	private PessoaControladora() {}
	
	private static Object[][] get(String busca) {
		
		Object[][] tabela;
		
		Vector<Pessoa> pessoas = PessoaDAO.getPessoas();
		
		if (busca != "") {
			
			Vector<Pessoa> pessoasSelecionadas = new Vector<Pessoa>();
			
			for(int i = 0; i < pessoas.size(); i++) {
				
				if(pessoas.get(i).getNome().toLowerCase().contains(busca.toLowerCase()))
					pessoasSelecionadas.add(pessoas.get(i));
				
			}
			
			pessoas = pessoasSelecionadas;
			
		}
		
		tabela = new Object[pessoas.size()][7];
		
		for(int i = 0; i < pessoas.size(); i++) {
			
			tabela[i][0] = pessoas.get(i).getCpf();
			tabela[i][1] = pessoas.get(i).getNome();
			tabela[i][2] = pessoas.get(i).getEndereco();
			tabela[i][3] = pessoas.get(i).getCidade();
			tabela[i][4] = pessoas.get(i).getEstado();
			tabela[i][5] = pessoas.get(i).getTelefone();
			tabela[i][6] = pessoas.get(i).getEmail();
			
		}
		
		return tabela;
		
	}
	
	public static DefaultTableModel updateTable(String busca) {
		
		return new DefaultTableModel(
			PessoaControladora.get(busca),
			new String[] {
				"CPF", "Nome", "Endereco", "Cidade", "Estado", "Telefone", "Email"
			}
		);
		
	}
	
	public static boolean delete(String cpfPessoa) {
		
		if(cpfPessoa.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao pode ser nulo");
			return false;
		}
		
		if(cpfPessoa.length() != 11) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF precisa conter 11 caracteres");
			return false;
		}
		
		Pessoa pessoa = PessoaDAO.getPessoaByCpf(cpfPessoa);
		if(pessoa != null) {
			PessoaDAO.deleteByCpf(cpfPessoa);
			return true;
		}
		
		JOptionPane.showMessageDialog(new JFrame(), "Cliente nao consta na base de dados");
		return false;
		
	}
	
	public static Pessoa search(String cpfPessoa) {
		
		if(cpfPessoa.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao pode ser nulo");
			return null;
		}
		
		if(cpfPessoa.length() != 11) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF precisa conter 11 caracteres");
			return null;
		}
		
		Pessoa pessoa = PessoaDAO.getPessoaByCpf(cpfPessoa);
		if(pessoa == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Cliente nao consta na base de dados");
			return null;
		}
		
		return pessoa;
	}
	
	public static boolean save(String campoCpf, String campoNome, String campoEndereco,
			String campoCidade, String campoEstado, String campoTelefone, String campoEmail) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(campoCpf.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao pode ser nulo");
			return false;
		}
		
		if(campoNome.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Nome nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */
		
		if(PessoaDAO.getPessoaByCpf(campoCpf) != null) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF ja consta na base de dados");
			return false;
		}

		/*
		 * Identificar outras validacoes
		 */
		
		if(campoCpf.length() != 11) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF precisa conter 11 caracteres");
			return false;
		}
		
		if(!campoEstado.equals("") && campoEstado.length() != 2) {
			JOptionPane.showMessageDialog(new JFrame(), "Estado precisa conter 2 caracteres");
			return false;
		}
		
		Pessoa pessoa = new Pessoa(campoCpf, campoNome, campoEndereco, campoCidade, campoEstado, campoTelefone, campoEmail);
		PessoaDAO.save(pessoa);
		
		return true;
		
	}

	public static boolean update(String campoCpf, String campoNome, String campoEndereco,
			String campoCidade, String campoEstado, String campoTelefone, String campoEmail) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		/*
		 * Identificar preenchimento do campos nulos
		 */
		
		if(campoCpf.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao pode ser nulo");
			return false;
		}
		
		if(campoNome.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Nome nao pode ser nulo");
			return false;
		}
		
		/*
		 * Identificar validacao dos campos unicos
		 */
		
		if(PessoaDAO.getPessoaByCpf(campoCpf) == null) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF nao consta na base de dados");
			return false;
		}

		/*
		 * Identificar outras validacoes
		 */
		
		if(campoCpf.length() != 11) {
			JOptionPane.showMessageDialog(new JFrame(), "CPF precisa conter 11 caracteres");
			return false;
		}
		
		if(!campoEstado.equals("") && campoEstado.length() != 2) {
			JOptionPane.showMessageDialog(new JFrame(), "Estado precisa conter 2 caracteres");
			return false;
		}
		
		Pessoa pessoa = new Pessoa(campoCpf, campoNome, campoEndereco, campoCidade, campoEstado, campoTelefone, campoEmail);
		PessoaDAO.update(pessoa);
		
		return true;
		
	}
	
}
