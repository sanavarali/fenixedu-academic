/*
 * Created on 29/Jun/2004
 *
 */
package DataBeans;

import Dominio.IExecutionDegree;

/**
 * @author T�nia Pous�o
 *  
 */
public class InfoExecutionDegreeWithInfoExecutionYearAndDegreeCurricularPlanAndInfoCampus extends
        InfoExecutionDegreeWithInfoExecutionYearAndDegreeCurricularPlan {

    /*
     * (non-Javadoc)
     * 
     * @see DataBeans.InfoExecutionDegree#copyFromDomain(Dominio.IExecutionDegree)
     */
    public void copyFromDomain(IExecutionDegree executionDegree) {
        super.copyFromDomain(executionDegree);
        if (executionDegree != null) {
            setInfoCampus(InfoCampus.newInfoFromDomain(executionDegree.getCampus()));
        }
    }

    public static InfoExecutionDegree newInfoFromDomain(IExecutionDegree executionDegree) {
        InfoExecutionDegreeWithInfoExecutionYearAndDegreeCurricularPlanAndInfoCampus infoExecutionDegree = null;
        if (executionDegree != null) {
            infoExecutionDegree = new InfoExecutionDegreeWithInfoExecutionYearAndDegreeCurricularPlanAndInfoCampus();
            infoExecutionDegree.copyFromDomain(executionDegree);
        }
        return infoExecutionDegree;
    }
}