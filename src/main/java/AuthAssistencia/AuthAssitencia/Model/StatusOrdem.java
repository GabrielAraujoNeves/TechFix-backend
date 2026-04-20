package AuthAssistencia.AuthAssitencia.Model;

public enum StatusOrdem {
    PENDENTE("Pendente"),
    CONSERTANDO("Consertando"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private String descricao;

    StatusOrdem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}