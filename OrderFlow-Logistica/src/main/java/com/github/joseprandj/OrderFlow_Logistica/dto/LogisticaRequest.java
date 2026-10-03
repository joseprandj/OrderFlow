package com.github.joseprandj.OrderFlow_Logistica.dto;

import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import com.github.joseprandj.OrderFlow_Logistica.model.StatusLogistica;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Quando {@code status} não é informado, o registro é criado como {@code PENDENTE_PAGAMENTO}.
 */
public record LogisticaRequest(
        @NotNull(message = "é obrigatório")
        UUID idPedido,

        StatusLogistica status,

        @Size(max = Logistica.TAMANHO_MAXIMO_OCORRENCIA, message = "deve possuir no máximo 4000 caracteres")
        String ocorrencia
) {

    public LogisticaRequest {
        ocorrencia = trimWhitespace(ocorrencia);
    }
}
