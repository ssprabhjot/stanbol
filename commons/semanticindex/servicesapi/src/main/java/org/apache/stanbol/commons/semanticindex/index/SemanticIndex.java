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
package org.apache.stanbol.commons.semanticindex.index;

import java.util.List;
import java.util.Map;

import org.apache.stanbol.commons.semanticindex.store.ChangeSet;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * A Semantic Index for Items
 * 
 * @param <Item>
 */
public interface SemanticIndex<Item> {
    /**
     * Name property for a SemanticIndex
     */
    public static final String PROP_NAME = "Semantic-Index-Name";

    /**
     * Description property for a SemanticIndex
     */
    public static final String PROP_DESCRIPTION = "Semantic-Index-Description";

    /**
     * Revision property for a Semantic Index. With this property, the last persisted revision of a Semantic
     * Index is indicated.
     */
    public static final String PROP_REVISION = "Semantic-Index-Revision";

    /**
     * Epoch property for a Semantic Index. With this property, the epoch of the Semantic Index is kept. When
     * the epoch of the Indexing Source, a reindexing must be performed in the Semantic Index.
     */
    public static final String PROP_EPOCH = "Semantic-Index-Epoch";

    /**
     * State property for a Semantic Index
     */
    public static final String PROP_STATE = "Semantic-Index-State";

    /**
     * The name of the Semantic Index. The same as configured by the {@link #PROP_NAME} property in the OSGI
     * component configuration
     * 
     * @return the name;
     */
    String getName();

    /**
     * The type of Items indexed in this semantic Index
     * 
     * @return the java {@link Class} of the Items provided by this index
     */
    Class<Item> getIntdexType();

    /**
     * The description for the Semantic Index. The same as configured by the {@link #PROP_DESCRIPTION}
     * property in the OSGI component configuration
     * 
     * @return the name;
     */
    String getDescription();

    /**
     * The state of the semantic index
     */
    IndexState getState();

    /**
     * Indexes the parsed ContentItem
     * 
     * @param ci
     *            the contentItem
     * @return <code>true</true> if the ConentItem was included in the index.
     * <code>false</code> if the ContentItem was ignored (e.g. filtered based on the indexing rules).
     * @throws IndexException
     *             On any error while accessing the semantic index
     */
    boolean index(Item item) throws IndexException;

    /**
     * Removes the {@link ContentItem} with the parsed {@code uri} from this index. If the no content item
     * with the parsed uri is present in this index the call can be ignored.
     * 
     * @param uri
     *            the uri of the item to remove
     * @throws IndexException
     *             On any error while accessing the semantic index
     */
    void remove(String uri) throws IndexException;

    /**
     * Persists all changes to the index and sets the revision to the parsed value if the operation succeeds.
     * <p>
     * TODO: The {@link ChangeSet} interface does NOT provide revisions for each changed ContentItem but only
     * for the whole Set. So this means that this method can only be called after indexing the whole
     * {@link ChangeSet}. This might be OK, but needs further investigation (rwesten)
     * 
     * @param revision
     *            the revision
     * @throws IndexException
     *             On any error while accessing the semantic index
     */
    void persist(long revision) throws IndexException;

    /**
     * Getter for the current revision of this SemanticIndex
     * 
     * @return the revision number or {@link Long#MIN_VALUE} if none.
     */
    long getRevision();

    /**
     * Getter for the list of fields supported by this semantic index. This information is optional.
     * Implementations that does not support this can indicate that by returning <code>null</code>.
     * 
     * @return the list of filed names or <code>null</code> if not available
     * @throws IndexException
     */
    List<String> getFieldsNames() throws IndexException;

    /**
     * Getter for the properties describing a specific field supported by this index. Names can be retrieved
     * by using {@link #getFieldsNames()}. This information is optional. Implementations that do not support
     * this can indicate this by returning <code>null</code>.
     * <p>
     * The keys of the returned map represent the properties. Values the actual configuration of the property.
     * 
     * @param name
     *            the field name
     * @return the field properties or <code>null</code> if not available.
     * @throws IndexException
     */
    Map<String,Object> getFieldProperties(String name) throws IndexException;

    /**
     * Getter for the RESTful search interfaces supported by this semantic index. The keys represent the types
     * of the RESTful Interfaces. See the {@link EndpointTypeEnum} enumeration for knows keys. The value is
     * the URL of that service relative to to the Stanbol base URI
     * 
     * @return the RESTful search interfaces supported by this semantic index.
     */
    Map<String,String> getRESTSearchEndpoints();

    /**
     * Getter for the Java search APIs supported by this semantic index. The keys are the java interfaces and
     * values are OSGI {@link ServiceReference}s. This also means that instead of using this method such
     * components can be accesses by using a filter on
     * <ul>
     * <li>{@link #PROP_NAME} = {@link #getName()}
     * <li> {@link Constants#OBJECTCLASS} = {@link Class#getName()}
     * </ul>
     * 
     * @return the Java search APIs supported by this semantic index. Also registered as OSGI services. The
     *         key is equals to the {@link Class#getName()} and {@link Constants#OBJECTCLASS}.
     */
    Map<String,ServiceReference> getSearchEndPoints();
}