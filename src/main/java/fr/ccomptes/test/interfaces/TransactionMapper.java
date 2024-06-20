package fr.ccomptes.test.interfaces;

import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.interfaces.dto.TransactionResponse;

import java.util.List;

public class TransactionMapper {

  public static TransactionResponse transactionToDto(final Transaction transaction) {
    return new TransactionResponse(
      transaction.getFrom().getName(),
      transaction.getTo().getName(),
      transaction.getAmount()
    );
  }

  public static List<TransactionResponse> transactionsToDto(final List<Transaction> transactions) {
    return transactions.stream().map(TransactionMapper::transactionToDto).toList();
  }
}
