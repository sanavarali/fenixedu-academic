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
package org.fenixedu.academic.ui.struts.action.person;

import org.apache.struts.actions.ForwardAction;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

public class PersonApplication {

    @StrutsApplication(descriptionKey = "label.navheader.person", path = "personal-area", titleKey = "label.navheader.person",
            bundle = "ApplicationResources", accessGroup = "role(PERSON)", hint = "Person")
    @Mapping(path = "/index", module = "person", parameter = "/person/personMainPage.jsp")
    public static class PersonalAreaApp extends ForwardAction {

    }

    @StrutsFunctionality(app = PersonalAreaApp.class, path = "change-password", titleKey = "label.person.changePassword")
    @Mapping(path = "/changePassword", module = "person", parameter = "/person/showChangePassLink.jsp")
    public static class ShowPersonPasswordLink extends ForwardAction {

    }

}