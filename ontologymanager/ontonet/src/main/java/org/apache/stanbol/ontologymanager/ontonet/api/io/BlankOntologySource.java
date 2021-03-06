/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.ontologymanager.ontonet.api.io;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * A utility input source that contains an unnamed, empty ontology. An example usage of this class is to avoid
 * a {@link NullPointerException} to be thrown when an {@link OntologyInputSource} is to be passed to a
 * method, but we are not actually interested in the ontology to pass.
 * 
 * @author alexdma
 * 
 */
public class BlankOntologySource extends AbstractOWLOntologyInputSource {

    /**
     * Creates a new input source with an unnamed, empty ontology.
     */
    public BlankOntologySource() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        bindTriplesProvider(manager);
        try {
            bindRootOntology(manager.createOntology());
        } catch (OWLOntologyCreationException e) {
            bindRootOntology(null);
        }
        bindPhysicalIri(null);
    }

    @Override
    public String toString() {
        return "";
    }

}
