package br.univel;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.univel.abs.SqlGen;
import br.univel.anotacoes.Coluna;
import br.univel.anotacoes.Tabela;

public class SqlGenImpl extends SqlGen {

	@Override
	public String getCreateTable(Connection con, Object obj) {

		try {

			StringBuilder sb = new StringBuilder();

			String nomeTabela;
			if (obj.getClass().isAnnotationPresent(Tabela.class)) {

				Tabela anotacaoTabela = obj.getClass().getAnnotation(Tabela.class);
				nomeTabela = anotacaoTabela.value();

			} else {
				nomeTabela = obj.getClass().getSimpleName().toUpperCase();

			}
			sb.append("CREATE TABLE ").append(nomeTabela).append(" (");

			Field[] atributos = obj.getClass().getDeclaredFields();

			String cp = "";

			for (int i = 0; i < atributos.length; i++) {

				Field field = atributos[i];

				String nomeColuna;
				String tipoColuna;
				int tamanhoColuna = 0;

				if (field.isAnnotationPresent(Coluna.class)) {
					Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

					if (anotacaoColuna.nome().isEmpty()) {
						nomeColuna = field.getName().toUpperCase();
						tamanhoColuna = 0;
					} else {
						nomeColuna = anotacaoColuna.nome();
						tamanhoColuna = anotacaoColuna.tamanho();

						if (anotacaoColuna.pk()) {
							if (cp.equalsIgnoreCase("")) {
								cp = anotacaoColuna.nome();
							} else {
								cp = cp + ", " + anotacaoColuna.nome();
							}

						}
					}

				} else {
					nomeColuna = field.getName().toUpperCase();
				}

				Class<?> tipoParametro = field.getType();

				if (tipoParametro.equals(String.class)) {
					tipoColuna = "VARCHAR(" + tamanhoColuna + ")";

				} else if (tipoParametro.equals(int.class)) {
					tipoColuna = "INT";
				} else {
					tipoColuna = "DESCONHECIDO";
				}

				if (i > 0) {
					sb.append(",");
				}

				sb.append("\n\t").append(nomeColuna).append(' ').append(tipoColuna);
			}

			if (cp != "") {
				sb.append(",\n\t PRIMARY KEY(" + cp + ")");
			}

			sb.append("\n);");

			return sb.toString();

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getDropTable(Connection con, Object obj) {
		try {

			StringBuilder sb = new StringBuilder();

			String nomeTabela;
			if (obj.getClass().isAnnotationPresent(Tabela.class)) {

				Tabela anotacaoTabela = obj.getClass().getAnnotation(Tabela.class);
				nomeTabela = anotacaoTabela.value();

			} else {
				nomeTabela = obj.getClass().getSimpleName().toUpperCase();

			}
			sb.append("DROP TABLE ").append(nomeTabela).append(";");

			return sb.toString();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PreparedStatement getSqlInsert(Connection con, Object obj) {
		Class<? extends Object> cl = obj.getClass();

		StringBuilder sb = new StringBuilder();
		PreparedStatement ps = null;

		String nomeTabela;
		if (cl.isAnnotationPresent(Tabela.class)) {
			Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
			nomeTabela = anotacaoTabela.value();

		} else {
			nomeTabela = cl.getSimpleName().toUpperCase();
		}
		sb.append("INSERT INTO ").append(nomeTabela).append(" (");

		Field[] atributos = cl.getDeclaredFields();

		for (int i = 0; i < atributos.length; i++) {

			Field field = atributos[i];

			String nomeColuna;

			if (field.isAnnotationPresent(Coluna.class)) {
				Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

				if (anotacaoColuna.nome().isEmpty()) {
					nomeColuna = field.getName().toUpperCase();
				} else {
					nomeColuna = anotacaoColuna.nome();
				}

			} else {
				nomeColuna = field.getName().toUpperCase();
			}

			if (i > 0) {
				sb.append(", ");
			}

			sb.append(nomeColuna);
		}

		sb.append(") VALUES (");

		for (int i = 0; i < atributos.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append('?');
		}
		sb.append(')');

		try {
			ps = con.prepareStatement(sb.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ps;

	}

	@Override
	public PreparedStatement getSqlSelectAll(Connection con, Object obj) {
		PreparedStatement ps = null;

		try {
			StringBuilder sb = new StringBuilder();

			String nomeTabela;
			if (obj.getClass().isAnnotationPresent(Tabela.class)) {

				Tabela anotacaoTabela = obj.getClass().getAnnotation(Tabela.class);
				nomeTabela = anotacaoTabela.value();

			} else {
				nomeTabela = obj.getClass().getSimpleName().toUpperCase();

			}
			sb.append("SELECT * FROM ").append(nomeTabela);

			try {
				ps = con.prepareStatement(sb.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ps;

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PreparedStatement getSqlSelectById(Connection con, Object obj) {
		PreparedStatement ps = null;

		try {
			StringBuilder sb = new StringBuilder();

			String nomeTabela;
			if (obj.getClass().isAnnotationPresent(Tabela.class)) {

				Tabela anotacaoTabela = obj.getClass().getAnnotation(Tabela.class);
				nomeTabela = anotacaoTabela.value();

			} else {
				nomeTabela = obj.getClass().getSimpleName().toUpperCase();

			}

			Field[] atributos = obj.getClass().getDeclaredFields();
			String cp = "";
			for (int i = 0; i < atributos.length; i++) {

				Field field = atributos[i];

				if (field.isAnnotationPresent(Coluna.class)) {
					Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

					if (anotacaoColuna.pk()) {
						if (cp.equalsIgnoreCase("")) {
							cp = anotacaoColuna.nome();
						} else {
							cp = cp + ", " + anotacaoColuna.nome();
						}
					}
				}
			}

			sb.append("SELECT * FROM ").append(nomeTabela).append(" WHERE ").append(cp).append(" = ?");

			try {
				ps = con.prepareStatement(sb.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ps;

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PreparedStatement getSqlUpdateById(Connection con, Object obj) {
		Class<? extends Object> cl = obj.getClass();

		StringBuilder sb = new StringBuilder();
		PreparedStatement ps = null;

		String nomeTabela;
		if (cl.isAnnotationPresent(Tabela.class)) {
			Tabela anTabela = cl.getAnnotation(Tabela.class);
			nomeTabela = anTabela.value();

		} else {
			nomeTabela = cl.getSimpleName().toUpperCase();
		}
		sb.append("UPDATE ").append(nomeTabela).append(" SET \n");

		Field[] atributos = cl.getDeclaredFields();
		String cp = "";

		for (int i = 0; i < atributos.length; i++) {

			Field field = atributos[i];
			String nomeColuna;

			if (field.isAnnotationPresent(Coluna.class)) {
				Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

				if (anotacaoColuna.nome().isEmpty()) {
					nomeColuna = field.getName().toUpperCase();
				} else {
					nomeColuna = anotacaoColuna.nome();
				}

				if (anotacaoColuna.pk()) {
					cp = nomeColuna;
				}

			} else {
				nomeColuna = field.getName().toUpperCase();
			}

			if (nomeColuna != cp) {
				sb.append("  ").append(nomeColuna).append(" = ?");

				if (i < atributos.length - 1) {
					sb.append(", \n");
				}
			}
		}

		sb.append("\n WHERE \n  ").append(cp).append(" = ?");

		try {
			ps = con.prepareStatement(sb.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement getSqlDeleteById(Connection con, Object obj) {

		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();

			String nomeTabela;
			if (obj.getClass().isAnnotationPresent(Tabela.class)) {

				Tabela anotacaoTabela = obj.getClass().getAnnotation(Tabela.class);
				nomeTabela = anotacaoTabela.value();

			} else {
				nomeTabela = obj.getClass().getSimpleName().toUpperCase();
			}

			Field[] atributos = obj.getClass().getDeclaredFields();
			String cp = "";
			for (int i = 0; i < atributos.length; i++) {

				Field field = atributos[i];

				if (field.isAnnotationPresent(Coluna.class)) {
					Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

					if (anotacaoColuna.pk()) {
						if (cp.equalsIgnoreCase("")) {
							cp = anotacaoColuna.nome();
						} else {
							cp = cp + ", " + anotacaoColuna.nome();
						}
					}
				}
			}

			sb.append("DELETE FROM ").append(nomeTabela).append(" WHERE ").append(cp).append(" = ?");

			try {
				ps = con.prepareStatement(sb.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}

		return ps;
	}

}
