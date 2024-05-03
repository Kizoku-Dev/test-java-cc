package fr.ccomptes.test.application;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    AccountService accountService;
    
    @Test
    public void whenSufficientFunds_thenTransactionSucceeds() throws Exception {

        Account aliceAccount = new Account("alice"); aliceAccount.setBalance(100L);
        Account bobAccount = new Account("bob"); bobAccount.setBalance(0L);

        when(accountRepository.findByName("alice")).thenReturn(aliceAccount);
        when(accountRepository.findByName("bob")).thenReturn(bobAccount);

        // toutes les conditions sont réunies pour que la transaction réussisse
        accountService.addTransaction("alice", "bob", 100L);

        assertEquals(aliceAccount.getBalance(), 0L); // Alice a tout 
        assertEquals(bobAccount.getBalance(), 100L); // donné à Bob
        verify(accountRepository, times(1)).findByName("alice"); // l'accountRepository a du être appelé
        verify(accountRepository, times(1)).findByName("bob"); // une fois par intervenant dans la transaction
        verify(transactionRepository, times(1)).save(any()); // une transaction a du être sauvegardée 

    }

    @Test
    /**
     * TODO : vérifier qu'une exception est produite quand les fonds sont insuffisants pour la transaction
     * @throws Exception
     */
    public void whenInsufficientFunds_thenTransactionFails() throws Exception {
        assert false;
    }

}
