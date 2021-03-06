/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 19/Dez/2003
 *  
 */
package org.fenixedu.academic.ui.struts.action.publico.teachersBody;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.fenixedu.academic.dto.InfoDepartment;
import org.fenixedu.academic.dto.InfoExecutionDegree;
import org.fenixedu.academic.dto.InfoExecutionYear;
import org.fenixedu.academic.dto.comparators.ComparatorByNameForInfoExecutionDegree;
import org.fenixedu.academic.dto.teacher.professorship.DetailedProfessorship;
import org.fenixedu.academic.service.services.commons.ReadExecutionDegreeByOID;
import org.fenixedu.academic.service.services.commons.ReadNotClosedExecutionYears;
import org.fenixedu.academic.service.services.department.ReadAllDepartments;
import org.fenixedu.academic.service.services.department.ReadDepartmentByOID;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.publico.teachersBody.ReadProfessorshipsAndResponsibilitiesByDepartmentAndExecutionPeriod;
import org.fenixedu.academic.service.services.publico.teachersBody.ReadProfessorshipsAndResponsibilitiesByExecutionDegreeAndExecutionPeriod;
import org.fenixedu.academic.service.services.resourceAllocationManager.ReadExecutionDegreesByExecutionYearId;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.exceptions.FenixActionException;
import org.fenixedu.academic.ui.struts.action.publico.PublicApplication;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

/**
 * @author <a href="mailto:joao.mota@ist.utl.pt">Jo�o Mota </a> 19/Dez/2003
 * 
 */
@StrutsFunctionality(app = PublicApplication.class, path = "show-teachers-body", titleKey = "title.teachers.body")
@Mapping(module = "publico", path = "/searchProfessorships", formBean = "teachersBodyForm")
@Forwards({ @Forward(name = "showForm", path = "/publico/services/teachers/searchProfessorships.jsp"),
        @Forward(name = "showProfessorships", path = "/publico/services/teachers/showProfessorships.jsp") })
public class ShowTeachersBodyDispatchAction extends FenixDispatchAction {

    private String makeBodyHeader(String executionYear, Integer semester, Integer teacherType) {

        String sem = semester.intValue() == 0 ? "Ambos Semestres" : (semester.intValue() + "&ordm; Semestre");
        String teacher = teacherType.intValue() == 0 ? "Todos os Docentes" : "Apenas Respons&aacute;veis";
        String header = "Ano Lectivo " + executionYear + " - " + sem + " - " + teacher;

        return header;

    }

