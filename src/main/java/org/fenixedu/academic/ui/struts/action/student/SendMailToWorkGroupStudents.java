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
/**
 * @author Sérgio Silva ist152416
 */

package org.fenixedu.academic.ui.struts.action.student;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Grouping;
import org.fenixedu.academic.domain.StudentGroup;
import org.fenixedu.academic.domain.accessControl.GroupingGroup;
import org.fenixedu.academic.domain.accessControl.StudentGroupGroup;
import org.fenixedu.academic.domain.util.email.ExecutionCourseSender;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.domain.util.email.Sender;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.messaging.EmailsDA;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenixframework.FenixFramework;

@Mapping(module = "teacher", path = "/sendMailToWorkGroupStudents", functionality = ManageExecutionCourseDA.class)
public class SendMailToWorkGroupStudents extends FenixDispatchAction {

    public ActionForward sendEmail(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String executionCourseCode = request.getParameter("executionCourseID");
        String studentGroupCode = request.getParameter("studentGroupCode");
        ExecutionCourse executionCourse = FenixFramework.getDomainObject(executionCourseCode);
        StudentGroup studentGroup = FenixFramework.getDomainObject(studentGroupCode);
        Group groupToSend = StudentGroupGroup.get(studentGroup);
        Sender sender = ExecutionCourseSender.newInstance(executionCourse);
        final String label =
                getResources(request, "APPLICATION_RESOURCES").getMessage("label.students.group.send.email",
                        studentGroup.getGroupNumber(), studentGroup.getGrouping().getName());
        Recipient recipient = Recipient.newInstance(label, groupToSend);
        return EmailsDA.sendEmail(request, sender, recipient);
    }

    public ActionForward sendGroupingEmail(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String executionCourseCode = request.getParameter("executionCourseID");
        String groupingCode = request.getParameter("groupingCode");
        ExecutionCourse executionCourse = FenixFramework.getDomainObject(executionCourseCode);
        Grouping grouping = FenixFramework.getDomainObject(groupingCode);
        Group groupToSend = GroupingGroup.get(grouping);
        Sender sender = ExecutionCourseSender.newInstance(executionCourse);
        final String label =
                getResources(request, "APPLICATION_RESOURCES").getMessage("label.students.grouping.send.email",
                        grouping.getName());
        Recipient recipient = Recipient.newInstance(label, groupToSend);
        return EmailsDA.sendEmail(request, sender, recipient);
    }
}
