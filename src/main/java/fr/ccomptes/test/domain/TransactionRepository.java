package fr.ccomptes.test.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  @Query("SELECT t FROM Transaction t WHERE " +
    "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
    "(:maxAmount IS NULL OR t.amount <= :maxAmount)")
  List<Transaction> findByAmountBetween(
    @Param("minAmount") Long minAmount,
    @Param("maxAmount") Long maxAmount);
}
