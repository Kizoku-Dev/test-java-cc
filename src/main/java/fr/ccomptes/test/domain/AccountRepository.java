package fr.ccomptes.test.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  Account findByName(String name);

  List<Account> findByNameContainsIgnoreCase(String name);

  boolean existsByIdAndApiKey(long id, String apiKey);
}
