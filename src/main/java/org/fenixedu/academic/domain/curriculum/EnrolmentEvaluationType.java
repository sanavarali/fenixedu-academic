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
package org.fenixedu.academic.domain.curriculum;

import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * @author dcs-rjao
 * 
 *         2/Abr/2003
 */
public enum EnrolmentEvaluationType {

    NORMAL,

    IMPROVEMENT,

    SPECIAL_SEASON,

    EQUIVALENCE;

    public String getName() {
        return name();
    }

    public String getQualifiedName() {
        return EnrolmentEvaluationType.class.getSimpleName() + "." + name();
    }

    public String getAcronym() {
        return getQualifiedName() + ".acronym";
    }

    public String getDescription() {
        return BundleUtil.getString(Bundle.ENUMERATION, getQualifiedName());
    }

    public String getNumericDescription() {
        return BundleUtil.getString(Bundle.REPORTS, name());
    }

}
