package br.livraria.controlador.menu;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import br.livraria.dao.FuncionarioDAO;
import br.livraria.model.Funcionario;

/*
 * Responsavel por gerenciar o Login
 */
public class LoginControlador {
	
	private LoginControlador() {}
	
	private static Funcionario funcionarioLogado;
	
	/*
	 * Responsavel por validar as entradas de id e senha
	 */
	public static boolean fazerLogin(String idUsuario, String senha) {
		
		System.out.println("ID Usuario : " + idUsuario);
		System.out.println("Senha : " + senha);
		
		int id;
		try {
			id = Integer.parseInt(idUsuario);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Usuario precisa ser um numero valido");
			return false;
		}
		
		Funcionario funcionario = FuncionarioDAO.getFuncionarioById(id);
		
		if(funcionario == null) {
			JOptionPane.showMessageDialog(new JFrame(), "ID Usuario nao encontrado");
			return false;
		}
		
		if(funcionario.getSenha().equals(senha)) {
			funcionarioLogado = funcionario;
			return true;
		} 
			
		JOptionPane.showMessageDialog(new JFrame(), "Senha invalida");
		return false;
		
	}

	/*
	 * Retorna o funcionario que esta logado
	 */
	public static Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}
	
}
