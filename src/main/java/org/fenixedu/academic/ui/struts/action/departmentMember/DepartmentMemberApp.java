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
package org.fenixedu.academic.ui.struts.action.departmentMember;

import org.apache.struts.actions.ForwardAction;
import org.fenixedu.academic.ui.struts.action.commons.FacesEntryPoint;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

@StrutsApplication(bundle = DepartmentMemberApp.BUNDLE, path = "department-member", titleKey = "title.department",
        hint = DepartmentMemberApp.HINT, accessGroup = DepartmentMemberApp.ACCESS_GROUP)
@Mapping(path = "/index", module = "departmentMember", parameter = "/departmentMember/index.jsp")
public class DepartmentMemberApp extends ForwardAction {

    static final String HINT = "Department Member";
    static final String BUNDLE = "DepartmentMemberResources";
    static final String ACCESS_GROUP = "role(DEPARTMENT_MEMBER)";

    @StrutsApplication(bundle = BUNDLE, path = "department", titleKey = "title.department", hint = HINT,
            accessGroup = ACCESS_GROUP)
    public static class DepartmentMemberDepartmentApp {
    }

    @StrutsApplication(bundle = "ResearcherResources", path = "messaging", titleKey = "title.unit.communication.section",
            hint = HINT, accessGroup = ACCESS_GROUP)
    public static class DepartmentMemberMessagingApp {
    }

    // Faces Entry Points

    @StrutsFunctionality(app = DepartmentMemberDepartmentApp.class, path = "teachers", titleKey = "link.departmentTeachers")
    @Mapping(path = "/viewDepartmentTeachers/listDepartmentTeachers", module = "departmentMember")
    public static class ListDepartmentTeachers extends FacesEntryPoint {
    }

    @StrutsFunctionality(app = DepartmentMemberDepartmentApp.class, path = "teacher-service", titleKey = "link.teacherService")
    @Mapping(path = "/viewTeacherService/viewTeacherService", module = "departmentMember")
    public static class ViewTeacherService extends FacesEntryPoint {
    }

}
