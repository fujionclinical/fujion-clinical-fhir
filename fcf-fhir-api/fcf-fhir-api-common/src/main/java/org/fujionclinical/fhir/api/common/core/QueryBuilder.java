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
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.query.QueryExpressionTuple;
import org.fujionclinical.api.query.QueryOperator;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder {

    private final Map<String, String> queryMap = new HashMap<>();

    private static QueryBuilder instance = new QueryBuilder();

    private QueryBuilder() {
        queryMap.put("id", "_id");
        queryMap.put("identifiers", "identifier");
    }

    public static String buildQueryString(
        List<QueryExpressionTuple> tuples,
        Map<String, String> map) {
        return instance.build(tuples, map);
    }

    public String build(
            List<QueryExpressionTuple> tuples,
            Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        for (QueryExpressionTuple tuple : tuples) {
            sb.append(sb.length() == 0 ? "" : "&");
            sb.append(xlate(map, tuple.propertyDescriptor.getName()));
            sb.append(xlate(tuple.operator, tuple.propertyDescriptor));
            String delim = "";

            for (Object operand : tuple.operands) {
                sb.append(delim);
                delim = ",";

                if (operand instanceof Date) {
                    sb.append(DateUtil.toISO((Date) operand));
                } else if (operand instanceof IConceptCode) {
                    IConceptCode code = (IConceptCode) operand;
                    sb.append(code.getSystem()).append("|").append(code.getCode());
                } else {
                    sb.append(operand);
                }
            }
        }

        return sb.toString();
    }

    private String xlate(
            Map<String, String> map,
            String value) {
        return map != null && map.containsKey(value) ? map.get(value) : queryMap.containsKey(value) ? queryMap.get(value) : value;
    }

    private String xlate(QueryOperator operator, PropertyDescriptor propertyDescriptor) {
        boolean allowPartialMatch = allowPartialMatch(propertyDescriptor);

        switch (operator) {
            case EQ:
                return allowPartialMatch ? ":exact=" : "=";

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
