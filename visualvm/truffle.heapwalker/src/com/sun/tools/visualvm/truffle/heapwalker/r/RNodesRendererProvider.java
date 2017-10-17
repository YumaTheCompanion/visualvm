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

import java.util.Map;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.modules.profiler.heapwalker.v2.HeapContext;
import org.netbeans.modules.profiler.heapwalker.v2.java.InstanceReferenceNode;
import org.netbeans.modules.profiler.heapwalker.v2.java.InstanceReferenceNodeRenderer;
import org.netbeans.modules.profiler.heapwalker.v2.java.PrimitiveNode;
import org.netbeans.modules.profiler.heapwalker.v2.java.PrimitiveNodeRenderer;
import org.netbeans.modules.profiler.heapwalker.v2.model.HeapWalkerNode;
import org.netbeans.modules.profiler.heapwalker.v2.ui.HeapWalkerRenderer;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jiri Sedlacek
 */
@ServiceProvider(service=HeapWalkerRenderer.Provider.class)
public class RNodesRendererProvider extends HeapWalkerRenderer.Provider {

    public void registerRenderers(Map<Class<? extends HeapWalkerNode>, HeapWalkerRenderer> renderers, HeapContext context) {
        Heap heap = context.getFragment().getHeap();
        
        renderers.put(RObjectNode.class, new RObjectNode.Renderer(heap));
        
        renderers.put(RObjectsContainer.class, new RObjectsContainer.Renderer());
        
        renderers.put(RObjectFieldNode.class, new RObjectFieldNode.Renderer(heap));
        
        renderers.put(RObjectReferenceNode.class, new RObjectReferenceNode.Renderer(heap));
        
        
        // --- reused from Java ------------------------------------------------
        
        // primitive fields & items
        renderers.put(PrimitiveNode.class, new PrimitiveNodeRenderer());
        
        // object fields & items
        renderers.put(InstanceReferenceNode.class, new InstanceReferenceNodeRenderer(heap)); 
    }
    
}