/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.tools.visualvm.truffle.heapwalker.r;

import com.sun.tools.visualvm.truffle.heapwalker.DynamicObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.SortOrder;
import org.netbeans.lib.profiler.heap.FieldValue;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.netbeans.modules.profiler.api.icons.ProfilerIcons;
import org.netbeans.modules.profiler.heapwalker.v2.HeapContext;
import org.netbeans.modules.profiler.heapwalker.v2.model.DataType;
import org.netbeans.modules.profiler.heapwalker.v2.model.HeapWalkerNode;
import org.netbeans.modules.profiler.heapwalker.v2.model.HeapWalkerNodeFilter;
import org.netbeans.modules.profiler.heapwalker.v2.model.Progress;
import org.netbeans.modules.profiler.heapwalker.v2.model.RootNode;
import org.netbeans.modules.profiler.heapwalker.v2.model.TextNode;
import org.netbeans.modules.profiler.heapwalker.v2.ui.HeapViewPlugin;
import org.netbeans.modules.profiler.heapwalker.v2.ui.HeapWalkerActions;
import org.netbeans.modules.profiler.heapwalker.v2.ui.TreeTableView;
import org.netbeans.modules.profiler.heapwalker.v2.ui.TreeTableViewColumn;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Hurka
 */
public class RAttributesPlugin extends HeapViewPlugin {

    private final Heap heap;
    private RObject selected;

    private final TreeTableView objectsView;

    RAttributesPlugin(HeapContext context, HeapWalkerActions actions) {
        super("Attributes", "Attributes", Icons.getIcon(ProfilerIcons.NODE_FORWARD));

        heap = context.getFragment().getHeap();

        objectsView = new TreeTableView("r_objects_attributes", context, actions, TreeTableViewColumn.instancesMinimal(heap, false)) {
            protected HeapWalkerNode[] computeData(RootNode root, Heap heap, String viewID, HeapWalkerNodeFilter viewFilter, List<DataType> dataTypes, List<SortOrder> sortOrders, Progress progress) {
                if (selected != null) {
                    List<FieldValue> fields = null;
                    
                    DynamicObject attributes = selected.getAttributes();
                    if (attributes != null) {
                        fields = new ArrayList(attributes.getFieldValues());
                        fields.addAll(attributes.getStaticFieldValues());
                    }

                    HeapWalkerNode[] nodes = getNodes(fields, root, heap, viewID, viewFilter, dataTypes, sortOrders, progress);
                    return nodes == null || nodes.length == 0 ? new HeapWalkerNode[] { new TextNode("<no attributes>") } : nodes;
                }
                
                return new HeapWalkerNode[] { new TextNode("<no object selected>") };
            }
        };
    }

    protected JComponent createComponent() {
        return objectsView.getComponent();
    }

    protected HeapWalkerNode[] getNodes(List<FieldValue> fields, HeapWalkerNode parent, Heap heap, String viewID, HeapWalkerNodeFilter viewFilter, List<DataType> dataTypes, List<SortOrder> sortOrders, Progress progress) {
        return RAttributesProvider.getNodes(fields, parent, heap, viewID, viewFilter, dataTypes, sortOrders, progress);
    }

    protected void nodeSelected(HeapWalkerNode node, boolean adjusting) {
        RObject selectedObject = node == null ? null : HeapWalkerNode.getValue(node, RObject.DATA_TYPE, heap);
        if (Objects.equals(selected, selectedObject)) {
            return;
        }

        selected = selectedObject;

        objectsView.reloadView();
    }

    @ServiceProvider(service = HeapViewPlugin.Provider.class, position = 100)
    public static class Provider extends HeapViewPlugin.Provider {

        public HeapViewPlugin createPlugin(HeapContext context, HeapWalkerActions actions, String viewID) {
            if (RHeapFragment.isRHeap(context)) {
                return new RAttributesPlugin(context, actions);
            }
            return null;
        }
    }
}
