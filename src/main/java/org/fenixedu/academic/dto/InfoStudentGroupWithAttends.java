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
package org.fenixedu.academic.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.StudentGroup;

public class InfoStudentGroupWithAttends extends InfoStudentGroup {

    @Override
    public void copyFromDomain(final StudentGroup studentGroup) {
        super.copyFromDomain(studentGroup);

        if (studentGroup != null) {
            final Collection<Attends> attends = studentGroup.getAttendsSet();
            final List<InfoFrequenta> infoAttends = new ArrayList<InfoFrequenta>(attends.size());
            setInfoAttends(infoAttends);
            for (final Attends attend : attends) {
                infoAttends.add(InfoFrequenta.newInfoFromDomain(attend));
            }
        }
    }

    public static InfoStudentGroupWithAttends newInfoFromDomain(final StudentGroup studentGroup) {
        final InfoStudentGroupWithAttends infoStudentGroup;

        if (studentGroup != null) {
            infoStudentGroup = new InfoStudentGroupWithAttends();
            infoStudentGroup.copyFromDomain(studentGroup);
        } else {
            infoStudentGroup = null;
        }

        return infoStudentGroup;
    }

}