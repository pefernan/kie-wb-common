/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client.view.glyph;

import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.glyph.AbstractLienzoShapeGlyph;

public final class ConnectorGlyph extends AbstractLienzoShapeGlyph {

    public ConnectorGlyph(final String color,
                          final double width,
                          final double height) {
        super(new Group(),
              width,
              height);
        build(width,
              height,
              color);
    }

    private void build(final double width,
                       final double height,
                       final String color) {
        group.removeAll();
        final Arrow arrow = new Arrow(new Point2D(0,
                                                  height),
                                      new Point2D(width,
                                                  0),
                                      5,
                                      10,
                                      45,
                                      45,
                                      ArrowType.AT_END)
                .setStrokeWidth(5).setStrokeColor(color).setDraggable(true);
        group.add(arrow);
        scaleTo(group,
                width - arrow.getStrokeWidth(),
                height - arrow.getStrokeWidth());
    }

    private void scaleTo(final Group group,
                         final double width,
                         final double height) {
        final BoundingBox bb = group.getBoundingBox();
        final double w = bb.getWidth();
        final double h = bb.getHeight();
        final double sw = w > 0 ? (width / w) : 1;
        final double sh = h > 0 ? (height / h) : 1;
        group.setScale(sw,
                       sh);
    }
}
