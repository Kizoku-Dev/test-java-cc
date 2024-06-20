package fr.ccomptes.test.interfaces.dto;

public record TransactionRequest(long srcId, long destId, long amount) {}
