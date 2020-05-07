package tpsdb.Model.Entities;

public class Processo {
    private Long codigo;
    private Long associado;
    private Long advogado;

    public Processo() {
    }
    
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getAssociado() {
        return associado;
    }

    public void setAssociado(Long associado) {
        this.associado = associado;
    }

    public Long getAdvogado() {
        return advogado;
    }

    public void setAdvogado(Long advogado) {
        this.advogado = advogado;
    }
    
    
}
