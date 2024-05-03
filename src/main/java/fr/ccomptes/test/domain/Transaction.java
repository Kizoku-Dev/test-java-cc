package fr.ccomptes.test.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Compte débiteur
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Account from;
    /**
     * Compte créditeur
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Account to;
    /**
     * Montant de la transaction
     */
    @Column()
    private Long amount;

    public Transaction() {}

    public Transaction(Account from, Account to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getFrom() { return from; }
    public void setFrom(Account from) { this.from = from; }

    public Account getTo() { return to; }
    public void setTo(Account to) { this.to = to; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
}
