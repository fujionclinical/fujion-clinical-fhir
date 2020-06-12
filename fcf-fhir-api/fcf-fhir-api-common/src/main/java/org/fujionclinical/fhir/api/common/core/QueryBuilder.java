/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.common.core;

import org.fujion.common.DateUtil;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.DateTimeWrapper;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IDomainObject;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.query.QueryExpressionTuple;
import org.fujionclinical.api.query.QueryOperator;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class QueryBuilder {

    private static final QueryBuilder instance = new QueryBuilder();

    public static String buildQueryString(
            Class<? extends IDomainObject> domainClass,
            List<QueryExpressionTuple> tuples) {
        return instance.build(domainClass, tuples);
    }

    private QueryBuilder() {
    }

    private String build(
            Class<? extends IDomainObject> domainClass,
            List<QueryExpressionTuple> tuples) {
        StringBuilder sb = new StringBuilder();

        for (QueryExpressionTuple tuple : tuples) {
            sb.append(sb.length() == 0 ? "" : "&");
            sb.append(ParameterMappings.getParameterName(tuple.propertyPath, tuple.operator, domainClass));
            sb.append(xlate(tuple.operator));
            String delim = "";

            for (Object operand : tuple.operands) {
                sb.append(delim);
                delim = ",";
                appendOperand(sb, operand);
            }
        }

        return sb.toString();
    }

    private void appendOperand(
            StringBuilder sb,
            Object operand) {
        if (operand instanceof Collection) {
            appendOperands(sb, (Collection<?>) operand);
        } else if (operand.getClass().isArray()) {
            appendOperands(sb, Arrays.asList((Object[]) operand));
        } else if (operand instanceof Date) {
            sb.append(DateUtil.toISODate((Date) operand));
        } else if (operand instanceof DateTimeWrapper) {
            sb.append(((DateTimeWrapper) operand).toISOString());
        } else if (operand instanceof IConceptCode) {
            IConceptCode code = (IConceptCode) operand;
            sb.append(code.getSystem()).append("|").append(code.getCode());
        } else if (operand instanceof IIdentifier) {
            IIdentifier id = (IIdentifier) operand;
            sb.append(id.getSystem()).append("|").append(id.getValue());
        } else if (operand instanceof Enum) {
            sb.append(((Enum<?>) operand).name().toLowerCase());
        } else {
            sb.append(operand);
        }
    }

    private void appendOperands(
            StringBuilder sb,
            Collection<?> operands) {
        String delim = "";

        for (Object operand : operands) {
            sb.append(delim);
            appendOperand(sb, operand);
            delim = ",";
        }
    }

    private String xlate(QueryOperator operator) {
        switch (operator) {
            case EQ:
            case SW:
                return "=";

            default:
                return "=" + operator.name().toLowerCase();
        }
    }

    private boolean allowPartialMatch(PropertyDescriptor propertyDescriptor) {
        Class<?> propertyType = CoreUtil.getPropertyType(propertyDescriptor);
        return MiscUtil.firstAssignable(propertyType, IPersonName.class) != null;
    }

}
