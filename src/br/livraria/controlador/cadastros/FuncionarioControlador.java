package br.livraria.controlador.cadastros;

import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.livraria.dao.FuncionarioDAO;
import br.livraria.dao.PessoaDAO;
import br.livraria.model.Funcionario;
import br.livraria.util.Convert;

public class FuncionarioControlador {

	private FuncionarioControlador() {}
	
	private static Object[][] get(String busca) {
		
		Object[][] tabela;
		
		Vector<Funcionario> funcionarios = FuncionarioDAO.getFuncionarios();
		
		if (busca != "") {
			
			Vector<Funcionario> funcionariosSelecionados = new Vector<Funcionario>();
			
			for(int i = 0; i < funcionarios.size(); i++) {
				
				if(funcionarios.get(i).getNome().toLowerCase().contains(busca.toLowerCase()))
					funcionariosSelecionados.add(funcionarios.get(i));
				
			}
			
			funcionarios = funcionariosSelecionados;
			
		}
		
		tabela = new Object[funcionarios.size()][11];
		
		for(int i = 0; i < funcionarios.size(); i++) {
			
			tabela[i][0] = funcionarios.get(i).getIdFuncionario();
			tabela[i][1] = funcionarios.get(i).getNome();
			tabela[i][2] = funcionarios.get(i).getCpf();
			tabela[i][3] = funcionarios.get(i).getDataContrato();
			tabela[i][4] = funcionarios.get(i).getEndereco();
			tabela[i][5] = funcionarios.get(i).getCidade();
			tabela[i][6] = funcionarios.get(i).getEstado();
			tabela[i][7] = funcionarios.get(i).getTelefone();
			tabela[i][8] = funcionarios.get(i).getEmail();
			tabela[i][9] = funcionarios.get(i).getSenha();
			tabela[i][10] = funcionarios.get(i).printPost();
			
		}
		
		return tabela;
		
	}
	
	public static DefaultTableModel updateTable(String busca) {
		
		return new DefaultTableModel(
			FuncionarioControlador.get(busca),
			new String[] {
				"ID", "Nome", "CPF", "Contrato", "Endereco", "Cidade", "Estado", "Telefone", "Email", "Senha", "Cargo"
			}
		);
		
	}
	
	public static boolean delete(String idFuncionario) {
		
		int id;
		try {
			id = Integer.parseInt(idFuncionario);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
		Funcionario funcionario = FuncionarioDAO.getFuncionarioById(id);
		if(funcionario != null) {
			FuncionarioDAO.deleteByID(id);
			PessoaDAO.deleteByCpf(funcionario.getCpf());
			return true;
		}
		
		JOptionPane.showMessageDialog(new JFrame(), "Funcionario nao consta na base de dados");
		return false;
		
	}
	
	public static Funcionario search(String idFuncionario) {
		
		int id;
		try {
			id = Integer.parseInt(idFuncionario);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return null;
		}
		
		Funcionario funcionario = FuncionarioDAO.getFuncionarioById(id);
		if(funcionario == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Funcionario nao consta na base de dados");
			return null;
		}
		
		return funcionario;
	}
	
	public static boolean save(String campoCpf, String campoNome, String campoEndereco,
			String campoCidade, String campoEstado, String campoTelefone, String campoEmail,
			String campoSenha, String campoDataContrato, boolean administrador) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		Date dataContrato = Convert.parseDate(campoDataContrato);
		
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
		
		if(campoSenha.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Senha nao pode ser nulo");
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
		
		Funcionario funcionario = new Funcionario(campoCpf, campoNome, campoEndereco, campoCidade,
				campoEstado, campoTelefone, campoEmail, campoSenha, dataContrato, administrador);
		FuncionarioDAO.save(funcionario);
		
		return true;
		
	}

	public static boolean update(String campoCpf, String campoNome, String campoEndereco,
			String campoCidade, String campoEstado, String campoTelefone, String campoEmail,
			String campoSenha, String campoDataContrato, boolean administrador, String campoIdFuncionario) {
		
		/*
		 * Converter as Strings para seus respectivos tipos primitivos
		 */
		
		Date dataContrato = Convert.parseDate(campoDataContrato);
		
		int idFuncionario = 0;
		try {
			idFuncionario = Integer.parseInt(campoIdFuncionario);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Insira um numero valido");
			return false;
		}
		
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
		
		if(campoSenha.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Senha nao pode ser nulo");
			return false;
		}
		
		if(campoIdFuncionario.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Funcionario nao pode ser nulo");
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
		
		Funcionario funcionario = new Funcionario(campoCpf, campoNome, campoEndereco, campoCidade, campoEstado, campoTelefone, campoEmail, campoSenha, dataContrato, administrador);
		funcionario.setIdFuncionario(idFuncionario);
		FuncionarioDAO.update(funcionario);
		
		return true;
		
	}
	
}
