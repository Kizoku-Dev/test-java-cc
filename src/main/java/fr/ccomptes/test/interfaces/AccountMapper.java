package fr.ccomptes.test.interfaces;

import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.interfaces.dto.AccountListResponse;

import java.util.List;

public class AccountMapper {

  public static AccountListResponse accountToDto(final Account account) {
    return new AccountListResponse(account.getId(), account.getName(), account.getBalance());
  }

  public static List<AccountListResponse> accountsToDto(final List<Account> accounts) {
    return accounts.stream().map(AccountMapper::accountToDto).toList();
  }
}
