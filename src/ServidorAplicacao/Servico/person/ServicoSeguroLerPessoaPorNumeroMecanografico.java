package ServidorAplicacao.Servico.person;

import Dominio.Funcionario;
import Dominio.Person;
import ServidorAplicacao.ServicoAutorizacao;
import ServidorAplicacao.ServicoSeguro;
import ServidorAplicacao.Servico.exceptions.NotExecuteException;
import ServidorPersistenteJDBC.IFuncionarioPersistente;
import ServidorPersistenteJDBC.IPessoaPersistente;
import ServidorPersistenteJDBC.SuportePersistente;

/**
 * @author Fernanda & T�nia
 *  
 */
public class ServicoSeguroLerPessoaPorNumeroMecanografico extends ServicoSeguro {
    private Person pessoa = null;

    private int numeroMecanografico;

    public ServicoSeguroLerPessoaPorNumeroMecanografico(ServicoAutorizacao servicoAutorizacao,
            int numeroMecanografico) {
        super(servicoAutorizacao);
        this.numeroMecanografico = numeroMecanografico;
    }

    public void execute() throws NotExecuteException {
        IFuncionarioPersistente iFuncionarioPersistente = SuportePersistente.getInstance()
                .iFuncionarioPersistente();
        Funcionario funcionario = null;
        if ((funcionario = iFuncionarioPersistente
                .lerFuncionarioSemHistoricoPorNumMecanografico(numeroMecanografico)) == null)
            throw new NotExecuteException();

        IPessoaPersistente iPessoaPersistente = SuportePersistente.getInstance().iPessoaPersistente();
        if ((pessoa = iPessoaPersistente.lerPessoa(funcionario.getChavePessoa())) == null)
            throw new NotExecuteException();
    }

    public Person getPessoa() {
        return pessoa;
    }
}