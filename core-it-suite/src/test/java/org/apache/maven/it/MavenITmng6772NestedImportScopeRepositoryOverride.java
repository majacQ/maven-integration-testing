package org.apache.maven.it;

import java.io.File;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.util.ResourceExtractor;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-6772">MNG-6772</a>:
 *
 * The test POM references an import scope POM, which also has a dependency on an import scope POM.
 *
 * Both import POMs can only be found in the repository defined in the test POM. 
 * It has a parent POM that defines the same repository with a different location.
 * The test confirms that the dominant repository definition (child) wins while resolving the import POMs.
 *
 */
public class MavenITmng6772NestedImportScopeRepositoryOverride
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng6772NestedImportScopeRepositoryOverride()
    {
        super( "(3.6.2,)" );
    }

    // This will test the behavior using ProjectModelResolver
    public void testitInProject()
        throws Exception
    {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-6772-override-in-project" );

        final Verifier verifier = newVerifier( testDir.getAbsolutePath(), null );
        overrideGlobalSettings( testDir, verifier );
        verifier.deleteArtifacts( "org.apache.maven.its.mng6772" );

        verifier.filterFile( "pom-template.xml", "pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );

        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }
    
    // This will test the behavior using DefaultModelResolver
    public void testitInDependency()
        throws Exception
    {
        final File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-6772-override-in-dependency" );

        final Verifier verifier = newVerifier( testDir.getAbsolutePath(), null );
        overrideGlobalSettings( testDir, verifier );
        verifier.deleteArtifacts( "org.apache.maven.its.mng6772" );

        verifier.filterFile( "pom-template.xml", "pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );

        verifier.executeGoal( "compile" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }
    
    // central must not be defined in any settings.xml or super POM will never be in play.
    private void overrideGlobalSettings( final File testDir, final Verifier verifier )
    {
        final File settingsFile = new File( testDir, "settings-override.xml" );
        final String path = settingsFile.getAbsolutePath();
        verifier.getCliOptions().add( "--global-settings" );
        if ( path.indexOf( ' ' ) < 0 )
        {
            verifier.getCliOptions().add( path );
        }
        else
        {
            verifier.getCliOptions().add( '"' + path + '"' );
        }
    }

}
