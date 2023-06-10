package br.com.ifsp.PessoasMongoDB.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ifsp.PessoasMongoDB.Model.Pessoa;

public interface PessoaRepository extends MongoRepository<Pessoa, String>{
	
	Optional<Pessoa> findByCod(int cod);

	boolean existsByCod(int cod);
	
	Iterable<Pessoa> findByNome(String nome);
	
	boolean existsByNome(String nome);

	Iterable<Pessoa> findBySobrenome(String sobrenome);
	
	boolean existsBySobrenome(String sobrenome);
	
	void deleteByCod(int cod);
	
}
