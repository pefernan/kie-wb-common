/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.migration.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.DOMParser;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;
import org.kie.workbench.common.forms.migration.model.legacy.data.DataHolder;
import org.kie.workbench.common.forms.migration.model.legacy.data.Type;
import org.kie.workbench.common.forms.migration.service.LegacyFormReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@ApplicationScoped
public class LegacyFormReaderImpl implements LegacyFormReader {

    public static final String NODE_FORM = "form";
    public static final String NODE_FIELD = "field";
    public static final String NODE_PROPERTY = "property";
    public static final String NODE_DATA_HOLDER = "dataHolder";

    public static final String ATTR_ID = "id";
    public static final String ATTR_INPUT_ID = "inputId";
    public static final String ATTR_OUT_ID = "outId";
    public static final String ATTR_POSITION = "position";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_BAG_TYPE = "bag-type";
    public static final String ATTR_SUPPORTED_TYPE = "supportedType";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";

    protected Logger log = LoggerFactory.getLogger(LegacyFormReaderImpl.class);

    @Override
    public LegacyForm loadFormFromXML(String xml) throws Exception {
        if (StringUtils.isBlank(xml)) {
            return null;
        }

        InputSource source = new InputSource(new StringReader(xml));

        DOMParser parser = new DOMParser();
        parser.parse(source);
        Document doc = parser.getDocument();
        NodeList nodes = doc.getElementsByTagName(NODE_FORM);
        Node nodeForm = nodes.item(0);

        if (!nodeForm.getNodeName().equals(NODE_FORM)) {
            throw new IllegalStateException("Cannot find '" + NODE_FORM + "' on xml");
        }

        LegacyForm legacyForm = new LegacyForm();

        legacyForm.setId(Long.valueOf(StringEscapeUtils.unescapeXml(nodeForm.getAttributes().getNamedItem(ATTR_ID).getNodeValue())));

        List<LegacyField> fields = new ArrayList<>();
        NodeList childNodes = nodeForm.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(NODE_PROPERTY)) {
                String propName = node.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                String value = StringEscapeUtils.unescapeXml(node.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());
                if ("name".equals(propName)) {
                    legacyForm.setName(value);
                } else if ("labelMode".equals(propName)) {
                    legacyForm.setLabelMode(value);
                }
            } else if (node.getNodeName().equals(NODE_FIELD)) {
                LegacyField field = deserializeField(node);
                fields.add(field);
            } else if (node.getNodeName().equals(NODE_DATA_HOLDER)) {
                String holderId = getNodeAttributeValue(node,
                                                        ATTR_ID);
                String holderInputId = getNodeAttributeValue(node,
                                                             ATTR_INPUT_ID);
                String holderOutId = getNodeAttributeValue(node,
                                                           ATTR_OUT_ID);
                String holderType = getNodeAttributeValue(node,
                                                          ATTR_TYPE);
                String holderValue = getNodeAttributeValue(node,
                                                           ATTR_VALUE);

                if (!StringUtils.isEmpty(holderId) && !StringUtils.isEmpty(holderType) && !StringUtils.isEmpty(holderValue)) {

                    DataHolder holder = new DataHolder(holderId,
                                                       holderValue,
                                                       Type.resolveType(holderType));

                    holder.setInput(holderInputId);
                    holder.setOutput(holderOutId);

                    legacyForm.addDataHolder(holder);
                }
            }
        }
        if (fields != null) {
            legacyForm.getFormFields().addAll(fields);
        }
        return legacyForm;
    }

    protected String getNodeAttributeValue(Node node,
                                           String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        return attribute != null ? attribute.getNodeValue() : "";
    }

    public LegacyField deserializeField(Node nodeField) throws Exception {
        if (!nodeField.getNodeName().equals(NODE_FIELD)) {
            return null;
        }

        LegacyField field = new LegacyField();
        field.setId(Long.valueOf(nodeField.getAttributes().getNamedItem(ATTR_ID).getNodeValue()));
        field.setFieldName(nodeField.getAttributes().getNamedItem(ATTR_NAME).getNodeValue());
        field.setPosition(Integer.parseInt(nodeField.getAttributes().getNamedItem(ATTR_POSITION).getNodeValue()));
        field.setFieldType(nodeField.getAttributes().getNamedItem(ATTR_TYPE).getNodeValue());

        Node bag = nodeField.getAttributes().getNamedItem(ATTR_BAG_TYPE);

        if (bag != null) {
            field.setBag(bag.getNodeValue());
        }

        NodeList fieldPropsNodes = nodeField.getChildNodes();
        for (int j = 0; j < fieldPropsNodes.getLength(); j++) {
            Node nodeFieldProp = fieldPropsNodes.item(j);
            if (nodeFieldProp.getNodeName().equals(NODE_PROPERTY)) {
                String propName = nodeFieldProp.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                String value = StringEscapeUtils.unescapeXml(nodeFieldProp.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());
                if (propName != null && value != null) {
                    if ("fieldRequired".equals(propName)) {
                        field.setFieldRequired(Boolean.valueOf(value));
                    } else if ("groupWithPrevious".equals(propName)) {
                        field.setGroupWithPrevious(Boolean.valueOf(value));
                    } else if ("height".equals(propName)) {
                        field.setHeight(value);
                    } else if ("labelCSSClass".equals(propName)) {
                        field.setLabelCSSClass(value);
                    } else if ("labelCSSStyle".equals(propName)) {
                        field.setLabelCSSStyle(value);
                    } else if ("label".equals(propName)) {
                        field.setLabel(deserializeI18nEntrySet(value));
                    } else if ("errorMessage".equals(propName)) {
                        field.setErrorMessage(deserializeI18nEntrySet(value));
                    } else if ("title".equals(propName)) {
                        field.setTitle(deserializeI18nEntrySet(value));
                    } else if ("readonly".equals(propName)) {
                        field.setReadonly(Boolean.valueOf(value));
                    } else if ("size".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setSize(Long.valueOf(value));
                        }
                    } else if ("formula".equals(propName)) {
                        field.setFormula(value);
                    } else if ("rangeFormula".equals(propName)) {
                        field.setRangeFormula(value);
                    } else if ("pattern".equals(propName)) {
                        field.setPattern(value);
                    } else if ("maxlength".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setMaxlength(Long.valueOf(value));
                        }
                    } else if ("styleclass".equals(propName)) {
                        field.setStyleclass(value);
                    } else if ("cssStyle".equals(propName)) {
                        field.setCssStyle(value);
                    } else if ("tabindex".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setTabindex(Long.valueOf(value));
                        }
                    } else if ("accesskey".equals(propName)) {
                        field.setAccesskey(value);
                    } else if ("isHTML".equals(propName)) {
                        field.setIsHTML(Boolean.valueOf(value));
                    } else if ("htmlContent".equals(propName)) {
                        field.setHtmlContent(deserializeI18nEntrySet(value));
                    } else if ("hideContent".equals(propName)) {
                        field.setHideContent(Boolean.valueOf(value));
                    } else if ("defaultValueFormula".equals(propName)) {
                        field.setDefaultValueFormula(value);
                    } else if ("defaultSubform".equals(propName)) {
                        field.setDefaultSubform(value);
                    } else if ("previewSubform".equals(propName)) {
                        field.setPreviewSubform(value);
                    } else if ("tableSubform".equals(propName)) {
                        field.setTableSubform(value);
                    } else if ("newItemText".equals(propName)) {
                        field.setNewItemText(deserializeI18nEntrySet(value));
                    } else if ("addItemText".equals(propName)) {
                        field.setAddItemText(deserializeI18nEntrySet(value));
                    } else if ("cancelItemText".equals(propName)) {
                        field.setCancelItemText(deserializeI18nEntrySet(value));
                    } else if ("deleteItems".equals(propName)) {
                        field.setDeleteItems(Boolean.valueOf(value));
                    } else if ("updateItems".equals(propName)) {
                        field.setUpdateItems(Boolean.valueOf(value));
                    } else if ("visualizeItems".equals(propName)) {
                        field.setVisualizeItem(Boolean.valueOf(value));
                    } else if ("hideCreateItem".equals(propName)) {
                        field.setHideCreateItem(Boolean.valueOf(value));
                    } else if ("inputBinding".equals(propName)) {
                        field.setInputBinding(value);
                    } else if ("outputBinding".equals(propName)) {
                        field.setOutputBinding(value);
                    }
                }
            }
        }

        return field;
    }

    protected String[] decodeStringArray(String textValue) {
        if (textValue == null || textValue.trim().length() == 0) {
            return new String[0];
        }
        String[] lista;
        lista = textValue.split("quot;");
        return lista;
    }

    public Map<String, String> deserializeI18nEntrySet(String cadena) {
        String[] values = decodeStringArray(cadena);
        Map<String, String> mapValues = new HashMap();
        for (int i = 0; i < values.length; i = i + 4) {
            String key = values[i + 1];
            String value = "";
            if (i + 3 < values.length) {
                value = values[i + 3];
            }
            if (key.length() == 2 && !StringUtils.isEmpty(value)) {
                mapValues.put(key,
                              value);
            }
        }
        return mapValues;
    }
}
