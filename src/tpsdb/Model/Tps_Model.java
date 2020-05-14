package tpsdb.Model;

import SimpleDotEnv.Env;
import java.io.File;
import tpsdb.Model.Entities.Associado;
import tpsdb.Model.Entities.Contrato;
import tpsdb.Model.Entities.Processo;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import nl.cad.tpsparse.tps.TpsFile;
import nl.cad.tpsparse.tps.record.FieldDefinitionRecord;
import nl.cad.tpsparse.tps.record.TableDefinitionRecord;

public class Tps_Model {

    private static final String tpsFolder = Env.get("tpsFolder");
    private static final List<Associado> associados = new ArrayList<>();
    private static final List<Contrato> contratos = new ArrayList<>();
    private static final List<Processo> processos = new ArrayList<>();

    private static List<List<Object>> getTableData(String tableName) {
        List<List<Object>> returned = new ArrayList<>();

        try {
            File file = new File(tpsFolder + "\\" + tableName + ".tps");
            TpsFile tpsFile = new TpsFile(file);

            Map<Integer, TableDefinitionRecord> tables = tpsFile.getTableDefinitions(false);
            tables.entrySet().forEach((entry) -> {
                //COLUNAS
                returned.add(getTableFields(entry));

                //ENTRADAS
                tpsFile.getDataRecords(entry.getKey(), entry.getValue(), false).forEach((rec) -> {
                    returned.add(rec.getValues());
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returned;
    }

    private static List<Object> getTableFields(Map.Entry<Integer, TableDefinitionRecord> entry) {
        List<Object> fieldList = new ArrayList<>();

        List<FieldDefinitionRecord> fields = entry.getValue().getFields();
        fields.forEach((field) -> {
            fieldList.add(field.getFieldName());
        });

        return fieldList;
    }

    public static void setContratos() {
        try {
            String tableName = "ASSEMPRE";

            List<List<Object>> rows = getTableData(tableName);
            for (List<Object> row : rows) {
                try {
                    Contrato contrato = new Contrato();
                    contrato.setAssociadoCodigo(Long.valueOf(row.get(0).toString().replaceAll("[^0-9]", "")));
                    contrato.setNumeroProposta(Long.valueOf(row.get(2).toString().replaceAll("[^0-9]", "")));;
                    contrato.setDataProposta(getCalendarFromTpsDate(Integer.valueOf(row.get(3).toString().replaceAll("[^0-9]", ""))));
                    contrato.setValorFinanciado(new BigDecimal(row.get(4).toString().replaceAll("[^0-9,.]", "")));
                    contrato.setQtdParcelas(Integer.valueOf(row.get(5).toString().replaceAll("[^0-9]", "")));
                    contrato.setValorParcela(new BigDecimal(row.get(6).toString().replaceAll("[^0-9,.]", "")));
                    contrato.setPrimeiroVencimento(getCalendarFromTpsDate(Integer.valueOf(row.get(7).toString().replaceAll("[^0-9]", ""))));
                    contrato.setUltimoVencimento(getCalendarFromTpsDate(Integer.valueOf(row.get(8).toString().replaceAll("[^0-9]", ""))));
                    contrato.setEncerramento(getCalendarFromTpsDate(Integer.valueOf(row.get(9).toString().replaceAll("[^0-9]", ""))));
                    contrato.setDataVencimentoRefinanciamento(getCalendarFromTpsDate(Integer.valueOf(row.get(10).toString().replaceAll("[^0-9]", ""))));
                    contrato.setContratoRecebido(row.get(7).toString());

                    contratos.add(contrato);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }

            }
        } catch (Exception e) {
        }
    }

    public static void setAssociados() {
        try {
            String tableName = "Associad";

            List<List<Object>> rows = getTableData(tableName);
            for (List<Object> row : rows) {
                try {
                    Associado associado = new Associado(
                            Long.valueOf(row.get(0).toString()),
                            row.get(132).toString(), //MAT ESTADO
                            row.get(3).toString(), //FUNCIONARIO ESTADO
                            row.get(2).toString(), //MAT IPE
                            row.get(83).toString(), //MAT ESTADO ANTIGA
                            row.get(91).toString(), //MAT IPE 2
                            row.get(84).toString(), //VINCULO
                            row.get(85).toString(), //PENSIONISTA
                            row.get(5).toString(), //situacao
                            row.get(7).toString(), //NOME
                            row.get(20).toString(), //CPF
                            row.get(14).toString(), //DATA CADASTRO
                            row.get(15).toString(), //DATA EXCLUSAO
                            row.get(16).toString() //DATA OBITO
                    );

                    associado.setRg(row.get(19).toString().replaceAll("[^0-9]", ""));
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar dataNascimento = getCalendarFromTpsDate(Integer.valueOf(row.get(13).toString()));
                    
                    associado.setDtNascimento(dateFormat.format(dataNascimento.getTime()));

                    associados.add(associado);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public static void setProcessos() {
        try {
            String tableName = "PROCESSO";

            List<List<Object>> rows = getTableData(tableName);
            for (List<Object> row : rows) {
                try {
                    Processo processo = new Processo();

                    processo.setCodigo(Long.valueOf(row.get(0).toString()));
                    processo.setAssociado(Long.valueOf(row.get(3).toString()));
                    processo.setAdvogado(Long.valueOf(row.get(4).toString()));

                    processos.add(processo);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public static Associado getAssociado(Long matricula, Long vinculo, Long pensionista) {
        try {
            Long m = matricula;
            Long v = vinculo;
            Long p = pensionista;

            //try{ m = matricula != 0? matricula.toString():"";}catch(Exception e){}
            //try{ v = vinculo != 0? vinculo.toString():"";}catch(Exception e){}
            //try{ p = pensionista != 0? pensionista.toString():"";}catch(Exception e){}
            for (int i = 0; i < associados.size(); i++) {

                Associado a = associados.get(i);
                if ((a.getMatriculaEstado().equals(m) | a.getNroFuncionarioEstado().equals(m)
                        | a.getMatriculaIpe().equals(m) | a.getMatriculaIpe2().equals(m)
                        | a.getMatriculaEstadoAntiga().equals(m))
                        & a.getNroVinculo().equals(v)
                        & a.getNroPensionista().equals(p)) {
                    return a;
                }
            }
            return new Associado(Long.valueOf("0"), matricula.toString(), "", "", "", "", "", "", "", "", "", "", "", "");
        } catch (Exception e) {
            return new Associado(Long.valueOf("0"), matricula.toString(), "", "", "", "", "", "", "", "", "", "", "", "");
        }

    }

    public static Contrato getContrato(Long numeroProposta) {
        Contrato proposta = new Contrato();
        try {
            for (Contrato contrato : contratos) {
                if (contrato.getNumeroProposta() == numeroProposta) {

                }
            }
        } catch (Exception e) {
        }
        return proposta;
    }

    private static Calendar getCalendarFromTpsDate(Integer tpsDateInteger) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1800, 11, 28);

        calendar.add(Calendar.DAY_OF_MONTH, tpsDateInteger);

        return calendar;
    }

    public static List<Associado> getAssociados() {
        return associados;
    }

    public static List<Contrato> getContratos() {
        return contratos;
    }

    public static List<Processo> getProcessos() {
        return processos;
    }

}
