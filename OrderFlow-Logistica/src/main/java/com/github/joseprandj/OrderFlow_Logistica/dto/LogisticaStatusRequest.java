package com.github.joseprandj.OrderFlow_Logistica.dto;

import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import com.github.joseprandj.OrderFlow_Logistica.model.StatusLogistica;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Alteração de status. A {@code ocorrencia} é sempre substituída juntamente com o status;
 * quando não informada, a ocorrência passa a ser {@code null}.
 */
public record LogisticaStatusRequest(
        @NotNull(message = "é obrigatório")
        StatusLogistica status,

        @Size(max = Logistica.TAMANHO_MAXIMO_OCORRENCIA, message = "deve possuir no máximo 4000 caracteres")
        String ocorrencia
) {

    public LogisticaStatusRequest {
        ocorrencia = trimWhitespace(ocorrencia);
    }
}
