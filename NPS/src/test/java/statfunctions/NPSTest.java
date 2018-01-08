package statfunctions;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class NPSTest
{
    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()

            // This is the function we want to test
            .withFunction( NPS.class );

    @Test
    public void shouldAllowIndexingAndFindingANode() throws Throwable
    {
        // This is in a try-block, to make sure we close the driver after the test
        try( Driver driver = GraphDatabase
                .driver( neo4j.boltURI() , Config.build().withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() ) )
        {
            // Given
            Session session = driver.session();

            // When
            Value result1 = session.run( "RETURN statfunctions.NPS([1,2,3,9,0,5,3,7,8,10], [0,6] , [7,8] , [9,10] ) AS result").single().get("result");

            // Then
            assertThat( result1.get("NPS").asDouble(), equalTo(-40.0));

        }
    }
}