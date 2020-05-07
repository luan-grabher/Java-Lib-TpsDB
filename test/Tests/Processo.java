package Tests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import tpsdb.Model.Entities.Associado;
import tpsdb.Model.Tps_Model;

public class Processo {

    public static void main(String[] args) {
        //set tables
        Tps_Model.setAssociados();
        Tps_Model.setProcessos();

        //get tables
        List<Associado> associados = Tps_Model.getAssociados();
        List<tpsdb.Model.Entities.Processo> processos = Tps_Model.getProcessos();

        Long advogado = Long.valueOf(19);

        //Lista processos do advogado escolhido
        List<tpsdb.Model.Entities.Processo> advogadoProcessos = processos.stream().filter(p -> Objects.equals(p.getAdvogado(), advogado)).collect(Collectors.toList());

        //Percorre associados encontrados
        List<Associado> processosAssociados = new ArrayList<>();
        for (tpsdb.Model.Entities.Processo advogadoProcesso : advogadoProcessos) {
            //Imprime associado
            Optional<Associado> associadoOp = associados.stream().filter(a -> Objects.equals(a.getCodigoAssociado(), advogadoProcesso.getAssociado())).findFirst();

            processosAssociados.add(associadoOp.get());
        }
        
        //Ordena por ordem alfabetica
        processosAssociados.sort(Comparator.comparing(Associado::getNome));
        
        for (Associado associado : processosAssociados) {
            StringBuilder out = new StringBuilder(associado.getNome());
            out.append(" | ");
            out.append(associado.getCpf());
            out.append(" | ");
            out.append(associado.getRg());
            out.append(" | ");
            out.append(associado.getDtNascimento());
            
            System.out.println(out.toString());
        }
    }

}
