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
 * Created on 27/Ago/2003
 *
 */
package org.fenixedu.academic.ui.struts.action.student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.fenixedu.academic.domain.student.GroupEnrolment;
import org.fenixedu.academic.dto.InfoExportGrouping;
import org.fenixedu.academic.dto.InfoSiteStudentsWithoutGroup;
import org.fenixedu.academic.service.services.exceptions.ExistingServiceException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidArgumentsServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidChangeServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidSituationServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidStudentNumberServiceException;
import org.fenixedu.academic.service.services.exceptions.NoChangeMadeServiceException;
import org.fenixedu.academic.service.services.exceptions.NonExistingServiceException;
import org.fenixedu.academic.service.services.exceptions.NonValidChangeServiceException;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.academic.service.services.student.ReadExportGroupingsByGrouping;
import org.fenixedu.academic.service.services.student.ReadStudentsWithoutGroup;
import org.fenixedu.academic.service.services.student.ReadStudentsWithoutGroup.NewStudentGroupAlreadyExists;
import org.fenixedu.academic.service.services.student.VerifyStudentGroupAtributes;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.exceptions.FenixActionException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

/**
 * @author asnr and scpo
 * 
 */
@Mapping(module = "student", path = "/groupEnrolment", formBean = "groupEnrolmentForm",
        functionality = ViewEnroledExecutionCoursesAction.class)
@Forwards(value = { @Forward(name = "sucess", path = "/student/viewGroupEnrolment_bd.jsp"),
        @Forward(name = "insucess", path = "/student/viewEnroledExecutionCourses.do?method=prepare"),
        @Forward(name = "viewStudentGroupInformation", path = "/student/viewStudentGroupInformation.do"),
        @Forward(name = "viewShiftsAndGroups", path = "/student/viewShiftsAndGroups.do"),
        @Forward(name = "viewExecutionCourseProjects", path = "/student/viewExecutionCourseProjects.do") })
public class GroupEnrolmentDispatchAction extends FenixDispatchAction {

    public ActionForward prepareEnrolment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException, FenixServiceException {

        User userView = getUserView(request);

        String groupPropertiesCodeString = request.getParameter("groupPropertiesCode");

        String shiftCodeString = request.getParameter("shiftCode");

        try {

            VerifyStudentGroupAtributes.run(groupPropertiesCodeString, shiftCodeString, null, userView.getUsername(),
                    new Integer(2));

        } catch (NotAuthorizedException e) {
            ActionErrors actionErrors2 = new ActionErrors();
            ActionError error2 = null;
            error2 = new ActionError("errors.noStudentInAttendsSet");
            actionErrors2.add("errors.noStudentInAttendsSet", error2);
            saveErrors(request, actionErrors2);
            return mapping.findForward("insucess");
        } catch (InvalidChangeServiceException e) {
            ActionErrors actionErrors2 = new ActionErrors();
            ActionError error2 = null;
            error2 = new ActionError("error.noProject");
            actionErrors2.add("error.noProject", error2);
            saveErrors(request, actionErrors2);
            return mapping.findForward("viewExecutionCourseProjects");
        } catch (InvalidArgumentsServiceException e) {
            ActionErrors actionErrors2 = new ActionErrors();
            ActionError error2 = null;
            error2 = new ActionError("errors.impossible.nrOfGroups.groupEnrolment");
            actionErrors2.add("errors.impossible.nrOfGroups.groupEnrolment", error2);
            saveErrors(request, actionErrors2);
            return mapping.findForward("viewShiftsAndGroups");
        } catch (InvalidSituationServiceException e) {
            ActionErrors actionErrors2 = new ActionErrors();
            ActionError error2 = null;
            error2 = new ActionError("errors.existing.groupStudentEnrolment");
            actionErrors2.add("errors.existing.groupStudentEnrolment", error2);
            saveErrors(request, actionErrors2);
            return mapping.findForward("viewShiftsAndGroups");

        } catch (FenixServiceException e) {
            throw new FenixActionException(e);

        }

        InfoSiteStudentsWithoutGroup studentsNotEnroled = null;

        try {
            studentsNotEnroled = ReadStudentsWithoutGroup.run(groupPropertiesCodeString, userView.getUsername());

        } catch (ExistingServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("error.noProject");
            actionErrors1.add("error.noProject", error1);
            saveErrors(request, actionErrors1);
            return mapping.findForward("viewExecutionCourseProjects");
        } catch (NewStudentGroupAlreadyExists e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("error.existingGroup");
            actionErrors1.add("error.existingGroup", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);
        } catch (FenixServiceException e) {
            throw new FenixActionException(e);
        }

        List infoStudentList = studentsNotEnroled.getInfoStudentList();
        if (infoStudentList != null) {
            Collections.sort(infoStudentList, new BeanComparator("number"));
            request.setAttribute("infoStudents", infoStudentList);
        }
        request.setAttribute("groupNumber", studentsNotEnroled.getGroupNumber());
        request.setAttribute("groupPropertiesCode", groupPropertiesCodeString);
        request.setAttribute("shiftCode", shiftCodeString);
        request.setAttribute("infoUserStudent", studentsNotEnroled.getInfoUserStudent());
        request.setAttribute("infoGrouping", studentsNotEnroled.getInfoGrouping());

        List<InfoExportGrouping> infoExportGroupings = ReadExportGroupingsByGrouping.run(groupPropertiesCodeString);
        request.setAttribute("infoExportGroupings", infoExportGroupings);

        return mapping.findForward("sucess");

    }

