package fr.ccomptes.test.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
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

    public Transaction(final Account from, final Account to, final long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
