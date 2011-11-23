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
package org.apache.stanbol.commons.solr.managed;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.stanbol.commons.solr.managed.ManagedSolrServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Tests the standalone implemention - running outside an OSGI environment - of
 * the {@link ManagedSolrServer} interface.
 * @author Rupert Westenthaler
 *
 */
public class ManagedSolrServerTest {

    private static final Logger log = LoggerFactory.getLogger(ManagedSolrServerTest.class);

    public static final String TEST_SOLR_CONFIG_NAME = "test";
    public static final String TEST_YARD_ID = "testYard";
    public static final String TEST_SOLR_CORE_NAME = "test";
    protected static final String TEST_INDEX_REL_PATH = File.separatorChar + "target" + File.separatorChar
                                                        + ManagedSolrServer.DEFAULT_SOLR_DATA_DIR;

    private static ManagedSolrServer managedSolrServer;

    private static File expectedManagedDirectory;
    private static Collection<String> expectedIndexNames = Arrays.asList("entityhub", "cache");

    @BeforeClass
    public static void init() throws IOException {
        // set to "${basedir}/some/rel/path" to test if property substitution works!
        String prefix = System.getProperty("basedir") == null ? "." : "${basedir}";
        String resolvedPrefix = System.getProperty("basedir") == null ? "." : System.getProperty("basedir");
        String solrServerDir = prefix + TEST_INDEX_REL_PATH;
        log.info("configured directory: " + solrServerDir);
        System.setProperty(ManagedSolrServer.MANAGED_SOLR_DIR_PROPERTY, solrServerDir);
        // create the SolrDirectoryManager used for the tests
        Iterator<ManagedSolrServer> providerIt = ServiceLoader.load(ManagedSolrServer.class,
            ManagedSolrServer.class.getClassLoader()).iterator();
        if (providerIt.hasNext()) {
            managedSolrServer = providerIt.next();
        } else {
            throw new IllegalStateException("Unable to instantiate "
                                            + ManagedSolrServer.class.getSimpleName()
                                            + " service by using " + ServiceLoader.class.getName() + "!");
        }
        expectedManagedDirectory = new File(resolvedPrefix, 
            TEST_INDEX_REL_PATH+File.separatorChar+managedSolrServer.getServerName());
        // store the expected managed directory for later testing
        expectedManagedDirectory = expectedManagedDirectory.getAbsoluteFile().getCanonicalFile();
        log.info("expected managed directory: " + expectedManagedDirectory);
        //setup the entityhub and the cache index (as it would be done by the Entityhub)
        //to test this initialisation
        managedSolrServer.createSolrIndex("entityhub", TEST_SOLR_CONFIG_NAME, null);
        //for the cahce we use the default configuration
        managedSolrServer.createSolrIndex("cache", TEST_SOLR_CONFIG_NAME, null);
    }

    @Test
    public void testManagedDirectoryInitialisation() {
        // the managed directory must be set based on the
        expectedManagedDirectory.equals(managedSolrServer.getManagedDirectory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullIndexName() {
        File file = managedSolrServer.getSolrIndexDirectory(null);
        log.warn("Directory for NULL core: {}",file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyIndexName() {
        managedSolrServer.getSolrIndexDirectory("");
    }

    @Test
    public void testGetManagedIndexes() {
        Set<String> expected = new HashSet<String>(expectedIndexNames);
        Collection<IndexMetadata> present = managedSolrServer.getIndexes(ManagedIndexState.ACTIVE);
        Collection<String> presentNames = new ArrayList<String>();
        for (IndexMetadata metadata : present) {
            presentNames.add(metadata.getIndexName());
            assertTrue(expected.remove(metadata.getIndexName()));
            // test that the index dir is the expected location
            File expectedLocation = new File(expectedManagedDirectory, metadata.getIndexName()+File.separatorChar);
            assertEquals(expectedLocation, new File(metadata.getDirectory()));
        }
        // test that the expected indexes where returned
        assertTrue("The expected Indexes "+expected+" are not present (present: "+
            presentNames+")!",expected.isEmpty());
    }

    @Test
    public void testIsManagedIndex() {
        for (String name : expectedIndexNames) {
            assertTrue(managedSolrServer.isManagedIndex(name));
        }
        assertFalse(managedSolrServer.isManagedIndex("notAnIndex" + System.currentTimeMillis()));
    }

    @Test
    public void testDefaultIndexInitialisation() throws IOException {
        // this is actually tested already by the initialisation of the
        // SolrYardTest ...
        String indexName = "testIndexInitialisation_" + System.currentTimeMillis();
        IndexMetadata metadata = managedSolrServer.createSolrIndex(indexName, 
            TEST_SOLR_CONFIG_NAME, null);
        Assert.assertNotNull("The metadata returned after creating index "+
            indexName+" MUST NOT be NULL!",metadata);
        File indexDir = new File(metadata.getDirectory());
        assertEquals(new File(expectedManagedDirectory, indexName), indexDir);
        assertTrue(indexDir.isDirectory());
    }
}
