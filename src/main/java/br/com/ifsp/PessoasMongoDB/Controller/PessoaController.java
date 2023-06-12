package br.com.ifsp.PessoasMongoDB.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.ifsp.PessoasMongoDB.ErrorResponse.ErrorResponse;
import br.com.ifsp.PessoasMongoDB.ErrorResponse.FoundException;
import br.com.ifsp.PessoasMongoDB.ErrorResponse.NotFoundException;
import br.com.ifsp.PessoasMongoDB.ErrorResponse.OtherException;
import br.com.ifsp.PessoasMongoDB.Model.Pessoa;
import br.com.ifsp.PessoasMongoDB.Repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
/*
 * Endpoints (URIs)
 * 	/pessoas						--> Obtém todas as pessoas.
 * 	/pessoas/5						--> Obtém a pessoa de código 5.
 * 	/pessoas/nome/Marcelo        	--> Obtém as pessoas que contém no nome Marcelo.
 * 	/pessoas/sobrenome/Alves     	--> Obtém as pessoas que contém no sobrenome Alves.
 * 	/pessoa/pessoa               	--> Cadastra uma nova pessoa.
 * 	/pessoa/10      	(PUT)      	--> Altera a pessoa de código 10.     
 * 	/pessoa/9      		(DELETE)  	--> Elimina a pessoa de código 9.
 * 
 */

public class PessoaController {

	@Autowired
	public PessoaRepository pessoaRepository;
	
	public PessoaController() {
	}

	// Gets...
	@GetMapping
	public Iterable<Pessoa> getPessoas() {
		if (this.pessoaRepository.count() == 0) {
			throw new NotFoundException("Cadastro de pessoas está vazio!",null);
		}
		return this.pessoaRepository.findAll();
	}

	@GetMapping("/{cod}")
	public Optional<Pessoa> getPessoaByCod(@PathVariable int cod) {
		if (this.pessoaRepository.existsByCod(cod)) {
			return this.pessoaRepository.findByCod(cod);
		}
		throw new NotFoundException("Não localizada a pessoa pelo código!","Código: " + cod);
	}
	
	@GetMapping("/nome/{nome}")
	public Iterable<Pessoa> getPessoasByNome(@PathVariable String nome) {
		if (this.pessoaRepository.existsByNome(nome)) {
			return this.pessoaRepository.findByNome(nome);
		}
		throw new NotFoundException("Não localizadas pessoas pelo nome!","Nome: " + nome);
	}
	
	@GetMapping("/sobrenome/{sobrenome}")
	public Iterable<Pessoa> getPessoasBySobrenome(@PathVariable String sobrenome) {
		if (this.pessoaRepository.existsBySobrenome(sobrenome)) {
			return this.pessoaRepository.findBySobrenome(sobrenome);
		}
		throw new NotFoundException("Não localizadas pessoas pelo sobrenome!","Sobrenome: " + sobrenome);
	}
	
	@GetMapping("/total")
	public String getContarPessoas() {
		return "Total de pessoas: " + this.pessoaRepository.count();
	}

	// Post...
	@PostMapping("/pessoa")
	public ResponseEntity<Pessoa> postPessoa(@RequestBody Pessoa pessoa) {
		Pessoa pessoaRet;
		if (this.pessoaRepository.existsByCod(pessoa.getCod())) {
			throw new FoundException("Código da pessoa já existe!","Código: " + pessoa.getCod());
		}
		pessoaRet = this.pessoaRepository.save(pessoa);
		return new ResponseEntity<>(pessoaRet,
									HttpStatus.OK
									);
	}
	
	/*
	private boolean validarPessoaExistente(Pessoa pessoa) {
		for (Pessoa p: this.pessoas) {
			if (p.getNome().equals(pessoa.getNome()) &&
					p.getSobrenome().equals(pessoa.getSobrenome())) {
					// Pessoa com nome e sobrenome existentes!
					return true;
			}
		}
		return false;
	}
	*/

	// Put...
	@PutMapping("/{cod}")
	public ResponseEntity<Pessoa> putPessoa(@PathVariable int cod, @RequestBody Pessoa pessoa) {
		if (!this.pessoaRepository.existsByCod(cod)) {
			throw new NotFoundException("Código da pessoa não localizado!","Código: " + cod);
		}
		if (pessoa.getCod() != cod) {
			throw new OtherException("Código da Pessoa é diferente do código informado na entidade (alteração)!",
										"Código pesquisa: " + cod + ", Código Pessoa/Entidade para alterar: " + pessoa.getCod() + ".");
		}
		
		Pessoa changePessoa;
		pessoa.setId(this.pessoaRepository.findByCod(cod).get().getId());
		changePessoa = this.pessoaRepository.save(pessoa);
		return new ResponseEntity<>(changePessoa,
									HttpStatus.OK)
									;
	}

	// Delete...
	@DeleteMapping("/{cod}")
	public ResponseEntity<Optional<Pessoa>> deletePessoa(@PathVariable int cod) {
		Optional<Pessoa> pessoa;
		if (!this.pessoaRepository.existsByCod(cod)) {
			throw new NotFoundException("Código da pessoa inexistente, dados não removidos!",
					"Código: " + cod);
		}
		pessoa = this.pessoaRepository.findByCod(cod);
		this.pessoaRepository.deleteByCod(cod);
		return new ResponseEntity<>(pessoa,
									HttpStatus.OK
									);
	}

	@ExceptionHandler(NotFoundException.class)
	private ResponseEntity<ErrorResponse> handlerNotFoundException(NotFoundException nfe) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
														HttpStatus.NOT_FOUND.toString(),
														nfe.getMessage(),
														nfe.getErroInfo(),
														this.getClass().toString()
														);
		
		return new ResponseEntity<>(errorResponse,
									HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(FoundException.class)
	private ResponseEntity<ErrorResponse> handlerFoundException(FoundException fe) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FOUND.value(),
														HttpStatus.FOUND.toString(),
														fe.getMessage(),
														fe.getErroInfo(),
														this.getClass().toString()
														);
		
		return new ResponseEntity<>(errorResponse,
									HttpStatus.FOUND);
	}
	
	@ExceptionHandler(OtherException.class)
	private ResponseEntity<ErrorResponse> handlerOtherException(OtherException oe) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(),
														HttpStatus.NOT_ACCEPTABLE.toString(),
														oe.getMessage(),
														oe.getErroInfo(),
														this.getClass().toString()
														);
		return new ResponseEntity<>(errorResponse,
									HttpStatus.NOT_ACCEPTABLE);
	}
	
}