    @EntryPoint
    public ActionForward prepareForm(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException {

        DynaActionForm executionYearForm = (DynaActionForm) actionForm;
        String executionYearId = (String) executionYearForm.get("executionYearId");
        Integer semester = (Integer) executionYearForm.get("semester");
        Integer teacherType = (Integer) executionYearForm.get("teacherType");

        try {

            List executionDegrees = ReadExecutionDegreesByExecutionYearId.run(executionYearId);
            List executionYears = ReadNotClosedExecutionYears.run();
            List departments = ReadAllDepartments.run();

            if (executionDegrees != null && executionDegrees.size() > 0) {
                // put execution year in the form
                if (StringUtils.isEmpty(executionYearId)) {
                    executionYearId =
                            ((InfoExecutionDegree) executionDegrees.iterator().next()).getInfoExecutionYear().getExternalId();

                    executionYearForm.set("executionYearId", executionYearId);
                }
                // initialize semester
                if (semester == null) {
                    semester = new Integer(0);
                }
                // initialize teacherType
                if (teacherType == null) {
                    teacherType = new Integer(0);
                }

                Collections.sort(executionDegrees, new ComparatorByNameForInfoExecutionDegree());
            }

            Iterator iter = executionYears.iterator();
            while (iter.hasNext()) {
                InfoExecutionYear year = (InfoExecutionYear) iter.next();
                if (year.getExternalId().equals(executionYearId)) {
                    request.setAttribute("searchDetails", makeBodyHeader(year.getYear(), semester, teacherType));
                    break;
                }
            }

            if (semester != null) {
                request.setAttribute("semester", semester);
            }
            if (teacherType != null) {
                request.setAttribute("teacherType", teacherType);
            }

            request.setAttribute("executionYearId", executionYearId);
            request.setAttribute("executionDegrees", executionDegrees);
            request.setAttribute("executionYears", executionYears);
            request.setAttribute("departments", departments);

        } catch (FenixServiceException e) {
            throw new FenixActionException(e);
        }

        return mapping.findForward("showForm");
    }

    public ActionForward showProfessorshipsByExecutionDegree(ActionMapping mapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response) throws FenixActionException {

        DynaActionForm executionDegreeForm = (DynaActionForm) actionForm;
        String executionDegreeId = (String) executionDegreeForm.get("executionDegreeId");

        Integer semester = (Integer) executionDegreeForm.get("semester");
        Integer teacherType = (Integer) executionDegreeForm.get("teacherType");
        String searchDetails = (String) executionDegreeForm.get("searchDetails");
        try {

            List detailedProfessorShipsListofLists =
                    ReadProfessorshipsAndResponsibilitiesByExecutionDegreeAndExecutionPeriod.run(executionDegreeId, semester,
                            teacherType);

            if ((detailedProfessorShipsListofLists != null) && (!detailedProfessorShipsListofLists.isEmpty())) {

                Collections.sort(detailedProfessorShipsListofLists, new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {

                        List list1 = (List) o1;
                        List list2 = (List) o2;
                        DetailedProfessorship dt1 = (DetailedProfessorship) list1.iterator().next();
                        DetailedProfessorship dt2 = (DetailedProfessorship) list2.iterator().next();

                        return Collator.getInstance().compare(dt1.getInfoProfessorship().getInfoExecutionCourse().getNome(),
                                dt2.getInfoProfessorship().getInfoExecutionCourse().getNome());
                    }

                });

                request.setAttribute("detailedProfessorShipsListofLists", detailedProfessorShipsListofLists);
            }

            InfoExecutionDegree degree = ReadExecutionDegreeByOID.run(executionDegreeId);

            request.setAttribute("searchType", "Consulta Por Curso");
            request.setAttribute("searchTarget", degree.getInfoDegreeCurricularPlan().getInfoDegree().getDegreeType() + " em "
                    + degree.getInfoDegreeCurricularPlan().getInfoDegree().getNome());
            request.setAttribute("searchDetails", searchDetails);
            request.setAttribute("semester", semester);
            request.setAttribute("teacherType", teacherType);
            request.setAttribute("executionDegree", degree);

        } catch (FenixServiceException e) {
            throw new FenixActionException(e);
        }

        return mapping.findForward("showProfessorships");
    }

    public ActionForward showTeachersBodyByDepartment(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException {

        DynaActionForm departmentForm = (DynaActionForm) actionForm;
        String departmentId = (String) departmentForm.get("departmentId");
        String executionYearId = (String) departmentForm.get("executionYearId");

        Integer semester = (Integer) departmentForm.get("semester");
        Integer teacherType = (Integer) departmentForm.get("teacherType");
        String searchDetails = (String) departmentForm.get("searchDetails");

        try {

            List detailedProfessorShipsListofLists =
                    ReadProfessorshipsAndResponsibilitiesByDepartmentAndExecutionPeriod.run(departmentId, executionYearId,
                            semester, teacherType);

            if ((detailedProfessorShipsListofLists != null) && (!detailedProfessorShipsListofLists.isEmpty())) {

                Collections.sort(detailedProfessorShipsListofLists, new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {

                        List list1 = (List) o1;
                        List list2 = (List) o2;
                        DetailedProfessorship dt1 = (DetailedProfessorship) list1.iterator().next();
                        DetailedProfessorship dt2 = (DetailedProfessorship) list2.iterator().next();

                        return Collator.getInstance().compare(dt1.getInfoProfessorship().getInfoExecutionCourse().getNome(),
                                dt2.getInfoProfessorship().getInfoExecutionCourse().getNome());
                    }

                });
                request.setAttribute("detailedProfessorShipsListofLists", detailedProfessorShipsListofLists);
            }

            InfoDepartment department = ReadDepartmentByOID.run(departmentId);

            request.setAttribute("searchType", "Consulta Por Departmento");
            request.setAttribute("searchTarget", department.getName());
            request.setAttribute("searchDetails", searchDetails);
            request.setAttribute("semester", semester);
            request.setAttribute("teacherType", teacherType);
        } catch (FenixServiceException e) {
            throw new FenixActionException(e);
        }

        return mapping.findForward("showProfessorships");
    }

}