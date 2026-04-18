package br.edu.atitus.currencyservice.dtos;
// Record e Imutavel (DTO)
public record CurrencyDTO(
        String sourceCurrency,
        String targetCurrency,
        Double conversionRate,
        String environment
) {

}
