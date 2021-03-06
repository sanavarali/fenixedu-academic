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
package org.fenixedu.academic.ui.struts.action.publico;

import java.io.DataOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.File;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.struts.action.base.FenixAction;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenixframework.FenixFramework;

@Mapping(module = "publico", path = "/files")
public class FileDownload extends FenixAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm actionForm, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final String oid = request.getParameter("oid");
        final File file = FenixFramework.getDomainObject(oid);
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST));
            response.getWriter().close();
        } else {
            final Person person = AccessControl.getPerson();
            if (!file.isPrivate() || file.isPersonAllowedToAccess(person)) {
                response.setContentType(file.getContentType());
                response.addHeader("Content-Disposition", "attachment; filename=" + file.getFilename());
                response.setContentLength(file.getSize().intValue());
                final DataOutputStream dos = new DataOutputStream(response.getOutputStream());
                dos.write(file.getContents());
                dos.close();
            } else if (file.isPrivate() && person == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(HttpStatus.getStatusText(HttpStatus.SC_UNAUTHORIZED));
                response.getWriter().close();
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(HttpStatus.getStatusText(HttpStatus.SC_FORBIDDEN));
                response.getWriter().close();
            }
        }
        return null;
    }

}
