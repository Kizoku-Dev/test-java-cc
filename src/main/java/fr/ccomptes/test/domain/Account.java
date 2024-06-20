package fr.ccomptes.test.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

  public Account(final String name) {
    this.name = name;
  }
}
