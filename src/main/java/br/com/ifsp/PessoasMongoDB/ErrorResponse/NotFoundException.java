package br.com.ifsp.PessoasMongoDB.ErrorResponse;

public class NotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String erroInfo;

	public NotFoundException(String mensagem, String erroInfo) {
		super(mensagem);
		this.erroInfo = erroInfo;
	}

	public String getErroInfo() {
		return erroInfo;
	}
	
}
