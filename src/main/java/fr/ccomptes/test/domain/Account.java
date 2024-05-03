package fr.ccomptes.test.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Nom du compte
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Solde en centimes d'euros
     */
    @Column()
    private long balance;

    public Account() {

    }

    public Account(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
 
    public long getBalance() { return this.balance; }
    public void setBalance(long balance) { this.balance = balance; }
}