    public ActionForward enrolment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException, FenixServiceException {

        DynaActionForm enrolmentForm = (DynaActionForm) form;

        User userView = getUserView(request);

        String groupPropertiesCodeString = request.getParameter("groupPropertiesCode");

        String groupNumberString = request.getParameter("groupNumber");
        Integer groupNumber = new Integer(groupNumberString);
        String shiftCodeString = request.getParameter("shiftCode");

        List<String> studentUsernames = Arrays.asList((String[]) enrolmentForm.get("studentsNotEnroled"));

        try {
            GroupEnrolment.run(groupPropertiesCodeString, shiftCodeString, groupNumber, studentUsernames, userView.getUsername());

        } catch (NonExistingServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("error.noProject");
            actionErrors1.add("error.noProject", error1);
            saveErrors(request, actionErrors1);
            return mapping.findForward("viewExecutionCourseProjects");
        } catch (NoChangeMadeServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.noStudentInAttendsSet");
            actionErrors1.add("errors.noStudentInAttendsSet", error1);
            saveErrors(request, actionErrors1);
            return mapping.findForward("insucess");
        } catch (InvalidStudentNumberServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.noStudentsInAttendsSet");
            actionErrors1.add("errors.noStudentsInAttendsSet", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);
        } catch (InvalidArgumentsServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.impossible.nrOfGroups.groupEnrolment");
            actionErrors1.add("errors.impossible.nrOfGroups.groupEnrolment", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);
        } catch (NonValidChangeServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.impossible.minimumCapacity.groupEnrolment");
            actionErrors1.add("errors.impossible.minimumCapacity.groupEnrolment", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);
        } catch (NotAuthorizedException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.impossible.maximumCapacity.groupEnrolment");
            actionErrors1.add("errors.impossible.maximumCapacity.groupEnrolment", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);

        } catch (ExistingServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.existing.elementsEnrolment");
            actionErrors1.add("errors.existing.elementsEnrolment", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);

        } catch (InvalidSituationServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("errors.existing.groupStudentEnrolment");
            actionErrors1.add("errors.existing.groupStudentEnrolment", error1);
            saveErrors(request, actionErrors1);
            return mapping.findForward("viewShiftsAndGroups");

        } catch (FenixServiceException e) {
            ActionErrors actionErrors1 = new ActionErrors();
            ActionError error1 = null;
            error1 = new ActionError("error.existingGroup");
            actionErrors1.add("error.existingGroup", error1);
            saveErrors(request, actionErrors1);
            return prepareEnrolment(mapping, form, request, response);

        }

        request.setAttribute("groupPropertiesCode", groupPropertiesCodeString);
        request.setAttribute("shiftCode", shiftCodeString);

        return mapping.findForward("viewShiftsAndGroups");

    }
}