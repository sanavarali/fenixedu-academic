/*
 * FrequentaOJB.java
 * 
 * Created on 20 de Outubro de 2002, 15:36
 */

package ServidorPersistente.OJB;

/**
 * @author tfc130
 */
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.odmg.HasBroker;

import Dominio.Attends;
import Dominio.IEnrollment;
import Dominio.IExecutionCourse;
import Dominio.IAttends;
import Dominio.IStudent;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IFrequentaPersistente;
import Util.PeriodState;
import Util.TipoCurso;

public class FrequentaOJB extends PersistentObjectOJB implements IFrequentaPersistente {

    public List readByUsername(String username) throws ExcepcaoPersistencia {
        Criteria crit = new Criteria();
        crit.addEqualTo("aluno.person.username", username);
        return queryList(Attends.class, crit);
    }

    public IAttends readByAlunoAndDisciplinaExecucao(IStudent aluno,
            IExecutionCourse disciplinaExecucao) throws ExcepcaoPersistencia {

        Criteria crit = new Criteria();
        crit.addEqualTo("aluno.idInternal", aluno.getIdInternal());
        crit.addEqualTo("chaveDisciplinaExecucao", disciplinaExecucao.getIdInternal());
        return (IAttends) queryObject(Attends.class, crit);

    }

    //by gedl AT rnl DOT IST DOT UTL DOT PT , september the 16th, 2003
    public IAttends readByAlunoIdAndDisciplinaExecucaoId(Integer alunoId, Integer disciplinaExecucaoId)
            throws ExcepcaoPersistencia {

        Criteria crit = new Criteria();
        crit.addEqualTo("chaveAluno", alunoId);
        crit.addEqualTo("chaveDisciplinaExecucao", disciplinaExecucaoId);
        return (IAttends) queryObject(Attends.class, crit);

    }

    public void delete(IAttends frequenta) throws ExcepcaoPersistencia {
        super.delete(frequenta);
    }

    public List readByStudentNumber(Integer id, TipoCurso tipoCurso) throws ExcepcaoPersistencia {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("aluno.number", id);
        criteria.addEqualTo("aluno.degreeType", tipoCurso);
        return queryList(Attends.class, criteria);
    }

    public List readByStudentNumberInCurrentExecutionPeriod(Integer number) throws ExcepcaoPersistencia {
        Criteria crit = new Criteria();
        crit.addEqualTo("aluno.number", number);
        crit.addEqualTo("disciplinaExecucao.executionPeriod.state", PeriodState.CURRENT);

        return queryList(Attends.class, crit);
    }

    public List readByExecutionCourse(IExecutionCourse executionCourse) throws ExcepcaoPersistencia {
        Criteria crit = new Criteria();
        crit.addEqualTo("disciplinaExecucao.idInternal", executionCourse.getIdInternal());
        return queryList(Attends.class, crit);
    }

    public Integer countStudentsAttendingExecutionCourse(IExecutionCourse executionCourse)
            throws ExcepcaoPersistencia {
        PersistenceBroker pb = ((HasBroker) odmg.currentTransaction()).getBroker();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("disciplinaExecucao.idInternal", executionCourse.getIdInternal());
        Query queryCriteria = new QueryByCriteria(Attends.class, criteria);
        return new Integer(pb.getCount(queryCriteria));
    }

    public IAttends readByEnrolment(IEnrollment enrolment) throws ExcepcaoPersistencia {
        Criteria crit = new Criteria();
        crit.addEqualTo("enrolment.idInternal", enrolment.getIdInternal());
        return (IAttends) queryObject(Attends.class, crit);
    }
}